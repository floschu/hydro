package at.florianschuster.hydro

import at.florianschuster.hydro.model.Cup
import at.florianschuster.hydro.model.Day
import at.florianschuster.hydro.model.LiquidUnit
import at.florianschuster.hydro.model.Milliliters
import at.florianschuster.hydro.model.Now
import at.florianschuster.hydro.model.NowInstant
import at.florianschuster.hydro.model.Percent
import at.florianschuster.hydro.model.Reminder
import at.florianschuster.hydro.model.Theme
import at.florianschuster.hydro.model.Today
import at.florianschuster.hydro.model.defaultCups
import at.florianschuster.hydro.model.defaultSelectedCups
import at.florianschuster.hydro.model.reachedGoal
import at.florianschuster.hydro.model.sumOfMilliliters
import at.florianschuster.hydro.model.times
import at.florianschuster.hydro.service.DateChangedService
import at.florianschuster.hydro.service.HydrationHistoryStore
import at.florianschuster.hydro.service.NotificationService
import at.florianschuster.hydro.service.PreferencesStore
import at.florianschuster.hydro.service.ReminderAlarmService
import at.florianschuster.hydro.service.hasCelebratedGoalToday
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant

data class AppState(
    val isDebug: Boolean,
    val dailyGoal: Milliliters,
    val todayHydration: Milliliters,
    val reminder: Reminder?,
    val theme: Theme,
    val canScheduleAlarms: Boolean,
    val defaultCups: List<Cup>,
    val selectedCups: List<Cup>,
    val appInForeground: Boolean,
    val liquidUnit: LiquidUnit,
    val onboardingShown: Boolean
) {
    val hydrationProgress: Percent = Percent(
        todayHydration.value / dailyGoal.value.toFloat()
    )
    val dailyGoalReached: Boolean = hydrationProgress.value >= 1f
    val allCups = (defaultCups + selectedCups).distinct().sorted()
}

sealed interface AppAction {
    data class SetDailyGoal(val value: Milliliters) : AppAction
    data class AddHydration(val value: Milliliters) : AppAction
    data class SetReminder(val value: Reminder?) : AppAction
    data object RestartReminder : AppAction
    data class ShowHydrationReminderNotification(val forced: Boolean = false) : AppAction
    data class SetTheme(val value: Theme) : AppAction
    data class SetSelectedCups(val value: List<Cup>) : AppAction
    data class SetAppInForeground(val value: Boolean) : AppAction
    data class SetLiquidUnit(val value: LiquidUnit) : AppAction
    data object SetHydrationForOnboarding : AppAction
    data object SetOnboardingShown : AppAction
    data object DeleteAll : AppAction
    data object ResetToday : AppAction
}

