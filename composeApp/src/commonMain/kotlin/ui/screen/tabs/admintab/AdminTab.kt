package ui.screen.tabs.admintab

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.FadeTransition
import dev.icerock.moko.biometry.BiometryAuthenticator
import dev.icerock.moko.biometry.compose.BindBiometryAuthenticatorEffect
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import ui.permissions.BiometryViewModel
import ui.theme.UnsessionThemeDev
import ui.uikit.AppTab
import utils.AppBarScreen
import utils.ScreenOptions

class AdminTab : AppTab {
    val appBarTitle = MutableStateFlow("Admin")
    override val screenOptions: ScreenOptions
        @Composable get() = ScreenOptions(
            title = "Admin",
        )


    @ExperimentalMaterial3Api
    @Composable
    override fun Content() {
        var success by remember { mutableStateOf(false) }
        val viewModel = BiometryViewModel(
            biometryAuthenticator = BiometryAuthenticator(LocalContext.current),
            eventsDispatcher = EventsDispatcher()
        )
        if (!success) {
            BindBiometryAuthenticatorEffect(viewModel.biometryAuthenticator)
            val status: BiometryViewModel.BiometryResult by viewModel.result.collectAsState()

            LaunchedEffect("bioAuth") {
                viewModel.tryToAuth(
                    title = "⚠ Доступ к админ-панели ⚠",
                    subtitle = "Требуется верификация личности",
                    failure = "Не хочу"
                )
            }

            if (status == BiometryViewModel.BiometryResult.SUCCESS) {
                success = true
            }
        } else {
            UnsessionThemeDev {
                AdminUI()
            }
        }
    }

    @Composable
    private fun AdminUI() {
        val currentScreen = remember { mutableStateOf<AppBarScreen?>(null) }
        Scaffold(
            topBar = {
                currentScreen.value?.AppBar?.invoke()
            }
        ) { paddingValues ->
            Box(Modifier.padding(paddingValues)) {
                Navigator(AdminScreenHost(appBarTitleFlow = appBarTitle)) { nav ->
                    currentScreen.value = (nav.lastItem as? AppBarScreen)
                    FadeTransition(nav)
                }
            }
        }
    }

    override val options: TabOptions
        @Composable get() = TabOptions(
            1U,
            icon = rememberVectorPainter(Icons.Default.AdminPanelSettings),
            title = "Admin"
        )
}
