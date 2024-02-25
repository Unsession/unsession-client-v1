package ui.screen.tabs.admintab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import api.models.Access
import api.models.usersAccess
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import kotlinx.coroutines.flow.Flow
import ui.permissions.WithAnyPermission
import ui.permissions.WithPermission
import ui.theme.defaultPadding
import utils.AppBarScreen
import utils.ScreenOptions

class AdminScreenHostModel : ScreenModel {

}

class AdminScreenHost(
    val model: AdminScreenHostModel = AdminScreenHostModel(),
    val appBarTitleFlow: Flow<String>
) : AppBarScreen() {
    override val screenOptions: ScreenOptions
        @Composable get() = ScreenOptions(
            title = "Admin"
        )

    @OptIn(ExperimentalMaterial3Api::class)
    override val AppBar: @Composable () -> Unit
        @Composable get() = {
            val appBarTitle by appBarTitleFlow.collectAsState(screenOptions.title)
            val nav = LocalNavigator.current!!
            TopAppBar(
                title = {
                    Text(appBarTitle)
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
        var startScreen: Tab = ServerControlTab()
        WithAnyPermission(Access.SS) {
            startScreen = ServerControlTab()
        }
        WithAnyPermission(permissions = usersAccess) {
            startScreen = UsersControlTab()
        }
        TabNavigator(startScreen) { nav ->
            Row {
                NavigationRail(
                    Modifier.fillMaxHeight().width(70.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                ) {
                    WithAnyPermission(permissions = usersAccess) {
                        DevNavItem(UsersControlTab())
                    }
                    WithPermission(Access.SS) {
                        DevNavItem(ServerControlTab())
                    }
                }
                Box(Modifier.fillMaxSize().padding(defaultPadding)) {
                    nav.current.Content()
                }
            }
        }
    }

    @Composable
    private fun DevNavItem(tab: Tab) {
        val tabNavigator = LocalTabNavigator.current

        NavigationRailItem(
            selected = tabNavigator.current.key == tab.key,
            icon = { Icon(tab.options.icon!!, contentDescription = null) },
            label = { Text(tab.options.title) },
            onClick = {
                tabNavigator.current = tab
            }
        )
    }
}