package com.example.trackingexpenses.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = SlateGray, // AppBarColor
    secondary = MainMedium, // Buttons
    tertiary = MainWhite, // Text
    background = Black,
    primaryContainer = DarkBrown,
    error = Error,
    outlineVariant = Yellow,
)

private val LightColorScheme = lightColorScheme(
    primary = LightSlateGray,
    secondary = LightBrown,
    tertiary = DarkGray,
    background = White,
    primaryContainer = Beige,
    error = Error
)

val textColor = { isDarkTheme: Boolean -> if (isDarkTheme) DarkColorScheme.tertiary else LightColorScheme.tertiary }

@Composable
fun TrackingExpensesTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = createTypography(isDarkTheme = darkTheme),
        content = content
    )
}