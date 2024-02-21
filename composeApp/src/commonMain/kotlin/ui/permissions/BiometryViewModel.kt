package ui.permissions

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.icerock.moko.biometry.BiometryAuthenticator
import dev.icerock.moko.biometry.compose.BindBiometryAuthenticatorEffect
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcher
import dev.icerock.moko.mvvm.dispatcher.EventsDispatcherOwner
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BiometryViewModel(
    val biometryAuthenticator: BiometryAuthenticator,
    override val eventsDispatcher: EventsDispatcher<BiometryViewModel>,
) : ViewModel(), EventsDispatcherOwner<BiometryViewModel> {

    enum class BiometryResult {
        SUCCESS,
        CANCEL,
        ERROR,
        NONE,
    }

    val result = MutableStateFlow(BiometryResult.NONE)

    @Suppress("TooGenericExceptionCaught") // чёбля))
    fun tryToAuth(
        title: String,
        subtitle: String,
        failure: String
    ) {
        viewModelScope.launch {
            try {
                val isSuccess = biometryAuthenticator.checkBiometryAuthentication(
                    requestTitle = title.desc(),
                    requestReason = subtitle.desc(),
                    failureButtonText = failure.desc()
                )
                if (isSuccess) {
                    result.value = BiometryResult.SUCCESS
                } else {
                    result.value = BiometryResult.CANCEL
                }
            } catch (throwable: Throwable) {
                println(throwable)
                result.value = BiometryResult.ERROR
            }
        }
    }
}

@Composable
fun BiometryScreen(
    viewModel: BiometryViewModel,
    onSuccess: () -> Unit,
) {
    BindBiometryAuthenticatorEffect(viewModel.biometryAuthenticator)

    val status: BiometryViewModel.BiometryResult by viewModel.result.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = status.name)

        Button(onClick = {viewModel.tryToAuth(
            title = "⚠ Доступ к админ-панели ⚠",
            subtitle = "Требуется верификация личности",
            failure = "Чёт не получилось..."
        )}) {
            Text(text = "Click on me")
        }
    }

    if (status == BiometryViewModel.BiometryResult.SUCCESS) {
        onSuccess()
    }
}