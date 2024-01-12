package ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import ui.viewmodel.LogInScreenViewModel

class LoginScreen : Screen {
    @Composable
    override fun Content() {
        val vm = rememberScreenModel { LogInScreenViewModel() }
        // login form with email and password text fields
        // login button
        // register button
        Column(Modifier.fillMaxSize()) {
            Spacer(Modifier.height(32.dp))
            TextField(
                vm.email.value,
                onValueChange = vm::setEmail,
                placeholder = { Text("Email") },
                singleLine = true,
                isError = !vm.isEmailValid.value
            )
            Spacer(Modifier.height(16.dp))
            TextField(
                vm.password.value,
                onValueChange = vm::setPassword,
                placeholder = { Text("Password") },
                singleLine = true,
                isError = !vm.isPasswordValid.value
            )
        }
    }
}