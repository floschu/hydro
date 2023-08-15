package at.florianschuster.hydro.ui

import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import at.florianschuster.hydro.AppAction
import at.florianschuster.hydro.AppState
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
            mutableStateOf(state.reminder.interval.inWholeMinutes.toFloat())
        }
        val intervalValue by remember(value) {
            derivedStateOf { value.roundToInt().minutes }
        }
        Slider(
            value = value,
            valueRange = 1f..60f,
            steps = 58,
            onValueChange = { minutesValue -> value = minutesValue },
            onValueChangeFinished = {
                dispatch(
                    AppAction.SetReminder(state.reminder.copy(interval = intervalValue))
                )
                onClose()
            }
        )
        Text(text = intervalValue.format())
    }
}
