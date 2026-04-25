package my.scamshield.feature.home.data.repository

import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Instant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import my.scamshield.core.domain.util.AppClock
import my.scamshield.feature.home.domain.model.ActivityItem
import my.scamshield.feature.home.domain.model.ActivityKind
import my.scamshield.feature.home.domain.repository.ActivityFeedRepository
import my.scamshield.feature.transfer.domain.model.Transaction

class InMemoryActivityFeedRepository(
    private val clock: AppClock,
) : ActivityFeedRepository {

    private val _items = MutableStateFlow(seed())
    override val items: StateFlow<List<ActivityItem>> = _items.asStateFlow()

    private val _holdsByPhone = MutableStateFlow<Map<String, Instant>>(emptyMap())
    override val holdsByPhone: StateFlow<Map<String, Instant>> = _holdsByPhone.asStateFlow()

    override fun recordSent(transaction: Transaction, transactionId: String, bypassedWarning: Boolean) {
        val titleSuffix = if (bypassedWarning) " (bypassed warning)" else ""
        _items.update { current ->
            listOf(
                ActivityItem(
                    id = transactionId,
                    kind = ActivityKind.SENT,
                    title = "Sent to ${transaction.recipient.displayName}$titleSuffix",
                    subtitle = transaction.recipient.phone,
                    amount = transaction.amount,
                    timestamp = clock.now(),
                    bypassedWarning = bypassedWarning,
                ),
            ) + current
        }
    }

    override fun recordBlocked(transaction: Transaction, reasonShort: String) {
        _items.update { current ->
            listOf(
                ActivityItem(
                    id = "blocked-${clock.now().toEpochMilliseconds()}",
                    kind = ActivityKind.BLOCKED,
                    title = "Blocked: Suspicious transfer",
                    subtitle = "${transaction.recipient.phone} · $reasonShort",
                    amount = transaction.amount,
                    timestamp = clock.now(),
                ),
            ) + current
        }
    }

    override fun recordHeld(transaction: Transaction) {
        val now = clock.now()
        val until = now + 24.hours
        _items.update { current ->
            listOf(
                ActivityItem(
                    id = "held-${now.toEpochMilliseconds()}",
                    kind = ActivityKind.HELD,
                    title = "Held: Transfer to ${transaction.recipient.displayName}",
                    subtitle = "${transaction.recipient.phone} · until tomorrow",
                    amount = transaction.amount,
                    timestamp = now,
                ),
            ) + current
        }
        _holdsByPhone.update { current -> current + (transaction.recipient.phone to until) }
    }

    override fun isHeld(phone: String): Boolean {
        val until = _holdsByPhone.value[phone] ?: return false
        return clock.now() < until
    }

    private fun seed(): List<ActivityItem> {
        val now = clock.now()
        return listOf(
            ActivityItem(
                id = "seed-sent-mum",
                kind = ActivityKind.SENT,
                title = "Sent to Mum",
                subtitle = "+60 12-987 6543",
                amount = 200.0,
                timestamp = now - 3.days,
            ),
            ActivityItem(
                id = "seed-blocked-old",
                kind = ActivityKind.BLOCKED,
                title = "Blocked: Suspicious transfer",
                subtitle = "+60 13-XXXX 4421 · Akaun keldai",
                amount = 850.0,
                timestamp = now - 5.days,
            ),
        )
    }
}
