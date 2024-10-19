package at.florianschuster.hydro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.florianschuster.hydro.model.Theme
import at.florianschuster.hydro.model.isDarkTheme
import at.florianschuster.hydro.ui.GoalOfTheDayBottomSheet
import at.florianschuster.hydro.ui.HistoryScreen
import at.florianschuster.hydro.ui.HistoryScreenToolbar
import at.florianschuster.hydro.ui.OnboardingScreen
import at.florianschuster.hydro.ui.SetIntervalBottomSheet
import at.florianschuster.hydro.ui.SetLiquidUnitBottomSheet
import at.florianschuster.hydro.ui.SettingsScreen
import at.florianschuster.hydro.ui.SettingsToolbar
import at.florianschuster.hydro.ui.ThemeBottomSheet
import at.florianschuster.hydro.ui.TodayScreen
import at.florianschuster.hydro.ui.TodayToolbar
import at.florianschuster.hydro.ui.base.HydrationBackground
import at.florianschuster.hydro.ui.base.HydroTheme
import dev.olshevski.navigation.reimagined.AnimatedNavHost
import dev.olshevski.navigation.reimagined.NavAction
import dev.olshevski.navigation.reimagined.NavBackHandler
import dev.olshevski.navigation.reimagined.material3.BottomSheetNavHost
import dev.olshevski.navigation.reimagined.navigate
import dev.olshevski.navigation.reimagined.pop
import dev.olshevski.navigation.reimagined.rememberNavController

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val store = App.instance.store
        setContent {
            val state by store.state.collectAsStateWithLifecycle()
            HydroTheme(
                darkTheme = state.theme.isDarkTheme(),
                dynamicColor = state.theme == Theme.Dynamic
            ) {
                AppScreen(
                    state = state,
                    dispatch = store::dispatch,
                    onShowDeveloperInfo = { openInfoCustomTab() },
                    onWriteDeveloper = { openFeedback() },
                )
            }
        }
    }
}

@Composable
private fun AppScreen(
    state: AppState,
    dispatch: (AppAction) -> Unit,
    onShowDeveloperInfo: () -> Unit,
    onWriteDeveloper: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        HydrationBackground(
            isSystemInDarkTheme = state.theme.isDarkTheme(),
            hydrationProgress = state.hydrationProgress
        )

        val navController = rememberNavController(
            startDestination = if (state.onboardingShown) {
                Screen.Today
            } else {
                Screen.Onboarding
            }
        )
        val sheetController = rememberNavController<Sheet>(
            initialBackstack = emptyList()
        )

        NavBackHandler(navController)
        AnimatedNavHost(
            controller = navController,
            transitionSpec = { action, _, _ ->
                when (action) {
                    is NavAction.Pop -> {
                        fadeIn() togetherWith slideOutVertically(
                            targetOffsetY = { fullHeight -> fullHeight }
                        ) + fadeOut()
                    }

                    is NavAction.Navigate -> {
                        (
                                slideInVertically(
                                    initialOffsetY = { fullHeight -> fullHeight }
                                ) + fadeIn()
                                ) togetherWith fadeOut()
                    }

                    else -> error("no transition defined for $action")
                }
            }
        ) { screen ->
            Scaffold(
                topBar = {
                    when (screen) {
                        is Screen.Onboarding -> {
                            // no toolbar
                        }

                        is Screen.Today -> TodayToolbar(
                            isDebug = state.isDebug,
                            onGoToSettings = {
                                navController.navigate(Screen.Settings)
                            },
                            onGoToHistory = {
                                navController.navigate(Screen.History)
                            }
                        )

                        is Screen.Settings -> SettingsToolbar(
                            onGoBack = navController::pop
                        )

                        is Screen.History -> HistoryScreenToolbar(
                            onGoBack = navController::pop
                        )
                    }
                },
                containerColor = Color.Transparent
            ) { contentPadding ->
                when (screen) {
                    is Screen.Onboarding -> OnboardingScreen(
                        contentPadding = contentPadding,
                        dispatch = dispatch,
                        onOnboardingFinished = {
                            navController.navigate(
                                listOf(Screen.Today, Screen.Settings)
                            )
                        }
                    )

                    is Screen.Today -> TodayScreen(
                        contentPadding = contentPadding,
                        state = state,
                        dispatch = dispatch
                    )

                    is Screen.Settings -> SettingsScreen(
                        contentPadding = contentPadding,
                        state = state,
                        dispatch = dispatch,
                        onSetGoalOfTheDay = { sheetController.navigate(Sheet.GoalOfTheDay) },
                        onSetLiquidUnit = { sheetController.navigate(Sheet.LiquidUnit) },
                        onSetTheme = { sheetController.navigate(Sheet.SetTheme) },
                        onShowDeveloperInfo = onShowDeveloperInfo,
                        onWriteDeveloper = onWriteDeveloper,
                        onSetInterval = { sheetController.navigate(Sheet.SetInterval) }
                    )

                    is Screen.History -> HistoryScreen(
                        contentPadding = contentPadding,
                        appState = state
                    )
                }
            }
        }

        BackHandler(
            enabled = sheetController.backstack.entries.isNotEmpty(),
            onBack = sheetController::pop
        )
        BottomSheetNavHost(
            controller = sheetController,
            onDismissRequest = sheetController::pop,
            scrimColor = Color.Black.copy(alpha = 0.75f)
        ) { sheet ->
            when (sheet) {
                is Sheet.GoalOfTheDay -> GoalOfTheDayBottomSheet(
                    state = state,
                    dispatch = dispatch
                )

                is Sheet.SetTheme -> ThemeBottomSheet(
                    state = state,
                    dispatch = dispatch,
                    onClose = sheetController::pop
                )

                is Sheet.SetInterval -> SetIntervalBottomSheet(
                    state = state,
                    dispatch = dispatch,
                    onClose = sheetController::pop
                )

                is Sheet.LiquidUnit -> SetLiquidUnitBottomSheet(
                    state = state,
                    dispatch = dispatch,
                    onClose = sheetController::pop
                )
            }
        }
    }
}
