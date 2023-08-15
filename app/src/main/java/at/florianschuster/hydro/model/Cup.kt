package at.florianschuster.hydro.model

import kotlinx.serialization.Serializable
import java.util.Locale

@Serializable
data class Cup(
    val milliliters: Milliliters
) : Comparable<Cup> {
    override fun compareTo(other: Cup): Int = milliliters compareTo other.milliliters
}

fun defaultCups(
    locale: Locale = Locale.getDefault()
): List<Cup> = defaultSelectedCups(locale) + when (locale) {
    Locale.US,
    Locale.UK -> listOf(
        Cup(Milliliters(355)), // 12 oz
        Cup(Milliliters(591)), // 20 oz
        Cup(Milliliters(946)), // 32 oz
        Cup(Milliliters(1183)) // 40 oz
    )

    else -> listOf(
        Cup(Milliliters(330)),
        Cup(Milliliters(500)),
        Cup(Milliliters(1_000)),
        Cup(Milliliters(2_000))
    )
}.sorted()

fun defaultSelectedCups(
    locale: Locale = Locale.getDefault()
) = when (locale) {
    Locale.US,
    Locale.UK -> listOf(
        Cup(Milliliters(236)) // 8 oz
    )

    else -> listOf(
        Cup(Milliliters(250))
    )
}
