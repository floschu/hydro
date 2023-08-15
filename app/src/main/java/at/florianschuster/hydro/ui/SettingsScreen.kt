package at.florianschuster.hydro.ui

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.InvertColors
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.outlined.Start
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import at.florianschuster.hydro.AppAction
import at.florianschuster.hydro.AppState
import at.florianschuster.hydro.R
import at.florianschuster.hydro.model.LiquidUnit
import at.florianschuster.hydro.model.Milliliters
import at.florianschuster.hydro.model.Reminder
import at.florianschuster.hydro.model.format
import at.florianschuster.hydro.ui.base.SettingItem
import at.florianschuster.hydro.ui.base.SettingsSection
import at.florianschuster.hydro.ui.base.TimePickerDialog
import at.florianschuster.hydro.ui.base.ToggleSettingItem
import kotlinx.datetime.LocalTime

@Composable
fun SettingsScreen(
    contentPadding: PaddingValues,
    state: AppState,
    dispatch: (AppAction) -> Unit,
    onSetGoalOfTheDay: () -> Unit,
    onSetLiquidUnit: () -> Unit,
    onSetTheme: () -> Unit,
    onShowDeveloperInfo: () -> Unit,
    onWriteDeveloper: () -> Unit,
    onSetInterval: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        SettingsSection(
            modifier = Modifier
                .padding(top = contentPadding.calculateTopPadding())
                .padding(horizontal = 16.dp),
            title = "Hydration"
        ) {
            SettingItem(
                fieldName = "Daily Goal",
                value = state.dailyGoal.format(state.liquidUnit),
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.WaterDrop,
                        contentDescription = null
                    )
                },
                onClick = onSetGoalOfTheDay
            )
            SettingItem(
                fieldName = "Measurement Unit",
                value = state.liquidUnit.format(),
                icon = {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(R.drawable.ic_unit),
                        contentDescription = null
                    )
                },
                onClick = onSetLiquidUnit
            )
            CupCarouselSelection(
                state = state,
                dispatch = dispatch
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        ReminderSettingsSection(
            canScheduleAlarms = state.canScheduleAlarms,
            reminder = state.reminder,
            onRemindersChanged = { dispatch(AppAction.SetReminder(it)) },
            onSetInterval = onSetInterval
        )

        Spacer(modifier = Modifier.height(16.dp))

        SettingsSection(
            modifier = Modifier
                .padding(horizontal = 16.dp),
            title = "App"
        ) {
            SettingItem(
                fieldName = "Theme",
                value = state.theme.format(),
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.InvertColors,
                        contentDescription = null
                    )
                },
                onClick = onSetTheme
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        DataSettingsSection(
            onDeleteAll = { dispatch(AppAction.DeleteAll) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SettingsSection(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .apply {
                    if (!state.isDebug) {
                        padding(bottom = contentPadding.calculateBottomPadding())
                    }
                },
            title = "About"
        ) {
            SettingItem(
                fieldName = "Developer Info",
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null
                    )
                },
                onClick = onShowDeveloperInfo
            )
            SettingItem(
                fieldName = "Feedback",
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Email,
                        contentDescription = null
                    )
                },
                onClick = onWriteDeveloper
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (state.isDebug) {
            DebugSettingsSection(
                modifier = Modifier.padding(bottom = contentPadding.calculateBottomPadding()),
                todayHydration = state.todayHydration,
                liquidUnit = state.liquidUnit,
                onResetToday = { dispatch(AppAction.ResetToday) },
                onShowReminderNotification = {
                    dispatch(AppAction.ShowHydrationReminderNotification(forced = true))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsToolbar(
    containerColor: Color = Color.Transparent,
    onGoBack: () -> Unit
) {
    CenterAlignedTopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        navigationIcon = {
            IconButton(
                onClick = onGoBack,
                content = {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "close settings"
                    )
                }
            )
        },
        title = {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = containerColor
        )
    )
}

@Composable
private fun ReminderSettingsSection(
    canScheduleAlarms: Boolean,
    reminder: Reminder?,
    onRemindersChanged: (Reminder?) -> Unit,
    onSetInterval: () -> Unit
) {
    var showPermissionRationaleDialog by remember { mutableStateOf(false) }
    var showScheduleAlarmsDialog by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (!granted) {
            showPermissionRationaleDialog = true
        } else if (!canScheduleAlarms) {
            showScheduleAlarmsDialog = true
        } else {
            onRemindersChanged(Reminder.DEFAULT)
        }
    }
    val context = LocalContext.current
    SettingsSection(
        modifier = Modifier.padding(horizontal = 16.dp),
        title = "Reminders"
    ) {
        ToggleSettingItem(
            fieldName = "Enabled",
            icon = if (reminder != null) {
                Icons.Outlined.Notifications
            } else {
                Icons.Outlined.NotificationsOff
            },
            checked = reminder != null,
            onCheckedChange = { checked ->
                if (checked) {
                    if (!canScheduleAlarms) {
                        showScheduleAlarmsDialog = true
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val permission = android.Manifest.permission.POST_NOTIFICATIONS
                        if (!context.isPermissionGranted(permission)) {
                            launcher.launch(permission)
                        } else {
                            onRemindersChanged(Reminder.DEFAULT)
                        }
                    } else {
                        onRemindersChanged(Reminder.DEFAULT)
                    }
                } else {
                    onRemindersChanged(null)
                }
            }
        )
        if (reminder != null) {
            var showStartPicker by remember { mutableStateOf(false) }
            var showEndPicker by remember { mutableStateOf(false) }
            var showStartBeforeEndAlert by remember { mutableStateOf(false) }
            SettingItem(
                fieldName = "Start",
                value = reminder.start.format(),
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Start,
                        contentDescription = null
                    )
                },
                onClick = { showStartPicker = true }
            )
            SettingItem(
                fieldName = "End",
                value = reminder.end.format(),
                icon = {
                    Icon(
                        modifier = Modifier.scale(scaleX = -1f, scaleY = 1f),
                        imageVector = Icons.Outlined.Start,
                        contentDescription = null
                    )
                },
                onClick = { showEndPicker = true }
            )
            SettingItem(
                fieldName = "Interval",
                value = reminder.interval.format(),
                icon = {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(R.drawable.ic_arrow_range),
                        contentDescription = null
                    )
                },
                onClick = onSetInterval
            )
            if (showStartPicker) {
                TimePickerDialog(
                    initialHour = reminder.start.hour,
                    initialMinute = reminder.start.minute,
                    onConfirm = { hour, minute ->
                        showStartPicker = false
                        val start = LocalTime(hour, minute, 0)
                        if (start > reminder.end) {
                            showStartBeforeEndAlert = true
                        } else {
                            onRemindersChanged(reminder.copy(start = start))
                        }
                    },
                    onCancel = { showStartPicker = false }
                )
            }
            if (showEndPicker) {
                TimePickerDialog(
                    initialHour = reminder.end.hour,
                    initialMinute = reminder.end.minute,
                    onConfirm = { hour, minute ->
                        showEndPicker = false
                        val end = LocalTime(hour, minute, 0)
                        if (reminder.start > end) {
                            showStartBeforeEndAlert = true
                        } else {
                            onRemindersChanged(reminder.copy(end = end))
                        }
                    },
                    onCancel = { showEndPicker = false }
                )
            }
            if (showStartBeforeEndAlert) {
                StartBeforeEndAlert(
                    onDismiss = { showStartBeforeEndAlert = false }
                )
            }
        }
        if (showPermissionRationaleDialog) {
            NotificationPermissionSettingsAlert(
                onDismiss = { showPermissionRationaleDialog = false }
            )
        }
        if (showScheduleAlarmsDialog) {
            AlarmSystemSettingsAlert(
                onDismiss = { showScheduleAlarmsDialog = false }
            )
        }
    }
}

