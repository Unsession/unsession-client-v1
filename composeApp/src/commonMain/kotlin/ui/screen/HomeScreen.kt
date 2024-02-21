package ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import ui.FabTab
import ui.screen.tabs.AdminTab
import ui.screen.tabs.SearchTeachersTab
import ui.theme.appBarElevation

class HomeScreen : Screen {

    @Composable
    override fun Content() {
        TabNavigator(SearchTeachersTab()) { nav ->
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                content = {
                    Box(Modifier.padding(it)) {
                        CurrentTab()
                    }
                },
                floatingActionButton = {
                    (nav.current as? FabTab)?.Fab()
                },
                floatingActionButtonPosition = FabPosition.End,
                bottomBar = {
                    Surface(
                        shadowElevation = appBarElevation,
                    ) {
                        BottomNavigation(
                            modifier = Modifier.background(MaterialTheme.colorScheme.primary)
                        ) {
                            TabNavigationItem(SearchTeachersTab())
                            TabNavigationItem(AdminTab())
                        }
                    }
                }
            )
        }
    }

    @Composable
    private fun RowScope.TabNavigationItem(tab: Tab) {

        val tabNavigator = LocalTabNavigator.current

        BottomNavigationItem(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            selected = tabNavigator.current == tab,
            onClick = { tabNavigator.current = tab },
            icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title) }
        )
    }
}