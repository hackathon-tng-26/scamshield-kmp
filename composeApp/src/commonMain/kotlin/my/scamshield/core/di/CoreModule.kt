package my.scamshield.core.di

import my.scamshield.core.data.i18n.InMemoryLocaleRepository
import my.scamshield.core.data.remote.ApiConfig
import my.scamshield.core.data.remote.ApiConfigDefaults
import my.scamshield.core.domain.i18n.LocaleRepository
import my.scamshield.core.domain.util.AppClock
import my.scamshield.core.domain.util.Logger
import my.scamshield.core.domain.util.SystemAppClock
import my.scamshield.core.platform.Caller
import org.koin.dsl.module

expect fun createLogger(): Logger
expect fun createCaller(): Caller

val coreModule = module {
    single<Logger> { createLogger() }
    single<AppClock> { SystemAppClock() }
    single<ApiConfig> {
        ApiConfig(baseUrl = ApiConfigDefaults.DEMO_FALLBACK)
    }
    single<Caller> { createCaller() }
    single<LocaleRepository> { InMemoryLocaleRepository() }
}
