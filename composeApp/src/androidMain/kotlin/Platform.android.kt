import android.os.Build
import api.AuthPlugin
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.engine.cio.CIO
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.ContentType.Application
import io.ktor.http.HttpHeaders.Authorization
import io.ktor.http.HttpHeaders.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun apiHttpClient(config: HttpClientConfig<*>.() -> Unit) = HttpClient(CIO) {
    config(this)
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
    engine {
        requestTimeout = MASTER_TIMEOUT
        pipelining = true
    }
    install(HttpTimeout) {
        requestTimeoutMillis = MASTER_TIMEOUT
        connectTimeoutMillis = MASTER_TIMEOUT
        socketTimeoutMillis = MASTER_TIMEOUT
    }
    defaultRequest {
        header("Content-Type", "application/json")
        header(Authorization, "Bearer ${getToken() ?: "none"}")
    }
    install(AuthPlugin)
}

actual fun rawHttpClient(config: HttpClientConfig<*>.() -> Unit) = HttpClient(OkHttp) {
    config(this)
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
        })
    }
    engine {
        config {
            retryOnConnectionFailure(true)
            callTimeout(MASTER_TIMEOUT, TimeUnit.SECONDS)
            connectTimeout(MASTER_TIMEOUT, TimeUnit.SECONDS)
        }
    }
    install(HttpTimeout) {
        requestTimeoutMillis = MASTER_TIMEOUT
        connectTimeoutMillis = MASTER_TIMEOUT
        socketTimeoutMillis = MASTER_TIMEOUT
    }
    defaultRequest {
        header(ContentType, Application.Json)
    }
}

actual val settings: Settings = Settings()

actual fun getToken(): String? {
    return settings.getStringOrNull("JWT_TOKEN")
}