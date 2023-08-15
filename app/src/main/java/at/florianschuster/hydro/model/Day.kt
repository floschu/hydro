package at.florianschuster.hydro.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable
import java.util.UUID

data class Day(
    val date: LocalDate,
    val hydration: List<Hydration>,
    val goal: Milliliters,
    val id: String = UUID.randomUUID().toString()
) : Comparable<Day> {

    @Serializable
    data class Hydration(
        val milliliters: Milliliters,
        val time: LocalTime,
        val id: String = UUID.randomUUID().toString()
    ) : Comparable<Hydration> {
        override fun compareTo(other: Hydration): Int = time compareTo other.time
    }

    override fun compareTo(other: Day): Int = date compareTo other.date
}

fun List<Day.Hydration>.sumOfMilliliters(): Milliliters {
    return Milliliters(sumOf { it.milliliters.value })
}

fun Day.reachedGoal(): Boolean = hydration.sumOfMilliliters() >= goal
