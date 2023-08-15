package at.florianschuster.hydro.model

import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

@Serializable
data class Reminder(
    val start: LocalTime,
    val end: LocalTime,
    val interval: Duration
) {
    init {
        require(start < end) { "$start cannot be before $end" }
    }

    companion object {
        val DEFAULT = Reminder(
            start = LocalTime(9, 0, 0, 0),
            end = LocalTime(18, 0, 0, 0),
            interval = 30.minutes
        )
    }
}

fun Reminder.calculateReminderTimes(): List<LocalTime> = buildList {
    var current = start
    while (current <= end) {
        add(current)
        current += interval
    }
    if (!contains(end)) add(end)
}
