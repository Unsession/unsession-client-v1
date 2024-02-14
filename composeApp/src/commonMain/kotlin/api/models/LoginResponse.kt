package api.models

import kotlinx.serialization.Serializable
import settings.SettingsRepo.logger
import settings.SettingsRepo.storeToken

@Serializable
data class LoginResponse(
    val token: String,
    val user: User
) {
    fun save() {
        logger.info("Saving token: $token")
        storeToken(token)
        logger.info("Saving user: $user")
        user.save(false)
    }
}
