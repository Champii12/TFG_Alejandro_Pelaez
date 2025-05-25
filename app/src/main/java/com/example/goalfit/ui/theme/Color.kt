package com.example.goalfit.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val Gray = Color(0xFF575757)
val Black = Color(0xFF121212)
val White = Color(0xFFFFFFFF)
val Green = Color(0xFF49dd63)
val Red = Color(0xFFff4d4d)
val Blue = Color(0xFF4d4dff)
val Yellow = Color(0xFFffcc00)
val Orange = Color(0xFFff9933)
val Cyan = Color(0xFF00ffff)
val Magenta = Color(0xFFff00ff)
val Teal = Color(0xFF00ffcc)
val Indigo = Color(0xFF4b0082)
val LightGray = Color(0xFFABABAB)
val LightBlue = Color(0xFFadd8e6)
val LightGreen = Color(0xFF90ee90)
val LightPink = Color(0xFFffb6c1)
val LightPurple = Color(0xFF9370db)
val LightRed = Color(0xFFfa8072)
val LightYellow = Color(0xFFffffe0)
val LightOrange = Color(0xFFffa07a)
val LightCyan = Color(0xFFe0ffff)
val LightMagenta = Color(0xFFff77ff)
val LightTeal = Color(0xFFe0ffff)
val LightIndigo = Color(0xFF9370db)
val DarkBlue = Color(0xFF00008b)
val DarkGreen = Color(0xFF006400)
val DarkPink = Color(0xFFc71585)
val DarkPurple = Color(0xFF800080)
val DarkRed = Color(0xFF8b0000)
val DarkYellow = Color(0xFFbdb76b)
val DarkOrange = Color(0xFFff8c00)
val DarkCyan = Color(0xFF008b8b)
val DarkMagenta = Color(0xFF8b008b)
val DarkTeal = Color(0xFF008080)
val DarkIndigo = Color(0xFF4b0082)
val DarkLightBlue = Color(0xFFadd8e6)
val DarkLightGreen = Color(0xFF90ee90)

val selectedColor = Color(0xFF7c7c7c)
val unselectedColor = Color(0xFF4a4a4a)

object AppColors {
    /* Gradiente principal reutilizando colores propios */
    @Composable
    fun gradient() = Brush.verticalGradient(
        colors = listOf(Orange, LightOrange, LightRed)
    )

    /* Theme-aware backgrounds */
    @Composable fun cardBg() = if (isSystemInDarkTheme()) Black else White
    @Composable fun cardTransparent()= if (isSystemInDarkTheme()) Black.copy(alpha = .12f)
    else White.copy(alpha = .15f)
    @Composable fun accent() = Orange     // o LightOrange / lo que desees
    @Composable fun textPrimary() = if (isSystemInDarkTheme()) White else Gray
    @Composable fun textSecondary() = if (isSystemInDarkTheme()) LightGray else LightGray
    @Composable fun iconTint() = if (isSystemInDarkTheme()) White else White
}