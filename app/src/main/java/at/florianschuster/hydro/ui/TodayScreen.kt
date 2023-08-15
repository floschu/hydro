package at.florianschuster.hydro.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.QueryStats
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import at.florianschuster.hydro.AppAction
import at.florianschuster.hydro.AppState
import at.florianschuster.hydro.R
import at.florianschuster.hydro.model.format
import at.florianschuster.hydro.model.icon
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
internal fun TodayScreen(
    contentPadding: PaddingValues,
    state: AppState,
    dispatch: (AppAction) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AnimatedVisibility(visible = state.dailyGoalReached) {
                LottieAnimation(
                    modifier = Modifier.size(148.dp),
                    composition = rememberLottieComposition(
                        // https://lottiefiles.com/animations/trophy-yEGPe40FVr
                        spec = LottieCompositionSpec.RawRes(R.raw.winner)
                    ).value
                )
            }
            Text(
                text = state.todayHydration.format(state.liquidUnit),
                style = MaterialTheme.typography.displayLarge
            )
            Text(
                text = state.hydrationProgress.format(),
                style = MaterialTheme.typography.titleMedium
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            state.selectedCups.forEach { cup ->
                FloatingActionButton(
                    onClick = { dispatch(AppAction.AddHydration(cup.milliliters)) },
                    content = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                modifier = Modifier.size(20.dp),
                                painter = cup.milliliters.icon(),
                                contentDescription = "add one cup"
                            )
                            Text(
                                text = cup.milliliters.format(state.liquidUnit),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayToolbar(
    containerColor: Color = Color.Transparent,
    isDebug: Boolean,
    onGoToHistory: () -> Unit,
    onGoToSettings: () -> Unit
) {
    CenterAlignedTopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        actions = {
            if (isDebug) {
                IconButton(
                    onClick = onGoToHistory,
                    content = {
                        Icon(
                            imageVector = Icons.Outlined.QueryStats,
                            contentDescription = "go to history"
                        )
                    }
                )
            }
            IconButton(
                onClick = onGoToSettings,
                content = {
                    Icon(
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "go to settings"
                    )
                }
            )
        },
        title = {},
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = containerColor
        )
    )
}
