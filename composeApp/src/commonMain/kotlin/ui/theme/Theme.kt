package ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.example.rally.ui.theme.Blue100
import com.example.rally.ui.theme.Purple700

val DarkColorScheme = darkColorScheme(
    primary = Green500,
    onPrimary = Color.White,
    surface = DarkBlue900,
    onSurface = Color.White,
    background = DarkBlue900,
    onBackground = Color.White,
    error = Red600,
    onError = Color.White,
)

val LightColorScheme = lightColorScheme(
    primary = Green500,
    onPrimary = Color.White,
    primaryContainer = Purple700,
    secondary = Blue100,
    background = Color.White,
    surface = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
)
