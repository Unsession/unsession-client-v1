package ui

import androidx.compose.animation.core.EaseOutQuart
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import api.ApiClient
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.apu.unsession.MR
import dev.icerock.moko.resources.compose.painterResource
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import settings.SettingsRepo
import ui.screen.HomeScreen
import ui.screen.loginflow.LoginScreen
import ui.screen.loginflow.RegistrationScreen

object SplashScreen : Screen {
    @Composable
    override fun Content() {
        val nav = LocalNavigator.current!!
        Box(Modifier.fillMaxSize().padding(32.dp)) {
            var splashFinish by remember { mutableStateOf(false) }
            SplashScreenUI {
                splashFinish = true
            }
            if (SettingsRepo.cachedLoginData() != null) {
                runBlocking {
                    ApiClient.Users.login(
                        SettingsRepo.cachedLoginData()!!,
                        onSuccess = {
                            nav.push(HomeScreen())
                        }, onFailure = {
                            nav.push(LoginScreen())
                        }
                    )
                }
            } else {
                nav.push(RegistrationScreen())
            }
        }
    }
}

@Composable
fun SplashScreenUI(onFinished: () -> Unit) {
    var imageSize by remember { mutableStateOf(160.dp) }

    LaunchedEffect(Unit) {
        delay(1)
        imageSize = 420.dp
        delay(400)
        onFinished()
    }

    val animatedSize by animateDpAsState(
        targetValue = imageSize,
        animationSpec = tween(600, easing = EaseOutQuart, delayMillis = 0),
        label = ""
    )

    val alpha by animateFloatAsState(
        targetValue = 1f, animationSpec = tween(600, delayMillis = 0), label = ""
    )

    Box(Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
        Image(
            painter = painterResource(MR.images.logo512),
            contentDescription = "Splash",
            modifier = Modifier.size(animatedSize).alpha(alpha)
        )
    }
}
