package ui.screen.tabs

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Reviews
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.FadeTransition
import com.apu.unsession.MR
import ui.screen.tabs.teacherstab.SearchTeachersScreen
import ui.uikit.AppTab
import utils.AppBarScreen
import utils.ScreenOptions


class SearchTeachersTab : AppTab {
    override val screenOptions: ScreenOptions
        @Composable get() = ScreenOptions(
            title = MR.strings.reviews.getString(LocalContext.current)
        )

    override val options: TabOptions
        @Composable get() = TabOptions(
            index = 0U,
            title = "Reviews",
            icon = rememberVectorPainter(Icons.Default.Reviews),
        )

    @Composable
    override fun Content() {
        val currentScreen = remember { mutableStateOf<AppBarScreen?>(null) }
        Box {
            Navigator(SearchTeachersScreen(), onBackPressed = { true }) {
                currentScreen.value = (it.lastItem as? AppBarScreen)
                FadeTransition(it)
            }
        }
    }
}
