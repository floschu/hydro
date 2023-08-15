package at.florianschuster.hydro.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.util.Locale

interface LocaleChangedService {
    val onChanged: Flow<Locale>
}

class AndroidLocaleChangedService(
    context: Context
) : LocaleChangedService {

    override val onChanged: Flow<Locale> = callbackFlow {
        trySend(context.getMainLocale())
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == Intent.ACTION_LOCALE_CHANGED) {
                    trySend(context.getMainLocale())
                }
            }
        }
        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(Intent.ACTION_LOCALE_CHANGED),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
        awaitClose { context.unregisterReceiver(receiver) }
    }

    private fun Context.getMainLocale(): Locale = resources.configuration.locales.get(0)
}
