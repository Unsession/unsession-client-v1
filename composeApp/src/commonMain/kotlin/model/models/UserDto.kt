package model.models

import kotlinx.serialization.Serializable
import lol.unsession.security.permissions.Access
import settings.SettingsRepo.storeEmail
import settings.SettingsRepo.storePassword
import settings.SettingsRepo.storeUsername

@Serializable
class User(
    val id: Int,
    val name: String,
    var userLoginData: UserLoginData?,
    val permissions: HashSet<Access>,
    val roleName: String,
    var banData: BanData?,
    val created: Int,
    val lastLogin: Int,
    val lastIp: String,
) {
    @Serializable
    data class UserLoginData(
        val username: String?,
        val email: String,
        val password: String,
        val salt: String? = null,
    ) {
        fun save() {
            username?.let { storeUsername(it) }
            storeEmail(email)
            storePassword(password)
        }
    }

    @Serializable
    data class BanData(
        val bannedUntil: Int,
        val bannedReason: String,
    )
}
