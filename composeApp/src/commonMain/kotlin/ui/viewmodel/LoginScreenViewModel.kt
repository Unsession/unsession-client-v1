package ui.viewmodel

import androidx.compose.runtime.mutableStateOf
import cafe.adriel.voyager.core.model.ScreenModel
import com.apu.unsession.MR
import utils.localization.Res

enum class LogInState {
    LOG_IN,
    LOG_IN_SUCCESS,
    LOG_IN_ERROR,
    LOG_IN_PROGRESS,
}

class LogInScreenViewModel : ScreenModel {
    var username = mutableStateOf("")
        private set
    var email = mutableStateOf("")
        private set

    var password = mutableStateOf("")
        private set

    var isPasswordVisible = mutableStateOf(false)
        private set

    private var logInState = mutableStateOf(0)

    var isEmailValid = mutableStateOf(false)
        private set

    var isPasswordValid = mutableStateOf(false)
        private set

    var isUsernameValid = mutableStateOf(false)
        private set

    var isFormValid = mutableStateOf(false)
        private set

    var loginErrorMessage = mutableStateOf("")
        private set

    var currentPasswordIcon = mutableStateOf(MR.images.invisible)
        private set

    private fun setError(message: String) {
        loginErrorMessage.value = message
    }

    private fun checkEmail(): Boolean {
        val emailRegex = "^[a-zA-Z0-9_.+-]+@(niuitmo.ru|itmo.ru)$".toRegex()
        val state = email.value.matches(emailRegex)
        isEmailValid.value = state
        return state
    }

    private fun checkPassword(): Boolean {
        when {
            password.value.length < 8 -> {
                setError(Res.getString(MR.strings.password_too_short).toString())
                isPasswordValid.value = false
            }

            password.value.contains(email.value.substringBefore("@")) -> {
                setError(Res.getString(MR.strings.password_too_easy).toString())
                isPasswordValid.value = false
            }

            password.value.contains(email.value.substringAfter("@")) -> {
                setError(Res.getString(MR.strings.password_too_easy).toString())
                isPasswordValid.value = false
            }

            password.value.contains("qwerty") -> {
                setError(Res.getString(MR.strings.password_too_easy).toString())
                isPasswordValid.value = false
            }

            password.value.contains("12345678") -> {
                setError(Res.getString(MR.strings.password_too_easy).toString())
                isPasswordValid.value = false
            }

            password.value.contains("password") -> {
                setError(Res.getString(MR.strings.password_too_easy).toString())
                isPasswordValid.value = false
            }

            else -> isPasswordValid.value = true
        }
        return isPasswordValid.value
    }

    private fun checkForm(): Boolean {
        val state = isEmailValid.value && isPasswordValid.value
        isFormValid.value = state
        return state
    }

    private fun checkUsername(): Boolean {
        val state = username.value.length > 4
        isUsernameValid.value = state
        return state
    }

    fun togglePasswordVisibility() {
        isPasswordVisible.value = !isPasswordVisible.value
        if (isPasswordVisible.value) {
            currentPasswordIcon.value = MR.images.visible
        } else {
            currentPasswordIcon.value = MR.images.invisible
        }
    }
    fun setLogInState(state: LogInState) {
        logInState.value = state.ordinal
    }
    fun getLogInState(): LogInState {
        return LogInState.entries[logInState.value]
    }
    fun setEmail(email: String) {
        this.email.value = email
        isEmailValid.value = true
        //checkEmail()
        checkForm()
    }
    fun setPassword(password: String) {
        this.password.value = password
        checkPassword()
        checkForm()
    }

    fun setUsername(username: String) {
        checkUsername()
        this.username.value = username
    }
}
