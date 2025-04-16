package com.example.trackingexpenses.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.trackingexpenses.R

val MontserratFontFamily = FontFamily(
    Font(R.font.montserrat, FontWeight.Normal),
    Font(R.font.montserrat_light, FontWeight.Light),
    Font(R.font.montserrat_medium, FontWeight.Medium),
)

val ComfortaaFontFamily = FontFamily(
    Font(R.font.comfortaa, FontWeight.Normal)
)

fun createTypography(isDarkTheme: Boolean): Typography {
    return Typography(
        bodyMedium = TextStyle(
            fontFamily = MontserratFontFamily,
            fontWeight = FontWeight.Light,
            color = textColor(isDarkTheme),
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.5.sp
        ),
        titleLarge = TextStyle(
            fontFamily = MontserratFontFamily,
            fontWeight = FontWeight.Medium,
            color = textColor(isDarkTheme),
            fontSize = 20.sp,
        ),
        titleMedium = TextStyle(
            fontFamily = MontserratFontFamily,
            fontWeight = FontWeight.Light,
            color = textColor(isDarkTheme),
            fontSize = 18.sp,
        ),
        labelSmall = TextStyle(
            fontFamily = MontserratFontFamily,
            fontWeight = FontWeight.Light,
            color = textColor(isDarkTheme),
            fontSize = 14.sp,
        ),
    )
}