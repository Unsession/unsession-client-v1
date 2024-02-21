package ui.screen.loginflow

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import api.Api.Users.register
import api.models.User
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.apu.unsession.MR
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ui.screen.HomeScreen
import utils.localization.Localized.localString
import utils.localization.Res.getString
import utils.state.LogInState

class RegistrationScreen : Screen {
    @Composable
    override fun Content() {
        val vm = rememberScreenModel { RegistrationScreenViewModel() }
        val nav = LocalNavigator.current!!
        val context = LocalContext.current

        fun clearError() {
            vm.loginErrorMessage.value = ""
        }

        if (vm.loginErrorMessage.value != "") {
            Box {
                AlertDialog(
                    onDismissRequest = { clearError() },
                    title = { Text(getString(MR.strings.login_error).localString()) },
                    text = { Text("Error: ${vm.loginErrorMessage}") },
                    confirmButton = {
                        Button(
                            onClick = { clearError() },
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Text(getString(MR.strings.ok).localString())
                        }
                    }
                )
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
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    vm.setLogInState(LogInState.LOG_IN_PROGRESS)
                    val loginData = User.UserLoginData(
                        username = vm.username.value,
                        email = vm.email.value,
                        password = vm.password.value,
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        register(loginData,
                            onSuccess = {
                                nav.push(HomeScreen())
                            }, onFailure = {
                                vm.loginErrorMessage.value = it
                                vm.setLogInState(LogInState.LOG_IN_FAILED)
                            }
                        )
                    }
                },
                enabled = vm.isFormValid.value || vm.loginErrorMessage.value == LogInState.LOG_IN_PROGRESS.name,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(getString(MR.strings.register).localString())
            }
            Text(
                modifier = Modifier.padding(top = 8.dp).clickable {
                    nav.push(LoginScreen())
                },
                text = getString(MR.strings.login).localString(),
                color = MaterialTheme.colorScheme.secondary,
                fontWeight = FontWeight.Medium,
                textDecoration = TextDecoration.Underline
            )
        }
    }
}
