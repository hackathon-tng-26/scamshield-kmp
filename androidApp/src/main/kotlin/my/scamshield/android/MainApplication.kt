package my.scamshield.android

import android.app.Application
import my.scamshield.core.data.local.AndroidContextHolder
import my.scamshield.di.initKoin

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AndroidContextHolder.init(this)
        initKoin()
    }
}
