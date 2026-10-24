package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = PureWhite,
    primaryContainer = LightGreenBackground,
    onPrimaryContainer = SecondaryGreen,
    secondary = SecondaryGreen,
    onSecondary = PureWhite,
    secondaryContainer = LightGreenBackground,
    onSecondaryContainer = SecondaryGreen,
    background = PureWhite,
    onBackground = PrimaryBlack,
    surface = PureWhite,
    onSurface = PrimaryBlack,
    surfaceVariant = PureWhite,
    onSurfaceVariant = GrayTextSecondary,
    outline = BorderGray
)

@Composable
fun DailyAdviceTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        typography = Typography,
        content = content
    )
}
