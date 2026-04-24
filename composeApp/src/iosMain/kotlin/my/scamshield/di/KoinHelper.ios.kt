package my.scamshield.di

import my.scamshield.app.di.appModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin

actual fun initKoin(): KoinApplication = startKoin {
    modules(appModule)
}
