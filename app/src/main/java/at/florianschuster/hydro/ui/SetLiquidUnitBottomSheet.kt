package at.florianschuster.hydro.ui

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import at.florianschuster.hydro.AppAction
import at.florianschuster.hydro.AppState
import at.florianschuster.hydro.model.LiquidUnit
import at.florianschuster.hydro.model.format
import at.florianschuster.hydro.model.formatMoreInfo
import at.florianschuster.hydro.ui.base.BottomSheet
import at.florianschuster.hydro.ui.base.HydroListItem

@Composable
fun SetLiquidUnitBottomSheet(
    state: AppState,
    dispatch: (AppAction) -> Unit,
    onClose: () -> Unit
) {
    BottomSheet(
        title = "Measurement Unit"
    ) {
        LiquidUnit.entries.forEach { liquidUnit ->
            HydroListItem(
                modifier = Modifier.clickable {
                    dispatch(AppAction.SetLiquidUnit(liquidUnit))
                    onClose()
                },
                headlineContent = { Text(text = liquidUnit.format()) },
                supportingContent = { Text(text = liquidUnit.formatMoreInfo()) },
                trailingContent = {
                    if (state.liquidUnit == liquidUnit) {
                        Icon(
                            imageVector = Icons.Outlined.Check,
                            contentDescription = "selected"
                        )
                    }
                }
            )
        }
    }
}
