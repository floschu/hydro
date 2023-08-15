package at.florianschuster.hydro

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import at.florianschuster.hydro.service.AndroidLocaleChangedService
import at.florianschuster.hydro.service.Channel
import at.florianschuster.hydro.service.HydrationHistoryStore
import at.florianschuster.hydro.service.NotificationService
import at.florianschuster.hydro.service.PreferencesStore
import at.florianschuster.hydro.service.ReminderAlarmService
import at.florianschuster.hydro.service.SqliteHydrationHistoryStore

class App : Application() {

    lateinit var store: AppStore
    lateinit var hydrationHistoryStore: HydrationHistoryStore

    override fun onCreate() {
        super.onCreate()

        instance = this

        Channel.registerAll(this)

        val processLifecycleOwner = ProcessLifecycleOwner.get()
        hydrationHistoryStore = SqliteHydrationHistoryStore(
            context = applicationContext,
            isDebug = isDebug
        )
        val preferencesStore = PreferencesStore(
            context = applicationContext
        )
        val notificationService = NotificationService(
            context = applicationContext
        )
        val reminderAlarmService = ReminderAlarmService(
            context = applicationContext,
            preferencesStore = preferencesStore,
            processLifecycleOwner.lifecycleScope
        )
        val localeChangedService = AndroidLocaleChangedService(
            context = applicationContext
        )
        store = AppStore(
            isDebug = isDebug,
            scope = processLifecycleOwner.lifecycleScope,
            preferencesStore = preferencesStore,
            hydrationHistoryStore = hydrationHistoryStore,
            reminderAlarmService = reminderAlarmService,
            notificationService = notificationService,
            localeChangedService = localeChangedService
        )

        processLifecycleOwner.lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onResume(owner: LifecycleOwner) {
                    store.dispatch(AppAction.SetAppInForeground(true))
                }

                override fun onPause(owner: LifecycleOwner) {
                    store.dispatch(AppAction.SetAppInForeground(false))
                }
            }
        )
    }

    companion object {
        private val isDebug = BuildConfig.DEBUG
        lateinit var instance: App
    }
}
