package ui.screen.tabs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.apu.unsession.MR
import dev.icerock.moko.biometry.BiometryAuthenticator
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import ui.permissions.BiometryScreen
import ui.permissions.BiometryViewModel
import ui.screen.tabs.admintab.AdminScreen
import ui.uikit.AppTab
import utils.ScreenOptions

class AdminTab : AppTab {
    override val screenOptions: ScreenOptions
        @Composable get() = ScreenOptions(
            title = "Admin",
        )

    @Composable
    override fun Content() {
        var success by remember { mutableStateOf(false) }
        if (!success) {
            BiometryScreen(viewModel = BiometryViewModel(
                biometryAuthenticator = BiometryAuthenticator(LocalContext.current),
                eventsDispatcher = EventsDispatcher()
            )
            ) {
                success = true
            }
        } else {
            AdminScreen()
        }
    }

    override val options: TabOptions
        @Composable get() = TabOptions(
            1U,
            icon = painterResource(MR.images.logo512.drawableResId),
            title = "Admin"
        )
}
