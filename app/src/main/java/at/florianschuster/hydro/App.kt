package at.florianschuster.hydro

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import at.florianschuster.hydro.service.AndroidDateChangedService
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
            isDebug = IS_DEBUG
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
        val dateChangedService = AndroidDateChangedService(
            context = applicationContext
        )
        store = AppStore(
            isDebug = IS_DEBUG,
            scope = processLifecycleOwner.lifecycleScope,
            preferencesStore = preferencesStore,
            hydrationHistoryStore = hydrationHistoryStore,
            reminderAlarmService = reminderAlarmService,
            notificationService = notificationService,
            dateChangedService = dateChangedService
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
        private const val IS_DEBUG = BuildConfig.DEBUG
        lateinit var instance: App
    }
}
