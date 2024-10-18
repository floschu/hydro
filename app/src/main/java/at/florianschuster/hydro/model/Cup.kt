package at.florianschuster.hydro.model

import kotlinx.serialization.Serializable

@Serializable
data class Cup(
    val milliliters: Milliliters
) : Comparable<Cup> {
    override fun compareTo(other: Cup): Int = milliliters compareTo other.milliliters
}

fun defaultCups(
    liquidUnit: LiquidUnit
): List<Cup> = defaultSelectedCups(liquidUnit) + when (liquidUnit) {
    LiquidUnit.USFluidOunce,
    LiquidUnit.UKFluidOunce -> listOf(
        Cup(Milliliters(355)), // 12 oz
        Cup(Milliliters(591)), // 20 oz
        Cup(Milliliters(946)), // 32 oz
        Cup(Milliliters(1183)) // 40 oz
    )

    LiquidUnit.Milliliter -> listOf(
        Cup(Milliliters(330)),
        Cup(Milliliters(500)),
        Cup(Milliliters(1_000)),
        Cup(Milliliters(2_000))
    )
}.sorted()

fun defaultSelectedCups(
    liquidUnit: LiquidUnit
) = when (liquidUnit) {
    LiquidUnit.USFluidOunce,
    LiquidUnit.UKFluidOunce -> listOf(
        Cup(Milliliters(236)) // 8 oz
    )

    LiquidUnit.Milliliter -> listOf(
        Cup(Milliliters(250))
    )
}
