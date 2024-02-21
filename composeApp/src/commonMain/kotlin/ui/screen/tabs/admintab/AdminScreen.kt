package ui.screen.tabs.admintab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.sharp.CrisisAlert
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import ui.uikit.RailNavItem
import utils.AppBarScreen
import utils.ScreenOptions

class AdminScreenModel : ScreenModel {

}

class AdminScreen(
    val model: AdminScreenModel = AdminScreenModel()
) : AppBarScreen, Screen {
    override val screenOptions: ScreenOptions
        @Composable get() = ScreenOptions(
            title = "Admin"
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
                    IconButton(onClick = { nav.pop() }, content = {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    })
                },
                modifier = Modifier.shadow(8.dp)
            )
        }

    @Composable
    override fun Content() {
        val options = screenOptions
        Column {
            Row {
                NavigationRail(
                    Modifier.fillMaxHeight().width(70.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    RailNavItem(
                        icon = Icons.Sharp.CrisisAlert,
                        buttonColor = MaterialTheme.colorScheme.error,
                        onClick = {

                        })
                }
                Box(Modifier.fillMaxSize()) {
                    Button(onClick = { options.title = "Admin0" }) {
                        Text("Admin0")
                    }
                }
            }
        }
    }
}