package ui.screen.tabs.searchteachers

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Reviews
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.FadeTransition
import com.apu.unsession.MR
import ui.AppTab
import ui.OptTab
import utils.ScreenOptions


class SearchTeachersTab : OptTab, AppTab {
    override var appBarVisibility: MutableState<Boolean> = mutableStateOf(true)
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
        Navigator(SearchTeachersScreen()) {
            FadeTransition(it)
        }
    }
}
