package my.scamshield.core.di

import my.scamshield.core.data.util.AndroidLogger
import my.scamshield.core.domain.util.Logger

actual fun createLogger(): Logger = AndroidLogger()
