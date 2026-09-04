package com.codebuzz.app.bellyreset.ui.theme

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
    primary = Turmeric,
    secondary = PineLight,
    tertiary = Pine,
    background = PineDark,
    surface = PineDark,
    onPrimary = PineDark,
    onSecondary = PineDark,
    onTertiary = TurmericLight,
    onBackground = SoftCream,
    onSurface = SoftCream
)

private val LightColorScheme = lightColorScheme(
    primary = Pine,
    secondary = Turmeric,
    tertiary = TurmericDark,
    background = SoftCream,
    surface = SoftCream,
    onPrimary = SoftCream,
    onSecondary = PineDark,
    onTertiary = SoftCream,
    onBackground = PineDark,
    onSurface = PineDark
)

@Composable
fun BellyResetTheme(
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
        typography = Typography,
        content = content
    )
}
