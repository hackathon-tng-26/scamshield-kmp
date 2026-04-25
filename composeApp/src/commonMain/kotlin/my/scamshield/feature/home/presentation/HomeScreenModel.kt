package my.scamshield.feature.home.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import my.scamshield.core.domain.wallet.WALLET_BALANCE_RM
import my.scamshield.feature.home.domain.model.ActivityItem
import my.scamshield.feature.home.domain.repository.ActivityFeedRepository

class HomeScreenModel(
    activityFeed: ActivityFeedRepository,
) : ScreenModel {
    val activity: StateFlow<List<ActivityItem>> = activityFeed.items
    val balanceRm: Double = WALLET_BALANCE_RM

    private val _hasPlayedScamShieldEntry = MutableStateFlow(false)
    val hasPlayedScamShieldEntry: StateFlow<Boolean> = _hasPlayedScamShieldEntry.asStateFlow()

    private val _introducedActivityIds = MutableStateFlow<Set<String>>(emptySet())
    val introducedActivityIds: StateFlow<Set<String>> = _introducedActivityIds.asStateFlow()

    private val _lastDisplayedBlockedCount = MutableStateFlow(0)
    val lastDisplayedBlockedCount: StateFlow<Int> = _lastDisplayedBlockedCount.asStateFlow()

    fun setLastDisplayedBlockedCount(count: Int) {
        if (_lastDisplayedBlockedCount.value != count) {
            _lastDisplayedBlockedCount.value = count
        }
    }

    fun markScamShieldEntryPlayed() {
        if (!_hasPlayedScamShieldEntry.value) {
            _hasPlayedScamShieldEntry.value = true
        }
    }

    fun markActivityIntroduced(id: String) {
        if (!_introducedActivityIds.value.contains(id)) {
            _introducedActivityIds.value = _introducedActivityIds.value + id
        }
    }
}
