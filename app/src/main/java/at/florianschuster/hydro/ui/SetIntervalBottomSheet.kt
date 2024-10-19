package at.florianschuster.hydro.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import at.florianschuster.hydro.AppAction
import at.florianschuster.hydro.AppState
import at.florianschuster.hydro.R
import at.florianschuster.hydro.model.format
import at.florianschuster.hydro.ui.base.BottomSheet
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.minutes

@Composable
fun SetIntervalBottomSheet(
    state: AppState,
    dispatch: (AppAction) -> Unit,
    onClose: () -> Unit
) {
    check(state.reminder != null)
    BottomSheet(
        title = "Interval"
    ) {
        var value by remember(state.reminder) {
            mutableFloatStateOf(state.reminder.interval.inWholeMinutes.toFloat())
        }
        val intervalValue by remember(value) {
            derivedStateOf { value.roundToInt().minutes }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(R.drawable.ic_arrow_range),
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(16.dp))
            Slider(
                value = value,
                valueRange = 1f..60f,
                onValueChange = { minutesValue -> value = minutesValue }
            )
        }
        Text(text = intervalValue.format())

        Spacer(modifier = Modifier.height(32.dp))
        TextButton(
            onClick = {
                dispatch(
                    AppAction.SetReminder(state.reminder.copy(interval = intervalValue))
                )
                onClose()
            }
        ) { Text("Save") }
    }
}
