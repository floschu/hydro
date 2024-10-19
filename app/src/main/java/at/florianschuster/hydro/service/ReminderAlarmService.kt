package at.florianschuster.hydro.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import at.florianschuster.hydro.App
import at.florianschuster.hydro.AppAction
import at.florianschuster.hydro.model.Now
import at.florianschuster.hydro.model.Reminder
import at.florianschuster.hydro.model.Today
import at.florianschuster.hydro.model.UserTimeZone
import at.florianschuster.hydro.model.calculateReminderTimes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant

class ReminderAlarmService(
    private val context: Context,
    private val preferencesStore: PreferencesStore,
    scope: CoroutineScope
) {

    private val alarmManager: AlarmManager = checkNotNull(context.getSystemService())

    private val canScheduleAlarmsNow: Boolean
        get() = alarmManager.canScheduleExactAlarms()

    val canScheduleAlarms: StateFlow<Boolean> = callbackFlow {
        send(canScheduleAlarmsNow)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                trySend(canScheduleAlarmsNow)
            }
        }
        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        awaitClose { context.unregisterReceiver(receiver) }
    }.stateIn(scope, SharingStarted.Eagerly, canScheduleAlarmsNow)

    suspend fun setAlarm(reminder: Reminder) {
        clear()
        check(canScheduleAlarms.value) { "cannot schedule exact reminders without permission." }
        reminder.calculateReminderTimes().forEach { time ->
            val alarmTime = if (time <= Now) {
                LocalDateTime(Today + DatePeriod(days = 1), time)
            } else {
                LocalDateTime(Today, time)
            }.toInstant(UserTimeZone)
            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                alarmTime.toEpochMilliseconds(),
                AlarmManager.INTERVAL_DAY,
                alarmPendingIntent(time)
            )
        }
    }

    suspend fun clear() {
        val reminder = preferencesStore.reminder.first() ?: return
        reminder.calculateReminderTimes().forEach { time ->
            alarmManager.cancel(alarmPendingIntent(time))
        }
    }

    private fun alarmPendingIntent(time: LocalTime): PendingIntent {
        return PendingIntent.getBroadcast(
            context,
            time.toMillisecondOfDay(),
            Intent(context, ReminderShowNotificationReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
    }
}

class ReminderShowNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        App.instance.store.dispatch(AppAction.ShowHydrationReminderNotification())
    }
}

class ReminderBootCompleteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        App.instance.store.dispatch(AppAction.RestartReminder)
    }
}
