package settings

import api.models.User
import lol.unsession.security.permissions.Access
import settings

enum class SettingsKeys() {
    JWT_TOKEN,
    USER_ID,
    USER_NAME,
    USER_EMAIL,
    USER_PASSWORD,
    USER_ROLE,
    PERMISSIONS,
    CREATED,
    BANNED_UNTIL,
    BANNED_REASON;
}

object SettingsRepo {
    fun storeToken(token: String) {
        settings.putString(SettingsKeys.JWT_TOKEN.name, token)
    }
    fun storeUserId(userId: Int) {
        settings.putInt(SettingsKeys.USER_ID.name, userId)
    }
    fun storeUsername(userName: String) {
        settings.putString(SettingsKeys.USER_NAME.name, userName)
    }
    fun storeEmail(userEmail: String) {
        settings.putString(SettingsKeys.USER_EMAIL.name, userEmail)
    }
    fun storeRole(userRole: String) {
        settings.putString(SettingsKeys.USER_ROLE.name, userRole)
    }
    fun storePassword(userPassword: String) {
        settings.putString(SettingsKeys.USER_PASSWORD.name, userPassword)
    }
    fun storePermissionsArray(permissions: Array<Access>) {
        val string = arrayToString(permissions.map { it.name }.toTypedArray())
        settings.putString(SettingsKeys.PERMISSIONS.name, string)
    }
    fun storeCreated(created: Int) {
        settings.putInt(SettingsKeys.CREATED.name, created)
    }
    fun storeBannedUntil(bannedUntil: Int) {
        settings.putInt(SettingsKeys.BANNED_UNTIL.name, bannedUntil)
    }
    fun storeBannedReason(bannedReason: String) {
        settings.putString(SettingsKeys.BANNED_REASON.name, bannedReason)
    }
    fun getUserId(): Int? {
        return settings.getIntOrNull(SettingsKeys.USER_ID.name)
    }
    fun getUsername(): String? {
        return settings.getStringOrNull(SettingsKeys.USER_NAME.name)
    }
    fun getEmail(): String? {
        return settings.getStringOrNull(SettingsKeys.USER_EMAIL.name)
    }
    fun getRole(): String? {
        return settings.getStringOrNull(SettingsKeys.USER_ROLE.name)
    }
    fun getPassword(): String? {
        return settings.getStringOrNull(SettingsKeys.USER_PASSWORD.name)
    }
    fun getPermissionsArray(): Array<Access> {
        val string = settings.getStringOrNull(SettingsKeys.PERMISSIONS.name) ?: return emptyArray()
        return constructArrayFromString(string).map { Access.valueOf(it) }.toTypedArray()
    }
    fun getCreated(): Int? {
        return settings.getIntOrNull(SettingsKeys.CREATED.name)
    }
    fun getBannedUntil(): Int? {
        return settings.getIntOrNull(SettingsKeys.BANNED_UNTIL.name)
    }
    fun getBannedReason(): String? {
        return settings.getStringOrNull(SettingsKeys.BANNED_REASON.name)
    }
    fun settingsReset() {
        settings.clear()
    }

    fun cachedLoginData(): User.UserLoginData? {
        return User.UserLoginData(
            getUsername() ?: return null,
            getEmail() ?: return null,
            getPassword() ?: return null
        )
    }
}

fun arrayToString(array: Array<String>): String {
    return array.joinToString(",")
}

fun constructArrayFromString(string: String): Array<String> {
    return string.split(",").toTypedArray()
}