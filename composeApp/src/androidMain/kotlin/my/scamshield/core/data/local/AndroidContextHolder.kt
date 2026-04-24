package my.scamshield.core.data.local

import android.content.Context

object AndroidContextHolder {
    @Volatile private var appContext: Context? = null

    fun init(context: Context) {
        appContext = context.applicationContext
    }

    fun requireContext(): Context = appContext
        ?: error("AndroidContextHolder not initialised — call init(context) in Application.onCreate()")
}
