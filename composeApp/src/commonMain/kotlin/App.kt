import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import ui.SplashScreen
import ui.screen.loginflow.LoginScreen
import ui.screen.loginflow.RegistrationScreen

val loginFlowScreens = listOf(
    LoginScreen, RegistrationScreen
)

@Composable
fun App() {
    MaterialTheme {
        Navigator(
            screen = SplashScreen,
            onBackPressed = ::canCallBackPressed
        )
    }
}

fun canCallBackPressed(s: Screen): Boolean = !loginFlowScreens.contains(s)
