package ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import com.apu.unsession.MR
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import model.models.User
import model.register
import ui.viewmodel.LogInScreenViewModel
import ui.viewmodel.LogInState
import utils.localization.Localized.localString
import utils.localization.Res.getString

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        val vm = rememberScreenModel { LogInScreenViewModel() }
        var showError by remember { mutableStateOf(false) }
        var errorText by remember { mutableStateOf("") }
        var logInState by remember { mutableStateOf(vm.getLogInState()) }
        if (showError) {
            Box {
                AlertDialog(
                    onDismissRequest = { },
                    title = { Text(getString(MR.strings.login_error).localString()) },
                    text = { Text("Error: $errorText") },
                    confirmButton = {
                        Button(
                            onClick = { showError = false },
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(getString(MR.strings.ok).localString())
                        }
                    })
            }
        }
        Column(
            Modifier.fillMaxSize().padding(horizontal = 32.dp),
            verticalArrangement = Arrangement.Center
        ) {
            TextField(
                vm.username.value,
                onValueChange = vm::setUsername,
                placeholder = { Text(getString(MR.strings.username).localString()) },
                singleLine = true,
                isError = !vm.isUsernameValid.value,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            TextField(
                vm.email.value,
                onValueChange = vm::setEmail,
                placeholder = { Text(getString(MR.strings.email).localString()) },
                singleLine = true,
                isError = !vm.isEmailValid.value,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            TextField(
                vm.password.value,
                onValueChange = vm::setPassword,
                placeholder = { Text(getString(MR.strings.password).localString()) },
                singleLine = true,
                isError = !vm.isPasswordValid.value,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    vm.setLogInState(LogInState.LOG_IN_PROGRESS)
                    val loginData = User.UserLoginData(
                        username = vm.email.value,
                        email = vm.email.value,
                        password = vm.password.value
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        register(loginData,
                            onSuccess = {
                                loginSuccess()
                            }, onFailure = {
                                errorText = it
                                showError = true
                            })
                    }
                },
                enabled = vm.isFormValid.value || logInState == LogInState.LOG_IN_PROGRESS,
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
            ) {
                Text(getString(MR.strings.login).localString())
            }
        }
    }
}

fun loginSuccess() {

}