package utils

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import java.io.Serializable
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Long.toDateText(): String {
    val dateTime = LocalDateTime.ofInstant(java.time.Instant.ofEpochSecond(this), ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    return dateTime.format(formatter)
}

//public actual interface Screen : Serializable {
//    public actual val key: ScreenKey
//        get() = commonKeyGeneration()
//
//    @Composable
//    public actual fun Content()
//}

public data class ScreenOptions(
    val title: String,
)

interface OptScreen : Screen, Serializable {
    public val screenOptions : ScreenOptions
        @Composable get
}