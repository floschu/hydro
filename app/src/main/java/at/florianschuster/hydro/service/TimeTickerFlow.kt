package at.florianschuster.hydro.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import at.florianschuster.hydro.model.Today
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.isActive
import kotlinx.datetime.LocalDate
import kotlin.time.Duration.Companion.seconds

interface DateChangedService {
    val onChanged: Flow<LocalDate>
}

class AndroidDateChangedService(
    private val context: Context
) : DateChangedService {

    override val onChanged: Flow<LocalDate> = merge(
        localDateChangeTickerFlow(),
        timeZoneOrTimeChangedFlow()
    )
        .onStart { emit(Unit) }
        .map { Today }
        .distinctUntilChanged()

    private fun localDateChangeTickerFlow() = flow {
        var currentDay = Today
        while (currentCoroutineContext().isActive) {
            delay(5.seconds)
            if (currentDay != Today) {
                currentDay = Today
                emit(Unit)
            }
        }
    }

    private fun timeZoneOrTimeChangedFlow() = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                trySend(Unit)
            }
        }
        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(Intent.ACTION_TIMEZONE_CHANGED),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        awaitClose { context.unregisterReceiver(receiver) }
    }
}
