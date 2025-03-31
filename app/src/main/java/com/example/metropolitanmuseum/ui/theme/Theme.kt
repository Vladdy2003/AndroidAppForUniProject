package com.example.metropolitanmuseum.ui.theme

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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// Schema de culori pentru tema light
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = Primary.copy(alpha = 0.1f),
    onPrimaryContainer = Primary,
    secondary = Secondary,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = Secondary.copy(alpha = 0.1f),
    onSecondaryContainer = Secondary,
    tertiary = MuseumGold,
    onTertiary = androidx.compose.ui.graphics.Color.Black,
    background = MuseumLightGray,
    onBackground = MuseumDarkGray,
    surface = androidx.compose.ui.graphics.Color.White,
    onSurface = MuseumDarkGray,
    error = ErrorColor,
    onError = androidx.compose.ui.graphics.Color.White
)

// Schema de culori pentru tema dark
private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = DarkPrimary.copy(alpha = 0.2f),
    onPrimaryContainer = androidx.compose.ui.graphics.Color.White,
    secondary = DarkSecondary,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = DarkSecondary.copy(alpha = 0.2f),
    onSecondaryContainer = androidx.compose.ui.graphics.Color.White,
    tertiary = MuseumGold.copy(alpha = 0.8f),
    onTertiary = androidx.compose.ui.graphics.Color.Black,
    background = androidx.compose.ui.graphics.Color(0xFF121212),
    onBackground = androidx.compose.ui.graphics.Color.White,
    surface = androidx.compose.ui.graphics.Color(0xFF1E1E1E),
    onSurface = androidx.compose.ui.graphics.Color.White,
    error = ErrorColor,
    onError = androidx.compose.ui.graphics.Color.White
)

@Composable
fun MetropolitanMuseumTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Opțional pentru a folosi culorile dinamice pe Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Folosește Dynamic Color dacă este activat și dispozitivul suportă
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Alege schema de culori în funcție de tema system (dark sau light)
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Opțional: Setarea culorii pentru bara de stare
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
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