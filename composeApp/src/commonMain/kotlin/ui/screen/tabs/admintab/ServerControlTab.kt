package ui.screen.tabs.admintab

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExtendedFloatingActionButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dataset
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import api.ApiClient
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ui.theme.defaultPadding
import ui.uikit.MiniTitle

class ServerControlTab : Tab {

    override val options: TabOptions
        @Composable get() = TabOptions(
            index = 0U,
            title = "Server",
            icon = rememberVectorPainter(Icons.Default.Dns)
        )

    override val key: ScreenKey = "ServerControlTab"

    @Composable
    override fun Content() {
        var status by remember { mutableStateOf("Работаем") }
        val items = remember { mutableStateListOf<String>() }
        LaunchedEffect("serverStatus") {
            CoroutineScope(Dispatchers.IO).launch {
                while (true) {
                    delay(1000)
                    ApiClient.Admin.Server.ping(onSuccess = {
                        status = "Работаем"
                    }, onFailure = {
                        status = "\uD83D\uDE33 Не работаем \uD83D\uDE33"
                    })
                }
            }
        }
        Column {
            var confirmationDialog by remember { mutableStateOf(false) }

            if (confirmationDialog) {
                AlertDialog(
                    onDismissRequest = {
                        confirmationDialog = false
                    },
                    title = {
                        Text("Ар ю суре?")
                    },
                    text = {
                        Text("Это бахнет схему public в unsession DB и все данные улетят в небытие. Всё сохранится в снепшоте яндекса, а этол раз в сутки.")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                ApiClient.networkScope.launch {
                                    ApiClient.Admin.Server.dropDatabase(onSuccess = {
                                        items.add("Database dropped. I hope, you have a reason for that and backup :---)")
                                    }, onFailure = {
                                        items.add("Failed to drop database err: $it")
                                    })
                                }
                                confirmationDialog = false
                            }
                        ) {
                            Text("Yes")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                confirmationDialog = false
                            }
                        ) {
                            Text("No")
                        }
                    }
                )
            }

            MiniTitle(title = "ОПАСНЫЕ ДЕЙСТВИЯ")

            Text("Статус сервера: $status")
            Spacer(modifier = Modifier.height(16.dp))

            ExtendedFloatingActionButton(backgroundColor = Color.Red,
                onClick = {
                    ApiClient.networkScope.launch {
                        ApiClient.Admin.Server.serverShutdown(onSuccess = {
                            items.add("Shutting down")
                        }, onFailure = {
                            items.add("Failed to shutdown err: $it")
                        })
                    }
                }, text = {
                    Text("Остановка процесса unsserver.jar")
                })
            Spacer(modifier = Modifier.height(16.dp))
            Column(Modifier.border(1.dp, Color.Red).padding(defaultPadding)) {
                Text("Опасное действие, ОБЯЗАТЕЛЬНО иметь резервную копию. Это последнее шаг.")
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    onClick = {
                        confirmationDialog = true
                    }, content = {
                        Row {
                            Icon(Icons.Default.Dataset, contentDescription = null)
                            Text("Drop database")
                        }
                    })
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(items.size) {
                    Text(items[it])
                }
            }
        }
    }
}
