package my.scamshield.core.di

import my.scamshield.core.data.util.IosLogger
import my.scamshield.core.domain.util.Logger
import my.scamshield.core.platform.Caller
import my.scamshield.core.platform.IosCaller

actual fun createLogger(): Logger = IosLogger()
actual fun createCaller(): Caller = IosCaller()
