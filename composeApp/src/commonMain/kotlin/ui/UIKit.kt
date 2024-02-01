package ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ClickableCard(modifier: Modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(), content: @Composable () -> Unit, onClick: () -> Unit) {
    Card(modifier = modifier) {
        Box(Modifier.fillMaxSize().clickable { onClick() }) {
            Column(Modifier.padding(8.dp)) {
                content()
            }
        }
    }
}