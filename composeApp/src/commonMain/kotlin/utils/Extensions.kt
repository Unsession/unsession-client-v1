package utils

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import cafe.adriel.voyager.core.screen.Screen
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Long.toDateText(): String {
    val dateTime =
        LocalDateTime.ofInstant(java.time.Instant.ofEpochSecond(this), ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
    return dateTime.format(formatter)
}

data class ScreenOptions(
    var title: String,
)

interface OptScreen : Screen {
    val screenOptions: ScreenOptions @Composable get
}

interface AppBarScreen : Screen, OptScreen {
    val AppBar: @Composable () -> Unit
        @Composable get
}

@Composable
fun clipboard(): ClipboardManager {
    return LocalClipboardManager.current
}

fun ClipboardManager.setText(text: String) {
    setText(AnnotatedString(text))
}

data class Email(
    val to: String,
    val subject: String,
    val body: String,
)

fun sendEmail(ctx: Context, email: Email) {
    val i = Intent(Intent.ACTION_SEND).apply {
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email.to))
        putExtra(Intent.EXTRA_SUBJECT, email.subject)
        putExtra(Intent.EXTRA_TEXT, email.body)
        type = "message/rfc822"
    }
    ctx.startActivity(Intent.createChooser(i, "Choose an Email client : "))
}
