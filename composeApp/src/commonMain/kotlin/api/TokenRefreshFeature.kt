package api

import api.Api.Users.login
import getToken
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpClientPlugin
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.request
import io.ktor.client.request.takeFrom
import io.ktor.client.statement.HttpReceivePipeline
import io.ktor.client.statement.request
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.util.AttributeKey
import settings.SettingsRepo.cachedLoginData

class AuthPlugin {
    class Configuration {
    }
    companion object Plugin : HttpClientPlugin<Configuration, AuthPlugin> {
        override val key = AttributeKey<AuthPlugin>("AuthPlugin")
        override fun install(plugin: AuthPlugin, scope: HttpClient) {
            scope.receivePipeline.intercept(HttpReceivePipeline.After) {
                println("Intercepting response from ${cachedLoginData()}  ${it.status}")
                val loginData = cachedLoginData()
                if (loginData == null) {
                    println("No login data, proceeding")
                    proceed()
                    return@intercept
                }
                if (it.status == HttpStatusCode.Unauthorized) {
                    login(loginData, onSuccess = {
                        println("Login successful, repeating request")
                        val repeatCall = HttpRequestBuilder().apply {
                            takeFrom(it.request)
                            headers[HttpHeaders.Authorization] = "Bearer ${getToken()}"
                        }
                        println(repeatCall.headers.entries())
                        val response = rawClient.request(repeatCall)
                        proceedWith(response)
                    }, {
                        println("Login failed, proceeding with error 401")
                        finish()
                    })
                }
            }
        }

        override fun prepare(block: Configuration.() -> Unit): AuthPlugin {
            val config = Configuration().apply(block)
            return AuthPlugin()
        }
    }
}
