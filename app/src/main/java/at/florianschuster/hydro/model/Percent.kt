package at.florianschuster.hydro.model

import androidx.annotation.FloatRange
import kotlin.math.roundToInt

@JvmInline
value class Percent(
    // 0.0f = 0%, 1.0f = 100%
    @FloatRange(from = 0.0, to = Double.MAX_VALUE) val value: Float
) {
    init {
        require(value >= 0f)
    }
}

fun Percent.format(): String = "${(value * 100).roundToInt()}%"
