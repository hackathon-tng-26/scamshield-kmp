package my.scamshield.feature.home.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.StateFlow
import my.scamshield.core.domain.wallet.WALLET_BALANCE_RM
import my.scamshield.feature.home.domain.model.ActivityItem
import my.scamshield.feature.home.domain.repository.ActivityFeedRepository

class HomeScreenModel(
    activityFeed: ActivityFeedRepository,
) : ScreenModel {
    val activity: StateFlow<List<ActivityItem>> = activityFeed.items
    val balanceRm: Double = WALLET_BALANCE_RM
}
