package ui.screen.tabs.admintab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Block
import androidx.compose.material.icons.sharp.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import api.ApiClient
import api.models.User
import app.cash.paging.compose.collectAsLazyPagingItems
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import kotlinx.coroutines.launch
import ui.theme.defaultPadding
import ui.uikit.MiniTitle
import ui.uikit.Section
import ui.uikit.UserCard
import utils.PagerViewModel

class UsersControlTab(
    val vm: PagerViewModel<User> = PagerViewModel<User>(
        request = { page, pageSize ->
            ApiClient.Admin.Users.getUsers(page, pageSize)
        }
    )
) : Tab {
    override val options: TabOptions
        @Composable get() = TabOptions(
            index = 1U,
            title = "Users",
            icon = rememberVectorPainter(Icons.Sharp.Person)
        )

    override val key: ScreenKey = "UsersControlTab"

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val users = vm.pager.collectAsLazyPagingItems()
        var selectedItemKey by remember { mutableIntStateOf(-1) }
        val scope = rememberCoroutineScope()
        val apiScope = ApiClient.networkScope

        var banDialogShown by remember { mutableStateOf(false) }

        if (banDialogShown) {
            Dialog(
                onDismissRequest = { banDialogShown = false },
                properties = DialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                ),
                content = {
                    data class BanData(
                        val name: String,
                        val seconds: Int
                    )

                    val banMap = hashMapOf(
                        0 to BanData("5m", 300),
                        1 to BanData("1h", 3600),
                        2 to BanData("6h", 3600 * 6),
                        3 to BanData("1d", 3600 * 24),
                        4 to BanData("7d", 3600 * 24 * 7),
                        5 to BanData("30d", 3600 * 24 * 30),
                        6 to BanData("∞", 3600 * 24 * 365 * 10)
                    )
                    var reason by remember { mutableStateOf("") }
                    var untilSteps by remember {
                        mutableIntStateOf(0)
                    }
                    val banData by remember {
                        derivedStateOf {
                            banMap[untilSteps]!!
                        }
                    }
                    val untilSeconds by remember { mutableIntStateOf((System.currentTimeMillis() / 1000 + banData.seconds).toInt()) }
                    Box(
                        Modifier.background(MaterialTheme.colorScheme.surface).fillMaxWidth(0.8f)
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            MiniTitle("Бахнем ${users[selectedItemKey]?.name} (${users[selectedItemKey]?.id})?")
                            Spacer(Modifier.height(32.dp))
                            TextField(
                                value = reason,
                                onValueChange = { reason = it },
                                label = { Text("Причина") }
                            )
                            Spacer(Modifier.height(16.dp))
                            Text("Длительность: ${banData.name}", modifier = Modifier.padding(8.dp))
                            Spacer(Modifier.height(8.dp))
                            Slider(
                                value = untilSteps.toFloat() / 6,
                                steps = 6,
                                onValueChange = {
                                    // 6 steps from 0f to 6f
                                    untilSteps = (it * 6).toInt()
                                },
                                modifier = Modifier.requiredHeight(60.dp)
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(
                                modifier = Modifier.padding(8.dp).fillMaxWidth()
                                    .requiredHeight(60.dp),
                                onClick = {
                                    apiScope.launch {
                                        val user = users[selectedItemKey]!!
                                        ApiClient.Admin.Users.banUser(
                                            user.id,
                                            reason,
                                            untilSeconds,
                                            onSuccess = {
                                                banDialogShown = false
                                            },
                                            onFailure = {
                                                banDialogShown = false
                                            }
                                        )
                                    }
                                }
                            ) {
                                Text("Бахнуть")
                            }
                        }
                    }
                }
            )
        }

        Section("UsersList") {
            LazyColumn {
                items(users.itemCount) { index ->
                    val user = users[index]
                    if (user != null) {
                        UserCard(user, expanded = selectedItemKey == index, actions = {
                            IconButton(onClick = {
                                banDialogShown = true
                            }) {
                                Icon(Icons.Sharp.Block, null)
                            }
                        }) {
                            selectedItemKey = index
                        }
                    } else {
                        ListItem(
                            modifier = Modifier.padding(defaultPadding),
                            headlineContent = { Text("Bruh") }
                        )
                    }
                }
            }
            Divider()
        }
    }

}
