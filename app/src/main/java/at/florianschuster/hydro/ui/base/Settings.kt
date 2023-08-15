package at.florianschuster.hydro.ui.base

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun SettingsSection(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier.padding(vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (title != null) {
                SettingTitle(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    name = title
                )
            }
            content()
        }
    }
}

@Composable
private fun SettingTitle(
    modifier: Modifier = Modifier,
    name: String
) {
    Text(
        modifier = modifier,
        text = name,
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
fun SettingItem(
    modifier: Modifier = Modifier,
    icon: @Composable () -> Unit,
    fieldName: String,
    value: String? = null,
    onClick: () -> Unit
) {
    HydroListItem(
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp),
        headlineContent = {
            Text(text = fieldName, style = MaterialTheme.typography.titleSmall)
        },
        supportingContent = {
            if (value != null) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        leadingContent = { icon() }
    )
}

@Composable
fun ToggleSettingItem(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    fieldName: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    HydroListItem(
        modifier = modifier.padding(horizontal = 20.dp),
        headlineContent = {
            Text(text = fieldName, style = MaterialTheme.typography.titleSmall)
        },
        leadingContent = {
            Icon(
                imageVector = icon,
                contentDescription = "set $fieldName"
            )
        },
        trailingContent = {
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    )
}
