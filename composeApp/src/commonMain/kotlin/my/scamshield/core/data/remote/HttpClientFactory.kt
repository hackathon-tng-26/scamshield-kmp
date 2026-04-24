package my.scamshield.core.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import my.scamshield.core.domain.util.Logger
import io.ktor.client.plugins.logging.Logger as KtorLogger

class HttpClientFactory(
    private val config: ApiConfig,
    private val logger: Logger,
) {
    fun create(): HttpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }

        install(HttpRequestRetry) {
            maxRetries = config.maxRetries
            retryIf { _, response -> response.status.value in 500..599 }
            retryOnExceptionIf { _, cause ->
                cause is io.ktor.client.plugins.HttpRequestTimeoutException
            }
            delayMillis { it * 500L }
        }

        install(HttpTimeout) {
            requestTimeoutMillis = config.timeoutMs
            connectTimeoutMillis = config.timeoutMs / 2
            socketTimeoutMillis = config.timeoutMs
        }

        install(Logging) {
            logger = object : KtorLogger {
                override fun log(message: String) {
                    this@HttpClientFactory.logger.debug(message, "HTTP")
                }
            }
            level = LogLevel.HEADERS
        }

        defaultRequest {
            header(HttpHeaders.ContentType, "application/json")
            url(config.baseUrl)
        }
    }
}
