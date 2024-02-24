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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import api.ApiClient.Users.login
import api.models.User
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.apu.unsession.MR
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import settings.SettingsRepo
import ui.screen.HomeScreen
import utils.localization.Localized.localString
import utils.localization.Res.getString
import utils.state.LogInState

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        val vm = rememberScreenModel { LogInScreenViewModel }
        val nav = LocalNavigator.current!!

        fun clearError() {
            LogInScreenViewModel.loginErrorMessage.value = ""
        }

        if (LogInScreenViewModel.loginErrorMessage.value != "") {
            Box {
                AlertDialog(
                    onDismissRequest = { },
                    title = { Text(getString(MR.strings.login_error).localString()) },
                    text = { Text("Error: ${LogInScreenViewModel.loginErrorMessage.value}") },
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
                LogInScreenViewModel.email.value,
                onValueChange = vm::setEmail,
                placeholder = { Text(getString(MR.strings.email).localString()) },
                singleLine = true,
                isError = !LogInScreenViewModel.isEmailValid.value,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            TextField(
                LogInScreenViewModel.password.value,
                onValueChange = vm::setPassword,
                placeholder = { Text(getString(MR.strings.password).localString()) },
                singleLine = true,
                isError = !LogInScreenViewModel.isPasswordValid.value,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = {
                    LogInScreenViewModel.setLogInState(LogInState.LOG_IN_PROGRESS)
                    val loginData = User.UserLoginData(
                        username = SettingsRepo.getUsername(),
                        email = LogInScreenViewModel.email.value,
                        password = LogInScreenViewModel.password.value,
                    )
                    CoroutineScope(Dispatchers.IO).launch {
                        login(loginData,
                            onSuccess = {
                                nav.push(HomeScreen())
                            }, onFailure = {
                                LogInScreenViewModel.loginErrorMessage.value = it
                                LogInScreenViewModel.setLogInState(LogInState.LOG_IN_FAILED)
                            })
                    }
                },
                enabled = LogInScreenViewModel.isFormValid.value || LogInScreenViewModel.loginErrorMessage.value == LogInState.LOG_IN_PROGRESS.name,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(getString(MR.strings.login).localString())
            }
            Text(modifier = Modifier.padding(top = 8.dp).clickable {
                nav.push(RegistrationScreen())
            }, text = getString(MR.strings.register).localString(), color = MaterialTheme.colorScheme.secondary, fontWeight = FontWeight.Medium, textDecoration = TextDecoration.Underline)
        }
    }
}
