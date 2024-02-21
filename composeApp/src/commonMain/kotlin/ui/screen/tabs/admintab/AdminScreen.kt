package ui.screen.tabs.admintab

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import utils.OptScreen
import utils.ScreenOptions

class AdminScreenModel: ScreenModel {

}
class AdminScreen(
    val model: AdminScreenModel = AdminScreenModel()
) : OptScreen, Screen {
    override val screenOptions: ScreenOptions
        @Composable get() = ScreenOptions(
            title = "Admin"
        )

    @Composable
    override fun Content() {

    }

}