class AppStore(
    private val hydrationHistoryStore: HydrationHistoryStore,
    isDebug: Boolean,
    private val notificationService: NotificationService,
    private val preferencesStore: PreferencesStore,
    private val reminderAlarmService: ReminderAlarmService,
    private val scope: CoroutineScope,
    dateChangedService: DateChangedService
) {
    private val _state = MutableStateFlow(
        kotlin.run {
            val liquidUnit = runBlocking { preferencesStore.liquidUnit.first() }
            AppState(
                isDebug = isDebug,
                dailyGoal = runBlocking {
                    preferencesStore.dailyGoal.first() ?: Milliliters.DAILY_GOAL_DEFAULT
                },
                todayHydration = runBlocking {
                    hydrationHistoryStore.day(Today).first()?.hydration?.sumOfMilliliters()
                        ?: Milliliters.ZERO
                },
                reminder = runBlocking { preferencesStore.reminder.first() },
                theme = runBlocking { preferencesStore.theme.first() },
                canScheduleAlarms = reminderAlarmService.canScheduleAlarms.value,
                liquidUnit = liquidUnit,
                defaultCups = defaultCups(liquidUnit),
                selectedCups = runBlocking {
                    preferencesStore.selectedCups.first().sorted()
                        .ifEmpty { defaultSelectedCups(liquidUnit) }
                },
                appInForeground = true,
                onboardingShown = runBlocking { preferencesStore.onboardingShown.first() }
            )
        }
    )
    val state: StateFlow<AppState> = _state.asStateFlow()

    init {
        with(preferencesStore) {
            onboardingShown.onEach { onboardingShown ->
                _state.update { it.copy(onboardingShown = onboardingShown) }
            }.launchIn(scope)
            dailyGoal.onEach { milliliters ->
                _state.update {
                    it.copy(dailyGoal = milliliters ?: Milliliters.DAILY_GOAL_DEFAULT)
                }
            }.launchIn(scope)
            reminder.onEach { reminder ->
                _state.update { it.copy(reminder = reminder) }
            }.launchIn(scope)
            theme.onEach { theme ->
                _state.update { it.copy(theme = theme) }
            }.launchIn(scope)

            combine(
                liquidUnit,
                selectedCups
            ) { liquidUnit, selectedCups -> liquidUnit to selectedCups }
                .onEach { (liquidUnit, selectedCups) ->
                    _state.update {
                        it.copy(
                            liquidUnit = liquidUnit,
                            defaultCups = defaultCups(liquidUnit),
                            selectedCups = selectedCups.sorted()
                                .ifEmpty { defaultSelectedCups(liquidUnit) }
                        )
                    }
                }
                .launchIn(scope)
        }

        dateChangedService.onChanged
            .flatMapLatest { localDate -> hydrationHistoryStore.day(localDate) }
            .onEach { day ->
                val todayHydration = day?.hydration?.sumOfMilliliters()
                    ?: Milliliters.ZERO
                _state.update { it.copy(todayHydration = todayHydration) }
            }
            .launchIn(scope)

        reminderAlarmService.canScheduleAlarms.onEach { canScheduleAlarms ->
            _state.update { it.copy(canScheduleAlarms = canScheduleAlarms) }
        }.launchIn(scope)
    }

    fun dispatch(action: AppAction) {
        when (action) {
            is AppAction.SetDailyGoal -> scope.launch {
                preferencesStore.setDailyGoal(action.value)
                val day = hydrationHistoryStore.day(Today).first()
                if (day != null) {
                    hydrationHistoryStore.setDay(day.copy(goal = action.value))
                }
            }

            is AppAction.AddHydration -> scope.launch {
                notificationService.cancelHydrationReminderNotification()
                val currentState = _state.value
                val storedDay = hydrationHistoryStore.day(Today).first()
                if (storedDay != null) {
                    val updatedDay = storedDay.copy(
                        hydration = storedDay.hydration + Day.Hydration(action.value, Now),
                        goal = currentState.dailyGoal
                    )
                    val goalReachedOnUpdatedDay = updatedDay.reachedGoal()
                    if (goalReachedOnUpdatedDay && !preferencesStore.hasCelebratedGoalToday()) {
                        if (!currentState.appInForeground) {
                            notificationService.showDailyGoalReachedNotification()
                        }
                        preferencesStore.setLastGoalCelebration(NowInstant)
                    }
                    hydrationHistoryStore.setDay(updatedDay)
                } else {
                    hydrationHistoryStore.setDay(
                        Day(
                            date = Today,
                            hydration = listOf(Day.Hydration(action.value, Now)),
                            goal = currentState.dailyGoal
                        )
                    )
                }
            }

            is AppAction.SetReminder -> scope.launch {
                if (action.value != null) {
                    reminderAlarmService.setAlarm(action.value)
                } else {
                    reminderAlarmService.clear()
                }
                preferencesStore.setReminder(action.value)
            }

            is AppAction.RestartReminder -> scope.launch {
                val reminder = preferencesStore.reminder.first()
                if (reminder != null) {
                    reminderAlarmService.setAlarm(reminder)
                }
            }

            is AppAction.ShowHydrationReminderNotification -> scope.launch {
                if (action.forced || !preferencesStore.hasCelebratedGoalToday()) {
                    val todayMilliliters = hydrationHistoryStore.day(Today).first()
                        ?.hydration
                        ?.sumOfMilliliters()
                        ?: Milliliters.ZERO
                    val liquidUnit = preferencesStore.liquidUnit.first()
                    val selectedCups = preferencesStore.selectedCups.first()
                        .ifEmpty { defaultSelectedCups(liquidUnit) }
                        .sorted()
                    notificationService.showHydrationReminderNotification(
                        todayMilliliters = todayMilliliters,
                        todayProgress = _state.value.hydrationProgress,
                        selectedCups = selectedCups,
                        liquidUnit = liquidUnit,
                    )
                }
            }

            is AppAction.SetTheme -> scope.launch {
                preferencesStore.setTheme(action.value)
            }

            is AppAction.SetSelectedCups -> scope.launch {
                preferencesStore.setSelectedCups(action.value)
            }

            is AppAction.DeleteAll -> scope.launch {
                preferencesStore.clear()
                hydrationHistoryStore.clear()
                reminderAlarmService.clear()
                notificationService.clear()
            }

            is AppAction.SetAppInForeground -> _state.update {
                it.copy(appInForeground = action.value)
            }

            is AppAction.ResetToday -> scope.launch {
                val today = hydrationHistoryStore.day(Today).first() ?: return@launch
                hydrationHistoryStore.setDay(today.copy(hydration = emptyList()))
                preferencesStore.setLastGoalCelebration(Instant.DISTANT_PAST)
            }

            is AppAction.SetLiquidUnit -> scope.launch {
                preferencesStore.setLiquidUnit(action.value)
            }

            is AppAction.SetHydrationForOnboarding -> scope.launch {
                val goal = _state.value.dailyGoal
                hydrationHistoryStore.setDay(
                    Day(
                        date = Today,
                        hydration = listOf(Day.Hydration(goal * 0.55f, Now)),
                        goal = goal
                    )
                )
            }

            is AppAction.SetOnboardingShown -> scope.launch {
                preferencesStore.setOnboardingShown(true)
                dispatch(AppAction.ResetToday)
            }
        }
    }
}
