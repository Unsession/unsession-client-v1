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
//    val context = LocalContext.current
    val colors = when {
//        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) -> {
//            if (useDarkTheme) dynamicDarkColorScheme(context)
//            else dynamicLightColorScheme(context)
//        }
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
