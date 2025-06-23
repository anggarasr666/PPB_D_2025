package com.example.otp.ui.theme

import android.app.Activity
import android.os.Build
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

private val StarbucksDarkColorScheme = darkColorScheme(
    primary = StarbucksGreen,
    onPrimary = StarbucksWhite,
    secondary = StarbucksCopper,
    onSecondary = StarbucksBlack,
    tertiary = StarbucksGreenLight,
    onTertiary = StarbucksBlack,
    background = StarbucksBlack,
    onBackground = StarbucksWhite,
    surface = Color(0xFF1C1C1C),
    onSurface = StarbucksWhite,
    error = StarbucksError,
    onError = StarbucksWhite
)

private val StarbucksLightColorScheme = lightColorScheme(
    primary = StarbucksGreen,
    onPrimary = StarbucksWhite,
    secondary = StarbucksCopper,
    onSecondary = StarbucksBlack,
    tertiary = StarbucksGreenLight,
    onTertiary = StarbucksBlack,
    background = StarbucksWhite,
    onBackground = StarbucksBlack,
    surface = StarbucksWhite,
    onSurface = StarbucksBlack,
    surfaceVariant = StarbucksLightGray,
    onSurfaceVariant = StarbucksGray,
    error = StarbucksError,
    onError = StarbucksWhite,
    outline = StarbucksGray
)

@Composable
fun OtpTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        StarbucksDarkColorScheme
    } else {
        StarbucksLightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}