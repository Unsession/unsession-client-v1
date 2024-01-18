package model

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
import settings.SettingsRepo.getToken

class AuthPlugin {
    class Configuration {
    }
    companion object Plugin : HttpClientPlugin<Configuration, AuthPlugin> {
        override val key = AttributeKey<AuthPlugin>("AuthPlugin")
        override fun install(plugin: AuthPlugin, scope: HttpClient) {
            scope.receivePipeline.intercept(HttpReceivePipeline.State) {
                val loginData = cachedLoginData() ?: return@intercept
                if (it.status == HttpStatusCode.Unauthorized) {
                    login(loginData, onSuccess = {
                        val repeatCall = HttpRequestBuilder().apply {
                            takeFrom(it.request)
                            headers.remove(HttpHeaders.Authorization)
                            headers.append(HttpHeaders.Authorization, "Bearer ${getToken()}")
                        }
                        val response = rawClient.request(repeatCall)
                        proceedWith(response)
                    }, {
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
