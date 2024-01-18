import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig

const val MASTER_TEST_TIMEOUT = 360000L

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun apiHttpClient(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient
expect fun rawHttpClient(config: HttpClientConfig<*>.() -> Unit = {}): HttpClient

expect val settings: Settings