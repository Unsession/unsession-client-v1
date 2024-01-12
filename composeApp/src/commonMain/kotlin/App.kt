import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.Navigator
import ui.screen.LoginScreen

@Composable
fun App() {
    MaterialTheme {
        Box(Modifier.fillMaxSize().padding(8.dp)) {
            Navigator(LoginScreen())
        }
    }
}
