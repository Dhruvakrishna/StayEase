package com.example.stayease.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Premium Color Palette
private val PrimaryColor = Color(0xFFE81948) // Airbnb-style Rose
private val SecondaryColor = Color(0xFF00A699) // Teal accent
private val BackgroundLight = Color(0xFFF7F7F7)
private val BackgroundDark = Color(0xFF121212)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD8E4),
    onPrimaryContainer = Color(0xFF31111D),
    secondary = SecondaryColor,
    onSecondary = Color.White,
    surface = Color.White,
    onSurface = Color(0xFF222222),
    background = BackgroundLight,
    onBackground = Color(0xFF222222),
    outline = Color(0xFFDDDDDD)
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryColor,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF910025),
    onPrimaryContainer = Color(0xFFFFD8E4),
    secondary = SecondaryColor,
    onSecondary = Color.White,
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFE3E3E3),
    background = BackgroundDark,
    onBackground = Color(0xFFE3E3E3),
    outline = Color(0xFF333333)
)

private val Typography = Typography(
    displayMedium = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 32.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        letterSpacing = 0.1.sp
    )
)

@Composable
fun StayEaseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
