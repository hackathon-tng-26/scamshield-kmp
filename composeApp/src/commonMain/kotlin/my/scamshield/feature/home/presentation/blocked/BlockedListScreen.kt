package my.scamshield.feature.home.presentation.blocked

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import my.scamshield.core.domain.util.AppClock
import my.scamshield.core.presentation.theme.AlertRed
import my.scamshield.core.presentation.theme.AlertRedBg
import my.scamshield.core.presentation.theme.SafeGreen
import my.scamshield.core.presentation.theme.SafeGreenBg
import my.scamshield.core.presentation.util.toRmAmount
import my.scamshield.feature.home.domain.model.ActivityItem
import my.scamshield.feature.home.domain.model.ActivityKind
import my.scamshield.feature.home.domain.model.relativeTime
import my.scamshield.feature.home.presentation.HomeScreenModel
import org.koin.compose.koinInject

class BlockedListScreen : Screen {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<HomeScreenModel>()
        val appClock: AppClock = koinInject()
        val activity by model.activity.collectAsStateWithLifecycle()
        val now = remember { appClock.now() }
        val blocked = activity.filter { it.kind == ActivityKind.BLOCKED }
        val totalSaved = model.displayedAmountSavedRm

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Scams blocked") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                )
            },
            containerColor = MaterialTheme.colorScheme.background,
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
            ) {
                Spacer(Modifier.height(8.dp))
                SummaryCard(count = model.displayedBlockedCount, totalSaved = totalSaved)
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "All blocked transfers",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                )
                Spacer(Modifier.height(8.dp))
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxSize(),
                ) {
                    items(blocked, key = { it.id }) { item ->
                        BlockedRow(item, now)
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(count: Int, totalSaved: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SafeGreenBg),
        shape = RoundedCornerShape(16.dp),
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = SafeGreen,
                modifier = Modifier.size(40.dp),
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$count scams blocked this month",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "RM ${totalSaved.toRmAmount()} saved",
                    style = MaterialTheme.typography.bodyMedium,
                    color = SafeGreen,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun BlockedRow(item: ActivityItem, now: kotlin.time.Instant) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AlertRedBg),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.Default.Block,
                contentDescription = null,
                tint = AlertRed,
                modifier = Modifier.size(22.dp),
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = item.subtitle,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                item.amount?.let {
                    Text(
                        text = "RM ${it.toRmAmount()}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = SafeGreen,
                    )
                    Text(
                        text = "saved",
                        style = MaterialTheme.typography.labelSmall,
                        color = SafeGreen.copy(alpha = 0.7f),
                    )
                }
                Text(
                    text = relativeTime(now, item.timestamp),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                )
            }
        }
    }
}
