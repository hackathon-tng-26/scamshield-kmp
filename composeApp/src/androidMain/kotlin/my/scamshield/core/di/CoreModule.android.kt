package my.scamshield.core.di

import my.scamshield.core.data.util.AndroidLogger
import my.scamshield.core.domain.util.Logger
import my.scamshield.core.platform.AndroidCaller
import my.scamshield.core.platform.Caller

actual fun createLogger(): Logger = AndroidLogger()
actual fun createCaller(): Caller = AndroidCaller()
