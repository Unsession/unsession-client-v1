package ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

val appBarElevation = 16.dp

@Composable
fun UnsessionTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = when {
        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colors,
        content = content,
        shapes = Shapes,
        typography = Typography
    )
}

@Composable
fun UnsessionThemeDev(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = when {
        useDarkTheme -> DevDarkColorScheme
        else -> DevLightColorScheme
    }

    MaterialTheme(
        colorScheme = colors,
        content = content,
        shapes = Shapes,
        typography = TypographyUbuntu
    )
}
