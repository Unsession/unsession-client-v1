package utils

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import java.io.Serializable
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Long.toDateText(): String {
    val dateTime =
        LocalDateTime.ofInstant(java.time.Instant.ofEpochSecond(this), ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    return dateTime.format(formatter)
}

public data class ScreenOptions(
    val title: String,
)

interface OptScreen : Screen, Serializable {
    val screenOptions: ScreenOptions @Composable get
}

interface AppBarScreen : Screen, OptScreen {
    val AppBar: @Composable () -> Unit
        @Composable get
}