package ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.enterAlwaysScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import ui.screen.tabs.FabTab
import ui.screen.tabs.SearchTeachersTab

object HomeScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        TabNavigator(SearchTeachersTab()) { nav ->
            val options = nav.current.options
            val appBarState = rememberTopAppBarState()
            val scrollBehavior = enterAlwaysScrollBehavior(appBarState)
            Scaffold(
                modifier = Modifier.fillMaxSize().nestedScroll(scrollBehavior.nestedScrollConnection),
                topBar = {
                    TopAppBar(
                        modifier = Modifier.fillMaxWidth(),
                        colors = TopAppBarDefaults.topAppBarColors(),
                        title = {
                            Text(text = options.title, modifier = Modifier.padding(8.dp))
                        },
                        navigationIcon = {},
                        actions = {},
                        scrollBehavior = scrollBehavior,
                    )
                },
                content = {
                    Box(
                        Modifier.padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = it.calculateTopPadding(),
                            bottom = it.calculateBottomPadding()
                        ).fillMaxSize()
                    ) {
                        CurrentTab()
                    }
                },
                floatingActionButton = {
                    (nav.current as? FabTab)?.Fab()
                },
                floatingActionButtonPosition = FabPosition.End,
                bottomBar = {
                    BottomNavigation {
                        TabNavigationItem(SearchTeachersTab())
                    }
                }
            )
        }
    }

    @Composable
    private fun RowScope.TabNavigationItem(tab: Tab) {
        val tabNavigator = LocalTabNavigator.current

        BottomNavigationItem(
            selected = tabNavigator.current == tab,
            onClick = { tabNavigator.current = tab },
            icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title) }
        )
    }
}