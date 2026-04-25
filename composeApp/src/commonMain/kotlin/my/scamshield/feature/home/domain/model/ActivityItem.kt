package my.scamshield.feature.home.domain.model

import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant

enum class ActivityKind {
    SENT,
    BLOCKED,
    HELD,
}

data class ActivityItem(
    val id: String,
    val kind: ActivityKind,
    val title: String,
    val subtitle: String,
    val amount: Double?,
    val timestamp: Instant,
    val bypassedWarning: Boolean = false,
)

fun relativeTime(now: Instant, then: Instant): String {
    val diff = now - then
    return when {
        diff < 1.minutes -> "just now"
        diff < 1.hours -> "${diff.inWholeMinutes}m ago"
        diff < 1.days -> "${diff.inWholeHours}h ago"
        diff < 2.days -> "Yesterday"
        else -> "${diff.inWholeDays}d ago"
    }
}