private fun Context.isPermissionGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(
        this,
        permission
    ) == PackageManager.PERMISSION_GRANTED
}

@Composable
private fun StartBeforeEndAlert(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Time constraints") },
        text = {
            Text(
                text = "Start time of the Reminder must be before the end time."
            )
        },
        confirmButton = {
            Button(
                onClick = { onDismiss() },
                content = { Text(text = "Ok") }
            )
        }
    )
}

@Composable
private fun NotificationPermissionSettingsAlert(onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Notification permission denied") },
        text = {
            Text(
                text = "The app is unable to show Reminders " +
                    "without the notification permission."
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    context.goToNotificationPermissionSettings()
                    onDismiss()
                },
                content = { Text(text = "Go To Settings") }
            )
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() },
                content = { Text(text = "Cancel") }
            )
        }
    )
}

private fun Context.goToNotificationPermissionSettings() {
    startActivity(
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.parse("package:$packageName")
        }
    )
}

@Composable
private fun AlarmSystemSettingsAlert(onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Alarm permission denied") },
        text = {
            Text(
                text = "The app is unable to show Reminders " +
                    "without the alarm permission."
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    context.goToAlarmSystemSettings()
                    onDismiss()
                },
                content = { Text(text = "Go To Settings") }
            )
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() },
                content = { Text(text = "Cancel") }
            )
        }
    )
}

private fun Context.goToAlarmSystemSettings() {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return
    startActivity(
        Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
            data = Uri.parse("package:$packageName")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
    )
}

@Composable
private fun DataSettingsSection(
    modifier: Modifier = Modifier,
    onDeleteAll: () -> Unit
) {
    var showDeleteAllDialog by remember { mutableStateOf(false) }
    SettingsSection(
        modifier = modifier.padding(horizontal = 16.dp),
        title = "Data"
    ) {
        SettingItem(
            fieldName = "Delete all stored data",
            value = "This includes all settings and the complete hydration history",
            icon = {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = null
                )
            },
            onClick = { showDeleteAllDialog = true }
        )
    }
    if (showDeleteAllDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAllDialog = false },
            title = { Text(text = "Are you sure?") },
            text = { Text(text = "This cannot be reversed.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteAllDialog = false
                        onDeleteAll()
                    },
                    content = { Text(text = "Yes") }
                )
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteAllDialog = false },
                    content = { Text(text = "Cancel") }
                )
            }
        )
    }
}

@Composable
private fun DebugSettingsSection(
    modifier: Modifier = Modifier,
    todayHydration: Milliliters,
    liquidUnit: LiquidUnit,
    onResetToday: () -> Unit,
    onShowReminderNotification: () -> Unit
) {
    SettingsSection(
        modifier = modifier.padding(horizontal = 16.dp),
        title = "Debug"
    ) {
        SettingItem(
            fieldName = "Reset hydration today",
            value = "This sets today's hydration from ${todayHydration.format(liquidUnit)} " +
                "to ${Milliliters.ZERO.format(liquidUnit)}",
            icon = {
                Icon(
                    imageVector = Icons.Outlined.RestartAlt,
                    contentDescription = null
                )
            },
            onClick = onResetToday
        )

        SettingItem(
            fieldName = "Show Reminder Notification",
            icon = {
                Icon(
                    imageVector = Icons.Outlined.NotificationsActive,
                    contentDescription = null
                )
            },
            onClick = onShowReminderNotification
        )
    }
}
