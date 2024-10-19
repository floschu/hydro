package at.florianschuster.hydro.ui.base

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.core.view.WindowCompat
import at.florianschuster.hydro.R

private val md_theme_light_primary = Color(0xFF006683)
private val md_theme_light_onPrimary = Color(0xFFFFFFFF)
private val md_theme_light_primaryContainer = Color(0xFFBDE9FF)
private val md_theme_light_onPrimaryContainer = Color(0xFF001F2A)
private val md_theme_light_secondary = Color(0xFF006689)
private val md_theme_light_onSecondary = Color(0xFFFFFFFF)
private val md_theme_light_secondaryContainer = Color(0xFFC3E8FF)
private val md_theme_light_onSecondaryContainer = Color(0xFF001E2C)
private val md_theme_light_tertiary = Color(0xFF00629F)
private val md_theme_light_onTertiary = Color(0xFFFFFFFF)
private val md_theme_light_tertiaryContainer = Color(0xFFD0E4FF)
private val md_theme_light_onTertiaryContainer = Color(0xFF001D34)
private val md_theme_light_error = Color(0xFFBA1A1A)
private val md_theme_light_errorContainer = Color(0xFFFFDAD6)
private val md_theme_light_onError = Color(0xFFFFFFFF)
private val md_theme_light_onErrorContainer = Color(0xFF410002)
private val md_theme_light_background = Color(0xFFFAFCFF)
private val md_theme_light_onBackground = Color(0xFF001F2A)
private val md_theme_light_surface = Color(0xFFFAFCFF)
private val md_theme_light_onSurface = Color(0xFF001F2A)
private val md_theme_light_surfaceVariant = Color(0xFFDCE4E9)
private val md_theme_light_onSurfaceVariant = Color(0xFF40484C)
private val md_theme_light_outline = Color(0xFF70787D)
private val md_theme_light_inverseOnSurface = Color(0xFFE1F4FF)
private val md_theme_light_inverseSurface = Color(0xFF003547)
private val md_theme_light_inversePrimary = Color(0xFF65D3FF)
private val md_theme_light_surfaceTint = Color(0xFF006683)
private val md_theme_light_outlineVariant = Color(0xFFC0C8CD)
private val md_theme_light_scrim = Color(0xFF000000)

private val md_theme_dark_primary = Color(0xFF65D3FF)
private val md_theme_dark_onPrimary = Color(0xFF003546)
private val md_theme_dark_primaryContainer = Color(0xFF004D64)
private val md_theme_dark_onPrimaryContainer = Color(0xFFBDE9FF)
private val md_theme_dark_secondary = Color(0xFF7AD1FF)
private val md_theme_dark_onSecondary = Color(0xFF003549)
private val md_theme_dark_secondaryContainer = Color(0xFF004C68)
private val md_theme_dark_onSecondaryContainer = Color(0xFFC3E8FF)
private val md_theme_dark_tertiary = Color(0xFF9BCBFF)
private val md_theme_dark_onTertiary = Color(0xFF003256)
private val md_theme_dark_tertiaryContainer = Color(0xFF004A79)
private val md_theme_dark_onTertiaryContainer = Color(0xFFD0E4FF)
private val md_theme_dark_error = Color(0xFFFFB4AB)
private val md_theme_dark_errorContainer = Color(0xFF93000A)
private val md_theme_dark_onError = Color(0xFF690005)
private val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
private val md_theme_dark_background = Color(0xFF001F2A)
private val md_theme_dark_onBackground = Color(0xFFBFE9FF)
private val md_theme_dark_surface = Color(0xFF001F2A)
private val md_theme_dark_onSurface = Color(0xFFBFE9FF)
private val md_theme_dark_surfaceVariant = Color(0xFF40484C)
private val md_theme_dark_onSurfaceVariant = Color(0xFFC0C8CD)
private val md_theme_dark_outline = Color(0xFF8A9297)
private val md_theme_dark_inverseOnSurface = Color(0xFF001F2A)
private val md_theme_dark_inverseSurface = Color(0xFFBFE9FF)
private val md_theme_dark_inversePrimary = Color(0xFF006683)
private val md_theme_dark_surfaceTint = Color(0xFF65D3FF)
private val md_theme_dark_outlineVariant = Color(0xFF40484C)
private val md_theme_dark_scrim = Color(0xFF000000)

