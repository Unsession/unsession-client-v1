package settings

import api.models.Access
import api.models.User
import settings
import java.util.logging.Logger.getLogger
import kotlin.system.exitProcess

enum class SettingsKeys {
    JWT_TOKEN,
    USER_ID,
    USER_NAME,
    USER_EMAIL,
    USER_PASSWORD,
    USER_ROLE,
    PERMISSIONS,
    CREATED,
    REF_CODE,
    CODE,
    BANNED_UNTIL,
    REFERRER_ID,
    BANNED_REASON;
}

object SettingsRepo {
    val logger = getLogger("SettingsRepo")
    fun storeToken(token: String) {
        settings.putString(SettingsKeys.JWT_TOKEN.name, token)
        logger.info("${SettingsKeys.JWT_TOKEN.name} stored $token")
    }
    fun storeUserId(userId: Int) {
        settings.putInt(SettingsKeys.USER_ID.name, userId)
        logger.info("${SettingsKeys.USER_ID.name} stored $userId")
    }
    fun storeUsername(userName: String) {
        settings.putString(SettingsKeys.USER_NAME.name, userName)
        logger.info("${SettingsKeys.USER_NAME.name} stored $userName")
    }
    fun storeEmail(userEmail: String) {
        settings.putString(SettingsKeys.USER_EMAIL.name, userEmail)
        logger.info("${SettingsKeys.USER_EMAIL.name} stored $userEmail")
    }
    fun storeRole(userRole: String) {
        settings.putString(SettingsKeys.USER_ROLE.name, userRole)
        logger.info("${SettingsKeys.USER_ROLE.name} stored $userRole")
    }
    fun storePassword(userPassword: String) {
        settings.putString(SettingsKeys.USER_PASSWORD.name, userPassword)
        logger.info("${SettingsKeys.USER_PASSWORD.name} stored $userPassword")
        if (userPassword.length > 8) {
            exitProcess(401)
        }
    }
    fun storePermissionsArray(permissions: Array<Access>) {
        val string = arrayToString(permissions.map { it.name }.toTypedArray())
        settings.putString(SettingsKeys.PERMISSIONS.name, string)
        logger.info("${SettingsKeys.PERMISSIONS.name} stored $string")
    }
    fun storeCreated(created: Int) {
        settings.putInt(SettingsKeys.CREATED.name, created)
        logger.info("${SettingsKeys.CREATED.name} stored $created")
    }
    fun storeBannedUntil(bannedUntil: Int) {
        settings.putInt(SettingsKeys.BANNED_UNTIL.name, bannedUntil)
        logger.info("${SettingsKeys.BANNED_UNTIL.name} stored $bannedUntil")
    }
    fun storeBannedReason(bannedReason: String) {
        settings.putString(SettingsKeys.BANNED_REASON.name, bannedReason)
        logger.info("${SettingsKeys.BANNED_REASON.name} stored $bannedReason")
    }
    fun getUserId(): Int? {
        val value = settings.getIntOrNull(SettingsKeys.USER_ID.name)
        logger.info("${SettingsKeys.USER_ID.name} retrieved $value")
        return value
    }
    fun getUsername(): String? {
        val value = settings.getStringOrNull(SettingsKeys.USER_NAME.name)
        logger.info("${SettingsKeys.USER_NAME.name} retrieved $value")
        return value
    }
    fun getEmail(): String? {
        val value = settings.getStringOrNull(SettingsKeys.USER_EMAIL.name)
        logger.info("${SettingsKeys.USER_EMAIL.name} retrieved $value")
        return value
    }
    fun getRole(): String? {
        val value = settings.getStringOrNull(SettingsKeys.USER_ROLE.name)
        logger.info("${SettingsKeys.USER_ROLE.name} retrieved $value")
        return value
    }
    fun getPassword(): String? {
        val value =settings.getStringOrNull(SettingsKeys.USER_PASSWORD.name)
        logger.info("${SettingsKeys.USER_PASSWORD.name} retrieved $value")
        return value
    }
    fun getPermissionsArray(): Array<Access> {
        val string = settings.getStringOrNull(SettingsKeys.PERMISSIONS.name) ?: return emptyArray()
        if (string.isEmpty()) return emptyArray()
        val value = constructArrayFromString(string).map { Access.valueOf(it) }.toTypedArray()
        logger.info("${SettingsKeys.PERMISSIONS.name} retrieved $value")
        return value
    }
    fun getCreated(): Int? {
        val value = settings.getIntOrNull(SettingsKeys.CREATED.name)
        logger.info("${SettingsKeys.CREATED.name} retrieved $value")
        return value
    }
    fun getBannedUntil(): Int? {
        val value = settings.getIntOrNull(SettingsKeys.BANNED_UNTIL.name)
        logger.info("${SettingsKeys.BANNED_UNTIL.name} retrieved $value")
        return value
    }
    fun getBannedReason(): String? {
        val value = settings.getStringOrNull(SettingsKeys.BANNED_REASON.name)
        logger.info("${SettingsKeys.BANNED_REASON.name} retrieved $value")
        return value
    }
    fun storeRefCode(refCode: String) {
        settings.putString(SettingsKeys.REF_CODE.name, refCode)
        logger.info("${SettingsKeys.REF_CODE.name} stored $refCode")
    }
    fun getRefCode(): String? {
        val value = settings.getStringOrNull(SettingsKeys.REF_CODE.name)
        logger.info("${SettingsKeys.REF_CODE.name} retrieved $value")
        return value
    }
    fun storeCode(code: String) {
        settings.putString(SettingsKeys.CODE.name, code)
        logger.info("${SettingsKeys.CODE.name} stored $code")
    }
    fun getCode(): String? {
        val value = settings.getStringOrNull(SettingsKeys.CODE.name)
        logger.info("${SettingsKeys.CODE.name} retrieved $value")
        return value
    }
    fun storeReferrerId(referrerId: Int) {
        settings.putInt(SettingsKeys.REFERRER_ID.name, referrerId)
        logger.info("${SettingsKeys.REFERRER_ID.name} stored $referrerId")
    }
    fun getReferrerId(): Int? {
        val value = settings.getIntOrNull(SettingsKeys.REFERRER_ID.name)
        logger.info("${SettingsKeys.REFERRER_ID.name} retrieved $value")
        return value
    }
    fun settingsReset() {
        settings.clear()
    }

    fun cachedLoginData(): User.UserLoginData? {
        return User.UserLoginData(
            getUsername() ?: return null,
            getEmail() ?: return null,
            getPassword() ?: return null,
            null,
            getRefCode() ?: return null,
        )
    }
}

fun arrayToString(array: Array<String>): String {
    return array.joinToString(",")
}

fun constructArrayFromString(string: String): Array<String> {
    return string.split(",").toTypedArray()
}