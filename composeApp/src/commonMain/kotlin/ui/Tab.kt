package ui

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.Tab

interface FabTab : Tab {
    @Composable
    fun Fab() {
    }
}