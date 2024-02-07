package ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import cafe.adriel.voyager.navigator.tab.Tab

interface FabTab : Tab {
    @Composable
    fun Fab() {
    }
}

interface AppTab : Tab {
    var appBarVisibility : MutableState<Boolean>
}