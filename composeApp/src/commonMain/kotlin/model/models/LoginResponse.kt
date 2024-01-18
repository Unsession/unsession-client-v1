package model.models

import kotlinx.serialization.Serializable
import settings.SettingsRepo.storeCache
import settings.SettingsRepo.storeToken

@Serializable
data class LoginResponse(
    val token: String,
    val user: User
) {
    fun save() {
        storeToken(token)
        storeCache(user)
    }
}
