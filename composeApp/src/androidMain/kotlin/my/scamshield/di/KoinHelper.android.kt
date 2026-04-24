package my.scamshield.di

import my.scamshield.app.di.appModule
import my.scamshield.core.data.local.AndroidContextHolder
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

actual fun initKoin(): KoinApplication = startKoin {
    androidLogger(Level.DEBUG)
    androidContext(AndroidContextHolder.requireContext())
    modules(appModule)
}
