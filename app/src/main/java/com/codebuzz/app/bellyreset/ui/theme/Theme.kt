package com.codebuzz.app.bellyreset.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PineBright,
    onPrimary = OnPineBright,
    primaryContainer = Pine,
    onPrimaryContainer = PineContainer,
    secondary = Turmeric,
    onSecondary = OnTurmericContainer,
    secondaryContainer = TurmericContainerDark,
    onSecondaryContainer = TurmericContainer,
    tertiary = TurmericLight,
    onTertiary = OnTurmericContainer,
    tertiaryContainer = TurmericContainerDark,
    onTertiaryContainer = TurmericContainer,
    background = Night,
    onBackground = OnNight,
    surface = Night,
    onSurface = OnNight,
    surfaceVariant = NightVariant,
    onSurfaceVariant = OnNightVariant,
    outline = NightOutline,
    outlineVariant = NightVariant,
    surfaceContainerLowest = NightContainerLowest,
    surfaceContainerLow = NightContainerLow,
    surfaceContainer = NightContainer,
    surfaceContainerHigh = NightContainerHigh,
    surfaceContainerHighest = NightContainerHighest
)

private val LightColorScheme = lightColorScheme(
    primary = Pine,
    onPrimary = SoftCream,
    primaryContainer = PineContainer,
    onPrimaryContainer = PineDark,
    secondary = TurmericDeep,
    onSecondary = SoftCream,
    secondaryContainer = TurmericContainer,
    onSecondaryContainer = OnTurmericContainer,
    tertiary = TurmericDark,
    onTertiary = SoftCream,
    tertiaryContainer = TurmericContainer,
    onTertiaryContainer = OnTurmericContainer,
    background = SoftCream,
    onBackground = PineDark,
    surface = SoftCream,
    onSurface = PineDark,
    surfaceVariant = CreamVariant,
    onSurfaceVariant = OnCreamVariant,
    outline = CreamOutline,
    outlineVariant = CreamOutlineVariant,
    surfaceContainerLowest = Color.White,
    surfaceContainerLow = CreamContainerLow,
    surfaceContainer = CreamContainer,
    surfaceContainerHigh = CreamContainerHigh,
    surfaceContainerHighest = CreamContainerHighest
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
