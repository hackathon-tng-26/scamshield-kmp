package my.scamshield.core.di

import my.scamshield.core.data.util.IosLogger
import my.scamshield.core.domain.util.Logger

actual fun createLogger(): Logger = IosLogger()
