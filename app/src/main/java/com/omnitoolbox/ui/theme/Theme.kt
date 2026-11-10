package omni.toolbox.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun OmniToolboxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeName: String = "default", // default, earth, forest, water, sand, nature
    content: @Composable () -> Unit
) {
    val isDark = darkTheme

    val colorScheme = when (themeName.lowercase()) {
        "earth" -> {
            if (isDark) {
                darkColorScheme(
                    primary = EarthPrimary,
                    secondary = EarthSecondary,
                    tertiary = EarthSurfaceDark,
                    background = EarthBgDark,
                    surface = EarthSurfaceDark,
                    onPrimary = Color.White,
                    onSecondary = Color(0xFFF5F5DC),
                    onTertiary = Color(0xFFF5F5DC),
                    onBackground = Color(0xFFF5F5DC),
                    onSurface = Color(0xFFF5F5DC),
                    outline = EarthPrimary.copy(alpha = 0.5f)
                )
            } else {
                lightColorScheme(
                    primary = EarthPrimary,
                    secondary = EarthSecondary,
                    tertiary = Color.White,
                    background = EarthBgLight,
                    surface = EarthSurfaceLight,
                    surfaceVariant = EarthSurfaceLight,
                    onPrimary = Color.White,
                    onSecondary = Color(0xFF2A211D),
                    onTertiary = Color(0xFF2A211D),
                    onBackground = Color(0xFF2A211D),
                    onSurface = Color(0xFF2A211D),
                    outline = EarthPrimary.copy(alpha = 0.5f)
                )
            }
        }
        "forest" -> {
            if (isDark) {
                darkColorScheme(
                    primary = ForestPrimary,
                    secondary = ForestSecondary,
                    tertiary = ForestSurfaceDark,
                    background = ForestBgDark,
                    surface = ForestSurfaceDark,
                    onPrimary = Color.White,
                    onSecondary = Color(0xFFF1F8E9),
                    onTertiary = Color(0xFFF1F8E9),
                    onBackground = Color(0xFFF1F8E9),
                    onSurface = Color(0xFFF1F8E9),
                    outline = ForestPrimary.copy(alpha = 0.5f)
                )
            } else {
                lightColorScheme(
                    primary = ForestPrimary,
                    secondary = ForestSecondary,
                    tertiary = Color.White,
                    background = ForestBgLight,
                    surface = ForestSurfaceLight,
                    surfaceVariant = ForestSurfaceLight,
                    onPrimary = Color.White,
                    onSecondary = Color(0xFF1B261E),
                    onTertiary = Color(0xFF1B261E),
                    onBackground = Color(0xFF1B261E),
                    onSurface = Color(0xFF1B261E),
                    outline = ForestPrimary.copy(alpha = 0.5f)
                )
            }
        }
        "water" -> {
            if (isDark) {
                darkColorScheme(
                    primary = WaterPrimary,
                    secondary = WaterSecondary,
                    tertiary = WaterSurfaceDark,
                    background = WaterBgDark,
                    surface = WaterSurfaceDark,
                    onPrimary = Color.White,
                    onSecondary = Color(0xFFE0F7FA),
                    onTertiary = Color(0xFFE0F7FA),
                    onBackground = Color(0xFFE0F7FA),
                    onSurface = Color(0xFFE0F7FA),
                    outline = WaterPrimary.copy(alpha = 0.5f)
                )
            } else {
                lightColorScheme(
                    primary = WaterPrimary,
                    secondary = WaterSecondary,
                    tertiary = Color.White,
                    background = WaterBgLight,
                    surface = WaterSurfaceLight,
                    surfaceVariant = WaterSurfaceLight,
                    onPrimary = Color.White,
                    onSecondary = Color(0xFF0D1B2A),
                    onTertiary = Color(0xFF0D1B2A),
                    onBackground = Color(0xFF0D1B2A),
                    onSurface = Color(0xFF0D1B2A),
                    outline = WaterPrimary.copy(alpha = 0.5f)
                )
            }
        }
        "sand" -> {
            if (isDark) {
                darkColorScheme(
                    primary = SandPrimary,
                    secondary = SandSecondary,
                    tertiary = SandSurfaceDark,
                    background = SandBgDark,
                    surface = SandSurfaceDark,
                    onPrimary = Color.Black,
                    onSecondary = Color(0xFFFFF8E1),
                    onTertiary = Color(0xFFFFF8E1),
                    onBackground = Color(0xFFFFF8E1),
                    onSurface = Color(0xFFFFF8E1),
                    outline = SandPrimary.copy(alpha = 0.5f)
                )
            } else {
                lightColorScheme(
                    primary = SandPrimary,
                    secondary = SandSecondary,
                    tertiary = Color.White,
                    background = SandBgLight,
                    surface = SandSurfaceLight,
                    surfaceVariant = SandSurfaceLight,
                    onPrimary = Color.Black,
                    onSecondary = Color(0xFF1C1A17),
                    onTertiary = Color(0xFF1C1A17),
                    onBackground = Color(0xFF1C1A17),
                    onSurface = Color(0xFF1C1A17),
                    outline = SandPrimary.copy(alpha = 0.5f)
                )
            }
        }
        "nature" -> {
            if (isDark) {
                darkColorScheme(
                    primary = NaturePrimaryDark,
                    onPrimary = Color.Black,
                    primaryContainer = NaturePrimaryContainerDark,
                    onPrimaryContainer = NatureOnPrimaryContainerDark,
                    secondary = NatureSecondaryDark,
                    onSecondary = Color.Black,
                    secondaryContainer = NatureSecondaryContainerDark,
                    onSecondaryContainer = NatureOnSecondaryContainerDark,
                    tertiary = NatureSurfaceDark,
                    onTertiary = NatureOnSurfaceDark,
                    background = NatureBgDark,
                    onBackground = NatureOnSurfaceDark,
                    surface = NatureSurfaceDark,
                    onSurface = NatureOnSurfaceDark,
                    surfaceVariant = NatureSurfaceVariantDark,
                    onSurfaceVariant = NatureOnSurfaceVariantDark,
                    outline = NatureOutlineDark
                )
            } else {
                lightColorScheme(
                    primary = NaturePrimaryLight,
                    onPrimary = Color.White,
                    primaryContainer = NaturePrimaryContainerLight,
                    onPrimaryContainer = NatureOnPrimaryContainerLight,
                    secondary = NatureSecondaryLight,
                    onSecondary = Color.White,
                    secondaryContainer = NatureSecondaryContainerLight,
                    onSecondaryContainer = NatureOnSecondaryContainerLight,
                    tertiary = Color.White,
                    onTertiary = NatureOnSurfaceLight,
                    background = NatureBgLight,
                    onBackground = NatureOnSurfaceLight,
                    surface = NatureSurfaceLight,
                    onSurface = NatureOnSurfaceLight,
                    surfaceVariant = NatureSurfaceVariantLight,
                    onSurfaceVariant = NatureOnSurfaceVariantLight,
                    outline = NatureOutlineLight
                )
            }
        }
        else -> { // Default (Epic Indigo/Slate)
            if (isDark) {
                darkColorScheme(
                    primary = EpicPrimary,
                    secondary = EpicSecondary,
                    tertiary = Color(0xFF1E293B),
                    background = EpicBgDark,
                    surface = EpicSurfaceDark,
                    onPrimary = Color.White,
                    onSecondary = Color(0xFFF8FAFC),
                    onTertiary = Color(0xFFF8FAFC),
                    onBackground = Color(0xFFF8FAFC),
                    onSurface = Color(0xFFF8FAFC),
                    outline = EpicPrimary.copy(alpha = 0.5f)
                )
            } else {
                lightColorScheme(
                    primary = EpicPrimary,
                    secondary = EpicSecondary,
                    tertiary = Color.White,
                    background = EpicBgLight,
                    surface = EpicSurfaceLight,
                    surfaceVariant = EpicSurfaceLight,
                    onPrimary = Color.White,
                    onSecondary = Color(0xFF0F172A),
                    onTertiary = Color(0xFF0F172A),
                    onBackground = Color(0xFF0F172A),
                    onSurface = Color(0xFF0F172A),
                    outline = EpicPrimary.copy(alpha = 0.5f)
                )
            }
        }
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            @Suppress("DEPRECATION")
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDark
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
