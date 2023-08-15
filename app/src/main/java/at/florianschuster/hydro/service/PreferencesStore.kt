package at.florianschuster.hydro.service

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import at.florianschuster.hydro.model.Cup
import at.florianschuster.hydro.model.LiquidUnit
import at.florianschuster.hydro.model.Milliliters
import at.florianschuster.hydro.model.Reminder
import at.florianschuster.hydro.model.Theme
import at.florianschuster.hydro.model.Today
import at.florianschuster.hydro.model.UserTimeZone
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PreferencesStore(
    private val context: Context
) {

    val dailyGoal: Flow<Milliliters?> = context.dataStore.data.map { preferences ->
        val persisted = preferences[dailyTargetMillilitersKey]
        if (persisted != null) Milliliters(persisted) else null
    }

    suspend fun setDailyGoal(milliliters: Milliliters) {
        context.dataStore.edit { it[dailyTargetMillilitersKey] = milliliters.value }
    }

    val reminder: Flow<Reminder?> = context.dataStore.data.map { preferences ->
        val persisted = preferences[reminderKey] ?: return@map null
        json.decodeFromString(persisted)
    }

    suspend fun setReminder(reminder: Reminder?) {
        context.dataStore.edit { preferences ->
            if (reminder == null) {
                preferences.remove(reminderKey)
            } else {
                preferences[reminderKey] = json.encodeToString(reminder)
            }
        }
    }

    val theme: Flow<Theme> = context.dataStore.data.map { preferences ->
        val persisted = preferences[themeKey]
        Theme.of(persisted)
    }

    suspend fun setTheme(theme: Theme) {
        context.dataStore.edit { preferences ->
            preferences[themeKey] = theme.serialized
        }
    }

    val lastGoalCelebration: Flow<Instant> = context.dataStore.data.map { preferences ->
        val persisted = preferences[lastGoalCelebrationKey] ?: return@map Instant.DISTANT_PAST
        Instant.fromEpochMilliseconds(persisted)
    }

    suspend fun setLastGoalCelebration(instant: Instant) {
        context.dataStore.edit { preferences ->
            preferences[lastGoalCelebrationKey] = instant.toEpochMilliseconds()
        }
    }

    val selectedCups: Flow<List<Cup>> = context.dataStore.data.map { preferences ->
        val persisted = preferences[selectedCupsKey] ?: return@map emptyList()
        json.decodeFromString(persisted)
    }

    suspend fun setSelectedCups(cups: List<Cup>) {
        context.dataStore.edit { preferences ->
            preferences[selectedCupsKey] = json.encodeToString(cups)
        }
    }

    val liquidUnit: Flow<LiquidUnit> = context.dataStore.data.map { preferences ->
        val persisted = preferences[liquidUnitKey]
        LiquidUnit.of(persisted)
    }

    suspend fun setLiquidUnit(unit: LiquidUnit) {
        context.dataStore.edit { it[liquidUnitKey] = unit.serialized }
    }

    val onboardingShown: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[onboardingShownKey] ?: false
    }

    suspend fun setOnboardingShown(shown: Boolean) {
        context.dataStore.edit { it[onboardingShownKey] = shown }
    }

    suspend fun clear() {
        context.dataStore.edit(MutablePreferences::clear)
    }

    companion object {
        private val json = Json

        private val Context.dataStore by preferencesDataStore("user_preferences")
        private val dailyTargetMillilitersKey = intPreferencesKey("dailyTargetMilliliters")
        private val reminderKey = stringPreferencesKey("reminder")
        private val themeKey = stringPreferencesKey("theme")
        private val lastGoalCelebrationKey = longPreferencesKey("lastGoalCelebration")
        private val selectedCupsKey = stringPreferencesKey("selectedCups")
        private val liquidUnitKey = stringPreferencesKey("liquidUnit")
        private val onboardingShownKey = booleanPreferencesKey("onboardingShown")
    }
}

suspend fun PreferencesStore.hasCelebratedGoalToday(): Boolean {
    val lastCelebrationDate = lastGoalCelebration
        .first()
        .toLocalDateTime(UserTimeZone)
        .date
    return lastCelebrationDate == Today
}
