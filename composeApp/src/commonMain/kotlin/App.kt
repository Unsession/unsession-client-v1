
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import ui.screen.LoginScreen
import ui.screen.RegistrationScreen
import ui.screen.SplashScreen

val loginFlowScreens = listOf(
    LoginScreen, RegistrationScreen
)

@Composable
fun App() {
    MaterialTheme {
        Box(Modifier.fillMaxSize()) {
            Navigator(
                screen = SplashScreen,
                onBackPressed = ::canCallBackPressed
            )
        }
    }
}

fun canCallBackPressed(s: Screen): Boolean = !loginFlowScreens.contains(s)
