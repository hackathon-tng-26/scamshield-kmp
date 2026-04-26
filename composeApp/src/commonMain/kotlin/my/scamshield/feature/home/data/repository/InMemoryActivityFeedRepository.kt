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
        val blocked = listOf(
            Triple("+60 13-XXXX 4421 · Akaun keldai", 850.0, 5.days),
            Triple("+60 17-XXXX 8899 · Polis tiruan (Macau Scam)", 3500.0, 2.days),
            Triple("+60 11-XXXX 2233 · Pelaburan palsu", 1200.0, 4.days),
            Triple("+60 12-XXXX 9988 · Sahkan akaun (impersonation)", 450.0, 7.days),
            Triple("+60 16-XXXX 5544 · Tipuan kerja online", 680.0, 9.days),
            Triple("+60 13-XXXX 7766 · Penjual fake (Shopee)", 320.0, 11.days),
            Triple("+60 19-XXXX 1122 · Akaun keldai", 2100.0, 14.days),
            Triple("+60 14-XXXX 3344 · Pelaburan crypto palsu", 5000.0, 17.days),
            Triple("+60 12-XXXX 6677 · Tipuan pinjaman", 900.0, 20.days),
            Triple("+60 17-XXXX 9911 · Polis tiruan (Macau Scam)", 4200.0, 23.days),
            Triple("+60 11-XXXX 4488 · Akaun keldai", 1750.0, 27.days),
        )
        return buildList {
            add(
                ActivityItem(
                    id = "seed-sent-siti",
                    kind = ActivityKind.SENT,
                    title = "Sent to Siti",
                    subtitle = "+60 12-345 6789",
                    amount = 200.0,
                    timestamp = now - 3.days,
                )
            )
            blocked.forEachIndexed { index, (subtitle, amount, age) ->
                add(
                    ActivityItem(
                        id = "seed-blocked-$index",
                        kind = ActivityKind.BLOCKED,
                        title = "Blocked: Suspicious transfer",
                        subtitle = subtitle,
                        amount = amount,
                        timestamp = now - age,
                    )
                )
            }
        }
    }
}
