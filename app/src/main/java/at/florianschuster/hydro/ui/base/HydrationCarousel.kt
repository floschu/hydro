package at.florianschuster.hydro.ui.base

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import at.florianschuster.hydro.model.LiquidUnit
import at.florianschuster.hydro.model.Milliliters
import at.florianschuster.hydro.model.format
import at.florianschuster.hydro.model.icon

@Composable
fun HydrationCarousel(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    milliliterItems: List<Milliliters>,
    liquidUnit: LiquidUnit,
    selected: List<Milliliters> = emptyList(),
    onClick: (index: Int, Milliliters) -> Unit = { _, _ -> },
    contentBelowItem: @Composable (index: Int) -> Unit = {}
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = contentPadding
    ) {
        itemsIndexed(items = milliliterItems) { index, item ->
            CupItem(
                item = item,
                index = index,
                liquidUnit = liquidUnit,
                selected = item in selected,
                onClick = onClick,
                contentBelowItem = contentBelowItem
            )
        }
    }
}

@Composable
private fun CupItem(
    modifier: Modifier = Modifier,
    item: Milliliters,
    index: Int,
    liquidUnit: LiquidUnit,
    selected: Boolean,
    onClick: (index: Int, Milliliters) -> Unit,
    contentBelowItem: @Composable (index: Int) -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedCard(
            modifier = modifier,
            colors = CardDefaults.outlinedCardColors(
                containerColor = if (selected) {
                    MaterialTheme.colorScheme.surfaceColorAtElevation(24.dp)
                } else {
                    MaterialTheme.colorScheme.surface
                }
            ),
            border = if (selected) {
                CardDefaults
                    .outlinedCardBorder()
                    .copy(brush = SolidColor(MaterialTheme.colorScheme.primary))
            } else {
                CardDefaults.outlinedCardBorder(false)
            }
        ) {
            Column(
                modifier = Modifier
                    .clickable { onClick(index, item) }
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    modifier = Modifier.size(28.dp),
                    painter = item.icon(),
                    contentDescription = null
                )
                Text(
                    text = item.format(liquidUnit),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        contentBelowItem(index)
    }
}
