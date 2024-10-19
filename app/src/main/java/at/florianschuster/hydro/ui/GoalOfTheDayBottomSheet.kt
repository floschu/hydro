package at.florianschuster.hydro.ui

import android.graphics.Paint.Align
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import at.florianschuster.hydro.AppAction
import at.florianschuster.hydro.AppState
import at.florianschuster.hydro.model.Milliliters
import at.florianschuster.hydro.model.format
import at.florianschuster.hydro.ui.base.BottomSheet
import at.florianschuster.hydro.ui.base.HydroTheme
import kotlin.math.roundToInt

@Composable
fun GoalOfTheDayBottomSheet(
    state: AppState,
    dispatch: (AppAction) -> Unit
) {
    BottomSheet(
        title = "Goal of the Day"
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Outlined.WaterDrop,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(16.dp))
            MillilitersSlider(
                range = remember { Milliliters.DAILY_GOAL_MIN..Milliliters.DAILY_GOAL_MAX },
                stepsSize = remember { Milliliters.DAILY_GOAL_STEPS },
                milliliters = state.dailyGoal,
                onMillilitersChanged = { dispatch(AppAction.SetDailyGoal(it)) }
            )
        }
        Text(text = state.dailyGoal.format(state.liquidUnit))
    }
}

@Composable
private fun MillilitersSlider(
    range: ClosedRange<Milliliters>,
    stepsSize: Milliliters,
    milliliters: Milliliters,
    onMillilitersChanged: (Milliliters) -> Unit
) {
    val valueRange = remember(range) {
        range.start.value.toFloat()..range.endInclusive.value.toFloat()
    }
    val steps = remember(valueRange) {
        val itemCount = (valueRange.endInclusive - valueRange.start).roundToInt()
        if (stepsSize.value == 1) 0 else (itemCount / stepsSize.value - 1)
    }
    Slider(
        value = milliliters.value.toFloat(),
        valueRange = valueRange,
        steps = steps,
        onValueChange = { onMillilitersChanged(Milliliters(it.roundToInt())) },
        colors = SliderDefaults.colors(
            activeTickColor = Color.Transparent,
            inactiveTickColor = Color.Transparent
        )
    )
}

@Preview
@Composable
fun MillilitersSliderPreviewDark() {
    HydroTheme(darkTheme = true, dynamicColor = false) {
        MillilitersSlider(
            range = Milliliters(0)..Milliliters(2000),
            stepsSize = Milliliters(100),
            milliliters = Milliliters(1000),
            onMillilitersChanged = {}
        )
    }
}

@Preview
@Composable
fun MillilitersSliderPreviewLight() {
    HydroTheme(darkTheme = false, dynamicColor = false) {
        MillilitersSlider(
            range = Milliliters(0)..Milliliters(2000),
            stepsSize = Milliliters(100),
            milliliters = Milliliters(1000),
            onMillilitersChanged = {}
        )
    }
}
