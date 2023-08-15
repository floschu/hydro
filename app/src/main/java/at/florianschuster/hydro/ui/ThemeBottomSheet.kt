package at.florianschuster.hydro.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import at.florianschuster.hydro.AppAction
import at.florianschuster.hydro.AppState
import at.florianschuster.hydro.model.Theme
import at.florianschuster.hydro.model.format
import at.florianschuster.hydro.model.icon
import at.florianschuster.hydro.ui.base.BottomSheet
import at.florianschuster.hydro.ui.base.HydroListItem

@Composable
fun ThemeBottomSheet(
    state: AppState,
    dispatch: (AppAction) -> Unit,
    onClose: () -> Unit
) {
    BottomSheet(
        title = "Theme"
    ) {
        Theme.entries.forEach { theme ->
            HydroListItem(
                modifier = Modifier.clickable {
                    dispatch(AppAction.SetTheme(theme))
                    onClose()
                },
                headlineContent = { Text(text = theme.format()) },
                supportingContent = {
                    if (theme == Theme.System) {
                        Text(
                            text = "Current system theme is " +
                                "${if (isSystemInDarkTheme()) "dark" else "light"}."
                        )
                    }
                    if (theme == Theme.Dynamic) {
                        Text(
                            text = "Uses your phone's wallpaper to determine colors."
                        )
                    }
                },
                leadingContent = {
                    Icon(
                        imageVector = theme.icon(),
                        contentDescription = "system theme"
                    )
                },
                trailingContent = {
                    if (state.theme == theme) {
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
