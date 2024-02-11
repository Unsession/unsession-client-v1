import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import ui.SplashScreen
import ui.screen.loginflow.LoginScreen
import ui.screen.loginflow.RegistrationScreen
import ui.theme.UnsessionTheme

val loginFlowScreens = listOf(
    LoginScreen, RegistrationScreen
)

@Composable
fun App() {
    UnsessionTheme {
        Navigator(
            screen = SplashScreen,
            onBackPressed = ::canCallBackPressed
        )
    }
}

fun canCallBackPressed(s: Screen): Boolean = !loginFlowScreens.contains(s)
