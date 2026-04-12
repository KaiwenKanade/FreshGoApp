package com.example.freshgoapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryFigmaGreen,
    secondary = PrimaryGreen,
    tertiary = NeutralSurface
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryFigmaGreen,
    secondary = PrimaryGreen,
    tertiary = NeutralSurface,
    background = Color.White,
    surface = SurfaceCardColor
)

@Composable
fun FreshGoAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography, // Pastikan file Type.kt Anda tidak error
        content = content
    )
}