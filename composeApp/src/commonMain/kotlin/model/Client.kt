package model

import apiHttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.request.url
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import model.models.LoginResponse
import model.models.User
import rawHttpClient
import settings.SettingsRepo.getUserId

val rawClient = rawHttpClient()
val apiClient = apiHttpClient()

val host = "http://localhost:7777"
val apiUrl = "http://localhost:7777/api/v1"

suspend fun signIn(loginData: User.UserLoginData, onFailure: (String) -> Unit) {
    if (getUserId() == null) {
        register(loginData,
            onSuccess = {

            }, onFailure = {
                onFailure(it)
            })
    } else {
        login(loginData,
            onSuccess = {

            }, onFailure = {
                onFailure(it)
            })
    }
}

suspend fun login(
    loginData: User.UserLoginData,
    onSuccess: suspend () -> Unit,
    onFailure: (String) -> Unit
) {
    loginData.save()
    try {
        val call = rawClient.post {
            url("$host/login")
            setBody(loginData)
        }
        if (!call.status.isSuccess()) {
            onFailure(call.status.description)
            return
        }
        call.body<LoginResponse>().save()
        onSuccess()
    } catch (e: Exception) {
        onFailure(e.message.toString())
    }
}

suspend fun register(
    loginData: User.UserLoginData,
    onSuccess: suspend () -> Unit,
    onFailure: (String) -> Unit
) {
    loginData.save()
    try {
        val call = rawClient.post {
            url("$host/register")
            setBody(loginData)
        }
        if (!call.status.isSuccess()) {
            onFailure(call.bodyAsText())
            return
        }
        call.body<LoginResponse>().save()
        onSuccess()
    } catch (e: Exception) {
        onFailure(e.message.toString())
    }
}

suspend fun test() = apiClient.get {
    url("$apiUrl/test")
}