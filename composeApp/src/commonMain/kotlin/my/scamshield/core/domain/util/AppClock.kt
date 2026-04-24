package my.scamshield.core.domain.util

import kotlin.time.Clock
import kotlin.time.Instant

interface AppClock {
    fun now(): Instant
    fun currentTimeMillis(): Long
}

class SystemAppClock : AppClock {
    override fun now(): Instant = Clock.System.now()
    override fun currentTimeMillis(): Long = Clock.System.now().toEpochMilliseconds()
}
