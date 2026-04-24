package my.scamshield.core.di

import io.ktor.client.HttpClient
import my.scamshield.core.data.remote.HttpClientFactory
import org.koin.dsl.module

val networkModule = module {
    single<HttpClient> {
        HttpClientFactory(config = get(), logger = get()).create()
    }
}
