// File: ui/theme/Theme.kt
package com.example.thermalya.ui.theme

import android.app.Activity
import android.os.Build
//import androidx.compose.foundation.isSystemInDarkMode
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
import com.example.thermalya.ui.theme.Typography

private val ThermalyaPrimaryColor = Color(0xFF9B6BA8)
private val ThermalyaPrimaryLight = Color(0xFFD4A5D9)
private val ThermalyaAccent = Color(0xFFE6B3D3)
private val ThermalyaBackground = Color(0xFFFFFFFF)
private val ThermalyaText = Color(0xFF2C2C2C)

private val LightColorScheme = lightColorScheme(
    primary = ThermalyaPrimaryColor,
    onPrimary = Color.White,
    primaryContainer = ThermalyaPrimaryLight,
    onPrimaryContainer = ThermalyaText,
    secondary = ThermalyaAccent,
    onSecondary = Color.White,
    secondaryContainer = ThermalyaPrimaryLight.copy(alpha = 0.2f),
    onSecondaryContainer = ThermalyaText,
    tertiary = ThermalyaAccent,
    onTertiary = Color.White,
    tertiaryContainer = ThermalyaAccent.copy(alpha = 0.2f),
    onTertiaryContainer = ThermalyaText,
    error = Color(0xFFB3261E),
    errorContainer = Color(0xFFF9DEDC),
    onError = Color.White,
    onErrorContainer = Color(0xFF410E0B),
    background = ThermalyaBackground,
    onBackground = ThermalyaText,
    surface = Color.White,
    onSurface = ThermalyaText,
    surfaceVariant = ThermalyaPrimaryLight.copy(alpha = 0.1f),
    onSurfaceVariant = Color(0xFF666666),
    outline = Color(0xFFE0E0E0),
    inverseOnSurface = Color.White,
    inverseSurface = ThermalyaText,
    inversePrimary = ThermalyaPrimaryLight,
    surfaceTint = ThermalyaPrimaryColor,
    scrim = Color.Black,
)

private val DarkColorScheme = darkColorScheme(
    primary = ThermalyaPrimaryLight,
    onPrimary = ThermalyaPrimaryColor,
    primaryContainer = ThermalyaPrimaryColor,
    onPrimaryContainer = ThermalyaPrimaryLight,
    secondary = ThermalyaAccent,
    onSecondary = ThermalyaPrimaryColor,
    secondaryContainer = ThermalyaAccent.copy(alpha = 0.2f),
    onSecondaryContainer = ThermalyaPrimaryLight,
    tertiary = ThermalyaAccent,
    onTertiary = ThermalyaPrimaryColor,
    tertiaryContainer = ThermalyaAccent.copy(alpha = 0.2f),
    onTertiaryContainer = ThermalyaPrimaryLight,
    error = Color(0xFFF2B8B5),
    errorContainer = Color(0xFF8C1d18),
    onError = Color(0xFF601410),
    onErrorContainer = Color(0xFFF9DEDC),
    background = Color(0xFF1C1B1F),
    onBackground = Color.White,
    surface = Color(0xFF1C1B1F),
    onSurface = Color.White,
    surfaceVariant = ThermalyaPrimaryColor.copy(alpha = 0.1f),
    onSurfaceVariant = Color(0xFFB0B0B0),
    outline = Color(0xFF5E5E5E),
    inverseOnSurface = Color(0xFF1C1B1F),
    inverseSurface = Color.White,
    inversePrimary = ThermalyaPrimaryColor,
    surfaceTint = ThermalyaPrimaryLight,
    scrim = Color.Black,
)

@Composable
fun ThermalyaTheme(
    darkTheme: Boolean = false,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view)?.isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}