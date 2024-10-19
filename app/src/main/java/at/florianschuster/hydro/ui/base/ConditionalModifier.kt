package at.florianschuster.hydro.ui.base

import androidx.compose.ui.Modifier

internal fun Modifier.conditional(
    condition: Boolean,
    modifier: Modifier.() -> Modifier
): Modifier {
    return if (condition) {
        then(modifier(Modifier))
    } else {
        this
    }
}