private val LightColors = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    tertiary = md_theme_light_tertiary,
    onTertiary = md_theme_light_onTertiary,
    tertiaryContainer = md_theme_light_tertiaryContainer,
    onTertiaryContainer = md_theme_light_onTertiaryContainer,
    error = md_theme_light_error,
    errorContainer = md_theme_light_errorContainer,
    onError = md_theme_light_onError,
    onErrorContainer = md_theme_light_onErrorContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surfaceVariant,
    onSurfaceVariant = md_theme_light_onSurfaceVariant,
    outline = md_theme_light_outline,
    inverseOnSurface = md_theme_light_inverseOnSurface,
    inverseSurface = md_theme_light_inverseSurface,
    inversePrimary = md_theme_light_inversePrimary,
    surfaceTint = md_theme_light_surfaceTint,
    outlineVariant = md_theme_light_outlineVariant,
    scrim = md_theme_light_scrim
)

private val DarkColors = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    tertiary = md_theme_dark_tertiary,
    onTertiary = md_theme_dark_onTertiary,
    tertiaryContainer = md_theme_dark_tertiaryContainer,
    onTertiaryContainer = md_theme_dark_onTertiaryContainer,
    error = md_theme_dark_error,
    errorContainer = md_theme_dark_errorContainer,
    onError = md_theme_dark_onError,
    onErrorContainer = md_theme_dark_onErrorContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surfaceVariant,
    onSurfaceVariant = md_theme_dark_onSurfaceVariant,
    outline = md_theme_dark_outline,
    inverseOnSurface = md_theme_dark_inverseOnSurface,
    inverseSurface = md_theme_dark_inverseSurface,
    inversePrimary = md_theme_dark_inversePrimary,
    surfaceTint = md_theme_dark_surfaceTint,
    outlineVariant = md_theme_dark_outlineVariant,
    scrim = md_theme_dark_scrim
)

private val NotoSansFontFamily = FontFamily(
    Font(R.font.notosans_black, FontWeight.Black),
    Font(R.font.notosans_bold, FontWeight.Bold),
    Font(R.font.notosans_extrabold, FontWeight.ExtraBold),
    Font(R.font.notosans_extralight, FontWeight.ExtraLight),
    Font(R.font.notosans_light, FontWeight.Light),
    Font(R.font.notosans_medium, FontWeight.Medium),
    Font(R.font.notosans_regular, FontWeight.Normal),
    Font(R.font.notosans_semibold, FontWeight.SemiBold),
    Font(R.font.notosans_thin, FontWeight.Thin)
)

// Set of Material typography styles to start with
private val typography = with(Typography()) {
    copy(
        displayLarge = displayLarge.copy(fontFamily = NotoSansFontFamily),
        displayMedium = displayMedium.copy(fontFamily = NotoSansFontFamily),
        displaySmall = displaySmall.copy(fontFamily = NotoSansFontFamily),
        headlineLarge = headlineLarge.copy(fontFamily = NotoSansFontFamily),
        headlineMedium = headlineMedium.copy(fontFamily = NotoSansFontFamily),
        headlineSmall = headlineSmall.copy(fontFamily = NotoSansFontFamily),
        titleLarge = titleLarge.copy(fontFamily = NotoSansFontFamily),
        titleMedium = titleMedium.copy(fontFamily = NotoSansFontFamily),
        titleSmall = titleSmall.copy(fontFamily = NotoSansFontFamily),
        bodyLarge = bodyLarge.copy(fontFamily = NotoSansFontFamily),
        bodyMedium = bodyMedium.copy(fontFamily = NotoSansFontFamily),
        bodySmall = bodySmall.copy(fontFamily = NotoSansFontFamily),
        labelLarge = labelLarge.copy(fontFamily = NotoSansFontFamily),
        labelMedium = labelMedium.copy(fontFamily = NotoSansFontFamily),
        labelSmall = labelSmall.copy(fontFamily = NotoSansFontFamily)
    )
}

@Composable
fun HydroTheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColors
        else -> LightColors
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            with(WindowCompat.getInsetsController(window, view)) {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
