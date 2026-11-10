package omni.toolbox.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = NeonMint,
    secondary = SlateDark,
    tertiary = CarbonGray,
    background = SlateDark,
    surface = CarbonGray,
    onPrimary = Color.Black,
    onSecondary = SoftWhite,
    onTertiary = SoftWhite,
    onBackground = SoftWhite,
    onSurface = SoftWhite,
    outline = NeonMint.copy(alpha = 0.5f)
)

private val LightColorScheme = lightColorScheme(
    primary = NeonMint,
    secondary = CarbonGray,
    tertiary = SoftWhite,
    background = SoftWhite,
    surface = Color.White,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = CarbonGray,
    onSurface = CarbonGray,
    outline = CarbonGray.copy(alpha = 0.5f)
)

@Composable
fun OmniToolboxTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    themeName: String = "default", // default, earth, forest, water, sand, nature
    dynamicColor: Boolean = true,
    accentColor: Color? = null,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        accentColor != null -> {
            if (darkTheme) {
                darkColorScheme(primary = accentColor, background = ForestNight, surface = DeepBark)
            } else {
                lightColorScheme(primary = accentColor, background = NatureWhite, surface = NatureWhite)
            }
        }
        themeName.lowercase() == "earth" -> {
            if (darkTheme) {
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
        themeName.lowercase() == "forest" -> {
            if (darkTheme) {
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
        themeName.lowercase() == "water" -> {
            if (darkTheme) {
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
        themeName.lowercase() == "sand" -> {
            if (darkTheme) {
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
        themeName.lowercase() == "nature" -> {
            if (darkTheme) {
                darkColorScheme(
                    primary = NaturePrimary,
                    secondary = NatureSecondary,
                    tertiary = NatureSurfaceDark,
                    background = NatureBgDark,
                    surface = NatureSurfaceDark,
                    onPrimary = Color.White,
                    onSecondary = Color(0xFFF5F2EB),
                    onTertiary = Color(0xFFF5F2EB),
                    onBackground = Color(0xFFF5F2EB),
                    onSurface = Color(0xFFF5F2EB),
                    outline = NaturePrimary.copy(alpha = 0.5f)
                )
            } else {
                lightColorScheme(
                    primary = NaturePrimary,
                    secondary = NatureSecondary,
                    tertiary = Color.White,
                    background = NatureBgLight,
                    surface = NatureSurfaceLight,
                    surfaceVariant = NatureSurfaceLight,
                    onPrimary = Color.White,
                    onSecondary = Color(0xFF0D1411),
                    onTertiary = Color(0xFF0D1411),
                    onBackground = Color(0xFF0D1411),
                    onSurface = Color(0xFF0D1411),
                    outline = NaturePrimary.copy(alpha = 0.5f)
                )
            }
        }
        else -> { // Default (Epic Indigo/Slate)
            val context = LocalContext.current
            if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else if (darkTheme) {
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
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
