package api.models

import kotlinx.serialization.Serializable
import settings.SettingsRepo
import settings.SettingsRepo.logger
import settings.SettingsRepo.storeEmail
import settings.SettingsRepo.storePassword
import settings.SettingsRepo.storeUsername
import java.time.Clock

@Serializable
data class User(
    val id: Int, // stored
    val name: String, // stored
    var userLoginData: UserLoginData? = null, // stored
    val permissions: HashSet<Access>, // stored
    val roleName: String, // stored
    var banData: BanData? = null,
    val created: Int = 0, // stored
    var lastLogin: Int? = null,
    var lastIp: String? = null,
) {
    @Serializable
    data class UserLoginData(
        val username: String?,
        val email: String,
        val password: String,
        val salt: String? = null
    ) {
        fun save() {
            username?.let { storeUsername(it) }
            storeEmail(email)
            storePassword(password)
        }
        companion object {
            fun get(): UserLoginData {
                return UserLoginData(
                    username = SettingsRepo.getUsername(),
                    email = SettingsRepo.getEmail()!!,
                    password = SettingsRepo.getPassword()!!,
                )
            }
        }
    }

    @Serializable
    data class BanData(
        val bannedUntil: Int,
        val bannedReason: String,
    ) {
        fun save() {
            SettingsRepo.storeBannedUntil(bannedUntil)
            SettingsRepo.storeBannedReason(bannedReason)
        }
        companion object {
            fun get(): BanData? {
                return BanData(
                    bannedUntil = SettingsRepo.getBannedUntil()?: return null,
                    bannedReason = SettingsRepo.getBannedReason()?: return null,
                )
            }
        }
    }

    val isBanned: Boolean
        get() {
            if (this.banData == null) return false
            return this.banData!!.bannedUntil >= Clock.systemUTC().millis() / 1000
        }

    fun save(login: Boolean = true): Boolean {
        if (login) {
            logger.info("Saving user: $this")
            userLoginData?.save() ?: return false
        }
        logger.info("Saving permissions: $permissions")
        banData?.save()
        logger.info("Saving ban data: $banData")
        with(SettingsRepo) {
            storeUsername(name)
            storeRole(roleName)
            storePermissionsArray(permissions.toTypedArray())
            storeCreated(created)
            storeUserId(id)
        }
        return true
    }

    fun hasAccess(access: Access): Boolean {
        if (isBanned) return false
        return this.permissions.contains(access)
    }

    fun hasAccess(access: Collection<Access>): Boolean {
        return this.permissions.containsAll(access)
    }

    companion object {
        fun get(): User {
            val s = SettingsRepo
            return User(
                id = s.getUserId()!!,
                name = s.getUsername()!!,
                roleName = s.getRole()!!,
                permissions = s.getPermissionsArray().toHashSet(),
                userLoginData = UserLoginData.get(),
                banData = BanData.get(),
                created = s.getCreated()?: -1
            )
        }

        fun hasAccess(access: Access): Boolean {
            return get().hasAccess(access)
        }

        fun hasAccess(access: Collection<Access>): Boolean {
            return get().hasAccess(access)
        }
    }
}