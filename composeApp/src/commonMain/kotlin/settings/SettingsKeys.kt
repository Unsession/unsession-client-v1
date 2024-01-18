package settings

import model.models.User
import settings

enum class SettingsKeys() {
    JWT_TOKEN,
    USER_ID,
    USER_NAME,
    USER_EMAIL,
    USER_PASSWORD,
    USER_ROLE;
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
    fun getToken(): String? {
        return settings.getStringOrNull(SettingsKeys.JWT_TOKEN.name)
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
    fun settingsReset() {
        settings.clear()
    }
    fun storeCache(user: User) {
        storeUserId(user.id)
        storeEmail(user.userLoginData!!.email)
        storeRole(user.roleName)
        storeUsername(user.name)
    }

    fun cachedLoginData(): User.UserLoginData? {
        return User.UserLoginData(
            getUsername()?: return null,
            getEmail()?: return null,
            getPassword()?: return null
        )
    }
}
