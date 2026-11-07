package com.toolbox.core.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.toolbox.core.persistence.NatureTheme
import com.toolbox.core.persistence.ThemeMode

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    secondary = Secondary,
    onSecondary = OnSecondary,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = OnSecondaryContainer,
    tertiary = Tertiary,
    onTertiary = OnTertiary,
    tertiaryContainer = TertiaryContainer,
    onTertiaryContainer = OnTertiaryContainer,
    error = Error,
    onError = OnError,
    errorContainer = ErrorContainer,
    onErrorContainer = OnErrorContainer,
    background = Background,
    onBackground = OnBackground,
    surface = Surface,
    onSurface = OnSurface,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = OnSurfaceVariant,
    outline = Outline,
    outlineVariant = OutlineVariant,
)

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = DarkPrimaryContainer,
    onPrimaryContainer = DarkOnPrimaryContainer,
    secondary = DarkSecondary,
    onSecondary = DarkOnSecondary,
    secondaryContainer = DarkSecondaryContainer,
    onSecondaryContainer = DarkOnSecondaryContainer,
    tertiary = DarkTertiary,
    onTertiary = DarkOnTertiary,
    tertiaryContainer = DarkTertiaryContainer,
    onTertiaryContainer = DarkOnTertiaryContainer,
    error = DarkError,
    onError = DarkOnError,
    errorContainer = DarkErrorContainer,
    onErrorContainer = DarkOnErrorContainer,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
    onSurface = DarkOnSurface,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = DarkOnSurfaceVariant,
    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant,
)

@Composable
fun ToolboxTheme(
    themeMode: ThemeMode = ThemeMode.System,
    natureTheme: NatureTheme = NatureTheme.Default,
    content: @Composable () -> Unit,
) {
    val isDark = when (themeMode) {
        ThemeMode.Light -> false
        ThemeMode.Dark -> true
        ThemeMode.System -> isSystemInDarkTheme()
    }

    val context = LocalContext.current
    val colorScheme = when (natureTheme) {
        NatureTheme.Default -> {
            when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                    if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
                }
                isDark -> DarkColorScheme
                else -> LightColorScheme
            }
        }
        NatureTheme.Forest -> if (isDark) {
            DarkColorScheme.copy(
                primary = DarkForestPrimary,
                onPrimary = DarkForestOnPrimary,
                primaryContainer = DarkForestPrimaryContainer,
                onPrimaryContainer = DarkForestOnPrimaryContainer,
                secondary = DarkForestSecondary,
                tertiary = DarkForestTertiary,
                background = DarkForestBackground,
                surface = DarkForestSurface,
            )
        } else {
            LightColorScheme.copy(
                primary = ForestPrimary,
                onPrimary = ForestOnPrimary,
                primaryContainer = ForestPrimaryContainer,
                onPrimaryContainer = ForestOnPrimaryContainer,
                secondary = ForestSecondary,
                tertiary = ForestTertiary,
                background = ForestBackground,
                surface = ForestSurface,
            )
        }
        NatureTheme.Ocean -> if (isDark) {
            DarkColorScheme.copy(
                primary = DarkOceanPrimary,
                onPrimary = DarkOceanOnPrimary,
                primaryContainer = DarkOceanPrimaryContainer,
                onPrimaryContainer = DarkOceanOnPrimaryContainer,
                secondary = DarkOceanSecondary,
                tertiary = DarkOceanTertiary,
                background = DarkOceanBackground,
                surface = DarkOceanSurface,
            )
        } else {
            LightColorScheme.copy(
                primary = OceanPrimary,
                onPrimary = OceanOnPrimary,
                primaryContainer = OceanPrimaryContainer,
                onPrimaryContainer = OceanOnPrimaryContainer,
                secondary = OceanSecondary,
                tertiary = OceanTertiary,
                background = OceanBackground,
                surface = OceanSurface,
            )
        }
        NatureTheme.Sunset -> if (isDark) {
            DarkColorScheme.copy(
                primary = DarkSunsetPrimary,
                onPrimary = DarkSunsetOnPrimary,
                primaryContainer = DarkSunsetPrimaryContainer,
                onPrimaryContainer = DarkSunsetOnPrimaryContainer,
                secondary = DarkSunsetSecondary,
                tertiary = DarkSunsetTertiary,
                background = DarkSunsetBackground,
                surface = DarkSunsetSurface,
            )
        } else {
            LightColorScheme.copy(
                primary = SunsetPrimary,
                onPrimary = SunsetOnPrimary,
                primaryContainer = SunsetPrimaryContainer,
                onPrimaryContainer = SunsetOnPrimaryContainer,
                secondary = SunsetSecondary,
                tertiary = SunsetTertiary,
                background = SunsetBackground,
                surface = SunsetSurface,
            )
        }
        NatureTheme.CherryBlossom -> if (isDark) {
            DarkColorScheme.copy(
                primary = DarkBlossomPrimary,
                onPrimary = DarkBlossomOnPrimary,
                primaryContainer = DarkBlossomPrimaryContainer,
                onPrimaryContainer = DarkBlossomOnPrimaryContainer,
                secondary = DarkBlossomSecondary,
                tertiary = DarkBlossomTertiary,
                background = DarkBlossomBackground,
                surface = DarkBlossomSurface,
            )
        } else {
            LightColorScheme.copy(
                primary = BlossomPrimary,
                onPrimary = BlossomOnPrimary,
                primaryContainer = BlossomPrimaryContainer,
                onPrimaryContainer = BlossomOnPrimaryContainer,
                secondary = BlossomSecondary,
                tertiary = BlossomTertiary,
                background = BlossomBackground,
                surface = BlossomSurface,
            )
        }
        NatureTheme.Lavender -> if (isDark) {
            DarkColorScheme.copy(
                primary = DarkLavenderPrimary,
                onPrimary = DarkLavenderOnPrimary,
                primaryContainer = DarkLavenderPrimaryContainer,
                onPrimaryContainer = DarkLavenderOnPrimaryContainer,
                secondary = DarkLavenderSecondary,
                tertiary = DarkLavenderTertiary,
                background = DarkLavenderBackground,
                surface = DarkLavenderSurface,
            )
        } else {
            LightColorScheme.copy(
                primary = LavenderPrimary,
                onPrimary = LavenderOnPrimary,
                primaryContainer = LavenderPrimaryContainer,
                onPrimaryContainer = LavenderOnPrimaryContainer,
                secondary = LavenderSecondary,
                tertiary = LavenderTertiary,
                background = LavenderBackground,
                surface = LavenderSurface,
            )
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = ToolboxTypography,
        shapes = ToolboxShapes,
        content = content,
    )
}
