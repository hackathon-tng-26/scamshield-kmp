package my.scamshield.core.data.util

import co.touchlab.kermit.Logger as KermitLogger
import my.scamshield.core.domain.util.Logger

class IosLogger : Logger {
    private val kermit = KermitLogger.withTag("ScamShield")

    override fun debug(message: String, tag: String?) {
        kermit.d(tag = tag ?: "App") { message }
    }

    override fun info(message: String, tag: String?) {
        kermit.i(tag = tag ?: "App") { message }
    }

    override fun warn(message: String, tag: String?, throwable: Throwable?) {
        if (throwable != null) kermit.w(throwable, tag ?: "App") { message }
        else kermit.w(tag = tag ?: "App") { message }
    }

    override fun error(message: String, tag: String?, throwable: Throwable?) {
        if (throwable != null) kermit.e(throwable, tag ?: "App") { message }
        else kermit.e(tag = tag ?: "App") { message }
    }
}
