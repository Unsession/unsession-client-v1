package ui.screen.tabs.inner

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import api.models.Teacher
import cafe.adriel.voyager.navigator.LocalNavigator
import com.apu.unsession.MR
import dev.icerock.moko.resources.format
import utils.AppBarScreen
import utils.OptScreen
import utils.ScreenOptions

class AddReviewScreen(val teacher: Teacher) : AppBarScreen, OptScreen {
    override val screenOptions: ScreenOptions
        @Composable get() = ScreenOptions(
            title = MR.strings.review_on.format(teacher.name).toString(LocalContext.current)
        )

    @OptIn(ExperimentalMaterial3Api::class)
    override val AppBar: @Composable () -> Unit
        @Composable get() = {
            val nav = LocalNavigator.current!!
            TopAppBar(
                title = {
                    Text(screenOptions.title)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        nav.pop()
                    }, content = {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    })
                }
            )
        }

    @Composable
    override fun Content() {
        val nav = LocalNavigator.current!!
        Scaffold(
            topBar = AppBar,
        ) {
            Column(Modifier.padding(it)) {

            }
        }
    }
}
