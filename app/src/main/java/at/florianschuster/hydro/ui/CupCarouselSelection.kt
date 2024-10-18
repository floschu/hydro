package at.florianschuster.hydro.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import at.florianschuster.hydro.AppAction
import at.florianschuster.hydro.AppState
import at.florianschuster.hydro.model.Cup
import at.florianschuster.hydro.ui.base.HydrationCarousel

@Composable
fun CupCarouselSelection(
    modifier: Modifier = Modifier,
    state: AppState,
    dispatch: (AppAction) -> Unit
) {
    Column(modifier = modifier) {
        var showCanOnlySelectThreeAlert by remember { mutableStateOf(false) }
        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = "Cups",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            modifier = Modifier.padding(horizontal = 24.dp),
            text = "Cups are displayed on your Main screen and in your Reminder notification",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        val allCupsAsMilliliters = remember(state.allCups) {
            state.allCups.map(Cup::milliliters)
        }
        val selectedCupsAsMilliliters = remember(state.selectedCups) {
            state.selectedCups.map(Cup::milliliters)
        }
        HydrationCarousel(
            contentPadding = PaddingValues(horizontal = 36.dp),
            milliliterItems = allCupsAsMilliliters,
            liquidUnit = state.liquidUnit,
            selected = selectedCupsAsMilliliters,
            onClick = { index, _ ->
                val cup = state.allCups[index]
                if (cup in state.selectedCups) {
                    dispatch(AppAction.SetSelectedCups(state.selectedCups - cup))
                } else if (state.selectedCups.count() >= 3) {
                    showCanOnlySelectThreeAlert = true
                } else {
                    dispatch(AppAction.SetSelectedCups(state.selectedCups + cup))
                }
            }
        )
        if (showCanOnlySelectThreeAlert) {
            AlertDialog(
                title = { Text(text = "Sorry") },
                text = { Text(text = "You can only select 3 different Cups.") },
                onDismissRequest = { showCanOnlySelectThreeAlert = false },
                confirmButton = {
                    Button(
                        onClick = { showCanOnlySelectThreeAlert = false },
                        content = { Text(text = "Ok") }
                    )
                }
            )
        }
    }
}
