package at.florianschuster.hydro.model

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import at.florianschuster.hydro.R
import kotlinx.serialization.Serializable
import kotlin.math.roundToInt

@Serializable
@JvmInline
value class Milliliters(val value: Int) : Comparable<Milliliters> {
    override fun compareTo(other: Milliliters): Int = value compareTo other.value

    companion object {
        val ZERO: Milliliters = Milliliters(0)

        val DAILY_GOAL_DEFAULT: Milliliters = Milliliters(2_000)
        val DAILY_GOAL_MIN: Milliliters = Milliliters(500)
        val DAILY_GOAL_MAX: Milliliters = Milliliters(5_000)
        val DAILY_GOAL_STEPS: Milliliters = Milliliters(100)
    }
}

operator fun Milliliters?.plus(
    other: Milliliters
): Milliliters = Milliliters((this?.value ?: 0) + other.value)

operator fun Milliliters.minus(
    other: Milliliters
): Milliliters = Milliliters(maxOf(0, value - other.value))

operator fun Milliliters.times(
    other: Float
): Milliliters = Milliliters(maxOf(0f, value * other).roundToInt())

fun Milliliters.format(
    unit: LiquidUnit
): String {
    val actualValue = unit.convertValue(this).roundToInt()
    return when (unit) {
        LiquidUnit.Milliliter -> "$actualValue ml"
        LiquidUnit.USFluidOunce -> "$actualValue oz."
        LiquidUnit.UKFluidOunce -> "$actualValue oz."
    }
}

@Composable
fun Milliliters.icon(): Painter = when (value) {
    in 0..250 -> painterResource(R.drawable.ic_water_less)
    in 251..330 -> painterResource(R.drawable.ic_water_medium)
    in 331..750 -> painterResource(R.drawable.ic_water_full)
    in 751..1_000 -> painterResource(R.drawable.ic_water_bottle)
    else -> painterResource(R.drawable.ic_water_bottle_large)
}
