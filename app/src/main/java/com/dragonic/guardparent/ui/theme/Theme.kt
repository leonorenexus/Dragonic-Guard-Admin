package com.dragonic.guardparent.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Typography

val PBlack       = Color(0xFF050810)
val PDeepBlue    = Color(0xFF0A0F1E)
val PNavy        = Color(0xFF0D1B3E)
val PGlass       = Color(0x1A4FC3F7)
val PGlassBorder = Color(0x334FC3F7)
val PCyan        = Color(0xFF4FC3F7)
val PPurple      = Color(0xFF7C4DFF)
val PRed         = Color(0xFFEF5350)
val PGreen       = Color(0xFF66BB6A)
val PAmber       = Color(0xFFFFCA28)
val PWhite       = Color(0xFFE8F4FD)
val PWhiteDim    = Color(0xFF90CAF9)
val PSurface     = Color(0x0DFFFFFF)

private val DarkColors = darkColorScheme(
    primary          = PCyan,
    onPrimary        = PBlack,
    secondary        = PPurple,
    background       = PBlack,
    onBackground     = PWhite,
    surface          = PDeepBlue,
    onSurface        = PWhite,
    surfaceVariant   = PGlass,
    error            = PRed,
)

val ParentTypography = Typography(
    displayLarge  = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        letterSpacing = 2.sp
    ),
    titleLarge    = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 20.sp
    ),
    titleMedium   = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    bodyLarge     = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp
    ),
    bodySmall     = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        color = PWhiteDim
    ),
    labelSmall    = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        letterSpacing = 1.2.sp,
        color = PCyan
    ),
)

@Composable
fun DRAGONICParentTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        typography = ParentTypography,
        content = content
    )
}
