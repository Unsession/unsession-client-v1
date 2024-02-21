package ui.permissions

import dev.icerock.moko.biometry.BiometryAuthenticator
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
