package my.scamshield.feature.home.domain.repository

import kotlinx.coroutines.flow.StateFlow
import my.scamshield.feature.home.domain.model.ActivityItem
import my.scamshield.feature.transfer.domain.model.Transaction

interface ActivityFeedRepository {
    val items: StateFlow<List<ActivityItem>>
    fun recordSent(transaction: Transaction, transactionId: String)
    fun recordBlocked(transaction: Transaction, reasonShort: String)
}
