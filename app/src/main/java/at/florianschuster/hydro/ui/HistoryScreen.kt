package at.florianschuster.hydro.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import at.florianschuster.hydro.App
import at.florianschuster.hydro.AppState
import at.florianschuster.hydro.model.Day
import at.florianschuster.hydro.model.LiquidUnit
import at.florianschuster.hydro.model.Today
import at.florianschuster.hydro.model.format
import at.florianschuster.hydro.model.reachedGoal
import at.florianschuster.hydro.model.sumOfMilliliters
import at.florianschuster.hydro.service.HydrationHistoryStore
import at.florianschuster.hydro.ui.HistoryScreenStore.Companion.LOAD_MORE_THRESHOLD
import at.florianschuster.hydro.ui.base.HydrationCarousel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

@Composable
fun HistoryScreen(
    contentPadding: PaddingValues,
    appState: AppState,
    historyScreenStore: HistoryScreenStore = remember(appState) {
        HistoryScreenStore(
            hydrationHistoryStore = App.instance.hydrationHistoryStore
        )
    }
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val state by historyScreenStore.state.collectAsStateWithLifecycle()

        val lazyColumnState = rememberLazyListState()
        val shouldLoadNext by remember {
            derivedStateOf {
                if (!historyScreenStore.state.value.canLoadNext) return@derivedStateOf false
                val lastVisibleItemPosition = lazyColumnState.layoutInfo.visibleItemsInfo
                    .lastOrNull()
                    ?.index ?: 0
                val itemCount = lazyColumnState.layoutInfo.totalItemsCount + 1 // loading
                (lastVisibleItemPosition + LOAD_MORE_THRESHOLD) > itemCount
            }
        }
        LaunchedEffect(shouldLoadNext) {
            if (shouldLoadNext) {
                historyScreenStore.loadNext()
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = contentPadding,
            state = lazyColumnState,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.days, key = Day::id) { day ->
                DayItem(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    day = day,
                    unit = appState.liquidUnit
                )
            }
            item {
                if (state.loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreenToolbar(
    containerColor: Color = Color.Transparent,
    onGoBack: () -> Unit
) {
    CenterAlignedTopAppBar(
        modifier = Modifier
            .fillMaxWidth(),
        navigationIcon = {
            IconButton(
                onClick = onGoBack,
                content = {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "close history"
                    )
                }
            )
        },
        title = {
            Text(
                text = "History",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = containerColor
        )
    )
}

@Composable
private fun DayItem(
    modifier: Modifier = Modifier,
    day: Day,
    unit: LiquidUnit
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.95f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = day.date.format(),
                style = MaterialTheme.typography.labelLarge
            )

            val milliliters = remember(day) { day.hydration.map { it.milliliters } }
            HydrationCarousel(
                contentPadding = PaddingValues(horizontal = 24.dp),
                milliliterItems = milliliters,
                selected = milliliters,
                liquidUnit = unit,
                contentBelowItem = { index ->
                    Text(
                        text = day.hydration[index].time.format(),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            )

            Text(
                modifier = Modifier.padding(horizontal = 24.dp),
                text = day.hydration.sumOfMilliliters().format(unit) +
                    " out of " +
                    day.goal.format(unit) +
                    if (day.reachedGoal()) " 🎉" else "",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

class HistoryScreenStore(
    private val hydrationHistoryStore: HydrationHistoryStore
) {

    data class State(
        val days: List<Day> = emptyList(),
        val loading: Boolean = false,
        val lastQueriedDate: LocalDate = Today + DatePeriod(days = 1),
        val reachedStart: Boolean = false
    ) {
        val canLoadNext = !loading && !reachedStart
    }

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    suspend fun loadNext() {
        _state.update { it.copy(loading = true) }
        val days = hydrationHistoryStore.days(_state.value.lastQueriedDate)
        _state.update {
            it.copy(
                loading = false,
                days = it.days + days,
                lastQueriedDate = if (days.isNotEmpty()) days.last().date else it.lastQueriedDate,
                reachedStart = days.isEmpty()
            )
        }
    }

    companion object {
        const val LOAD_MORE_THRESHOLD = 10
    }
}
