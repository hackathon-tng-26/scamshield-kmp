package my.scamshield.feature.home.domain.repository

import kotlin.time.Instant
import kotlinx.coroutines.flow.StateFlow
import my.scamshield.feature.home.domain.model.ActivityItem
import my.scamshield.feature.transfer.domain.model.Transaction

interface ActivityFeedRepository {
    val items: StateFlow<List<ActivityItem>>
    val holdsByPhone: StateFlow<Map<String, Instant>>
    fun recordSent(transaction: Transaction, transactionId: String, bypassedWarning: Boolean = false)
    fun recordBlocked(transaction: Transaction, reasonShort: String)
    fun recordHeld(transaction: Transaction)
    fun isHeld(phone: String): Boolean
}
