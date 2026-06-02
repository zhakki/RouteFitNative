package com.example.routefitnative.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = RouteFitAccent,
    onPrimary = RouteFitOnAccent,
    secondary = RouteFitTextSecondary,
    onSecondary = RouteFitBackground,
    background = RouteFitBackground,
    onBackground = RouteFitTextPrimary,
    surface = RouteFitSurface,
    onSurface = RouteFitTextPrimary,
    surfaceVariant = RouteFitSurfaceVariant,
    onSurfaceVariant = RouteFitTextSecondary,
    outline = RouteFitOutline,
    outlineVariant = RouteFitOutline
)

@Composable
fun RouteFitNativeTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
