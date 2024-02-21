package ui.uikit

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lan
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import api.models.User
import java.time.format.DateTimeFormatter

@Composable
fun RailNavItem(icon: ImageVector, tint: Color = LocalContentColor.current, buttonColor: Color = MaterialTheme.colors.secondary, onClick: () -> Unit) {
    FloatingActionButton(onClick = onClick, containerColor = buttonColor, modifier = Modifier.padding(4.dp), shape = RoundedCornerShape(4.dp)) {
        Icon(icon, tint = tint, contentDescription = null)
    }
}

@Composable
fun UserCard(user: User, onClick: () -> Unit) {
    @Composable
    fun info(icon: ImageVector, text: String) {
        Row {
            Icon(icon, contentDescription = icon.name)
            Text(text)
        }
    }
    ClickableCard(onClick = onClick, content = {
        info(Icons.Default.Tag, user.id.toString())
        info(Icons.Default.Person, user.name)
        val login = user.userLoginData
        info(Icons.Default.Email, login?.email.toString())
        info(Icons.Default.AccountCircle, login?.username.toString())
        info(Icons.Default.Key, user.roleName)
        val datetime = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
        if (user.isBanned) {
            Text(
                "Забанен до ${datetime.parse(user.banData?.bannedUntil.toString())} по причине ${user.banData?.bannedReason}; ещё ${
                    user.banData?.bannedUntil?.minus(
                        System.currentTimeMillis() / 1000
                    )!! / 60 / 60
                } часов"
            )
        }
        info(Icons.Default.Lan, user.lastIp.toString())
        info(Icons.Default.Login, user.lastLogin.toString())
        info(Icons.Default.Cake, datetime.parse(user.created.toString()).toString())
    })
}
