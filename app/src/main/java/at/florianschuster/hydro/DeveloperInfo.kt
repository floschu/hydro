package at.florianschuster.hydro

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent

private const val DEV_URL = "https://florianschuster.at/"
private const val DEV_EMAIL = "hydro.mobileapp@gmail.com"

fun Activity.openInfoCustomTab() {
    val customTab = CustomTabsIntent.Builder().apply {
        setShowTitle(true)
        setInstantAppsEnabled(true)
    }.build()
    runCatching {
        customTab.launchUrl(this, Uri.parse(DEV_URL))
    }.onFailure {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(DEV_URL))
        startActivity(intent)
    }
}

fun Activity.openFeedback() {
    try {
        startActivity(
            Intent(Intent.ACTION_SEND).apply {
                data = Uri.parse("mailto:$DEV_EMAIL")
                putExtra(Intent.EXTRA_SUBJECT, "Feedback hydro app")
            }
        )
    } catch (ex: ActivityNotFoundException) {
        Toast.makeText(
            this,
            "No email client installed.",
            Toast.LENGTH_SHORT
        ).show()
    }
}
