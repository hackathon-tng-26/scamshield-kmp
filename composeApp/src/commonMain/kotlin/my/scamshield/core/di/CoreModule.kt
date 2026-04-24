package my.scamshield.core.di

import my.scamshield.core.data.remote.ApiConfig
import my.scamshield.core.data.remote.ApiConfigDefaults
import my.scamshield.core.domain.util.AppClock
import my.scamshield.core.domain.util.Logger
import my.scamshield.core.domain.util.SystemAppClock
import org.koin.dsl.module

expect fun createLogger(): Logger

val coreModule = module {
    single<Logger> { createLogger() }
    single<AppClock> { SystemAppClock() }
    single<ApiConfig> {
        ApiConfig(baseUrl = ApiConfigDefaults.DEMO_FALLBACK)
    }
}
