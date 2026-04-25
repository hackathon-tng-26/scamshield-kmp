package my.scamshield.feature.transfer.presentation.warning

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import my.scamshield.core.presentation.component.BilingualLabel
import my.scamshield.core.presentation.component.LocaleToggle
import my.scamshield.core.presentation.theme.AlertRed
import my.scamshield.core.presentation.theme.AlertRedBg
import my.scamshield.core.presentation.theme.WarnOrange
import my.scamshield.core.presentation.util.toRmAmount
import my.scamshield.core.platform.Caller
import my.scamshield.feature.home.domain.repository.ActivityFeedRepository
import my.scamshield.feature.transfer.domain.usecase.ExecuteTransferUseCase
import my.scamshield.feature.transfer.presentation.success.TransferSuccessScreen
import my.scamshield.feature.transfer.presentation.warning.bypass.BypassReasonSheet
import my.scamshield.feature.home.presentation.HomeScreen
import my.scamshield.feature.transfer.domain.model.RiskScore
import my.scamshield.feature.transfer.domain.model.Transaction
import org.koin.compose.koinInject

class ScamWarningScreen(
    private val transaction: Transaction,
    private val score: RiskScore,
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val activityFeed: ActivityFeedRepository = koinInject()
        val caller: Caller = koinInject()
        val executeTransfer: ExecuteTransferUseCase = koinInject()
        val scope = rememberCoroutineScope()
        var showBypassSheet by remember { mutableStateOf(false) }

        var coolingSecondsLeft by remember { mutableStateOf(BYPASS_COOLING_SEC) }
        LaunchedEffect(Unit) {
            while (coolingSecondsLeft > 0) {
                delay(1_000L)
                coolingSecondsLeft -= 1
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(AlertRedBg)
                .windowInsetsPadding(WindowInsets.systemBars),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                HeaderBar()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                ) {
                    TransactionSummaryCard(transaction)
                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = "Kenapa kami tanya:",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color(0xFF7A0F19),
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(Modifier.height(10.dp))
                    score.explanationHighlights.forEach { item ->
                        EvidenceBullet(item)
                    }

                    Spacer(Modifier.height(16.dp))
                    RiskScoreCard(score.score)

                    Spacer(Modifier.weight(1f))

                    Button(
                        onClick = {
                            activityFeed.recordBlocked(transaction, "Akaun keldai")
                            navigator.replaceAll(HomeScreen())
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                    ) {
                        BilingualLabel(
                            bm = "Ya, batalkan — saya tak kenal",
                            en = "Yes, cancel — I don't know them",
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = { caller.dial("997") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AlertRed),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            modifier = Modifier.height(20.dp),
                        )
                        Spacer(Modifier.padding(horizontal = 4.dp))
                        BilingualLabel(
                            bm = "Hubungi NSRC 997",
                            en = "Call National Scam Response Centre",
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = {
                            activityFeed.recordHeld(transaction)
                            navigator.replaceAll(HomeScreen())
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WarnOrange),
                    ) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            modifier = Modifier.height(20.dp),
                        )
                        Spacer(Modifier.padding(horizontal = 4.dp))
                        BilingualLabel(
                            bm = "Tahan 24 jam — saya semak dulu",
                            en = "Hold for 24h — let me check first",
                        )
                    }
                    val bypassEnabled = coolingSecondsLeft == 0
                    TextButton(
                        onClick = { showBypassSheet = true },
                        enabled = bypassEnabled,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    ) {
                        val alpha = if (bypassEnabled) 0.5f else 0.3f
                        val labelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = alpha)
                        val bm = if (bypassEnabled) {
                            "Saya masih nak teruskan"
                        } else {
                            "Saya masih nak teruskan (tunggu ${coolingSecondsLeft}s)"
                        }
                        val en = if (bypassEnabled) {
                            "I still want to proceed"
                        } else {
                            "I still want to proceed (wait ${coolingSecondsLeft}s)"
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(bm, style = MaterialTheme.typography.labelMedium, color = labelColor, fontWeight = FontWeight.Medium)
                            Text(en, style = MaterialTheme.typography.labelSmall, color = labelColor)
                        }
                    }
                }
            }
        }

        if (showBypassSheet) {
            BypassReasonSheet(
                onDismiss = { showBypassSheet = false },
                onConfirm = { _ ->
                    showBypassSheet = false
                    scope.launch {
                        val result = executeTransfer(transaction)
                        result.onSuccess { txId ->
                            activityFeed.recordSent(transaction, txId, bypassedWarning = true)
                            navigator.replaceAll(TransferSuccessScreen(transaction, txId))
                        }.onFailure {
                            navigator.replaceAll(HomeScreen())
                        }
                    }
                },
            )
        }
    }

    companion object {
        private const val BYPASS_COOLING_SEC = 30
    }
}

@Composable
private fun HeaderBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AlertRed)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.height(28.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Awak pasti kenal orang ini?",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Are you sure you know this person?",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
            )
        }
        LocaleToggle(onDark = true)
    }
}

@Composable
private fun TransactionSummaryCard(transaction: Transaction) {
    val recipient = transaction.recipient
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "You were about to send",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "RM ${transaction.amount.toRmAmount()}",
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
            )
            Spacer(Modifier.height(4.dp))
            if (recipient.verifiedName != null) {
                Text(
                    text = "to  ${recipient.verifiedName}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = recipient.phone,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "✓ verified · DuitNow",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            } else {
                Text(
                    text = "to  ${recipient.phone}  (new recipient)",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Name unverified — could not confirm with DuitNow",
                    style = MaterialTheme.typography.labelSmall,
                    color = AlertRed,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun EvidenceBullet(text: String) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.padding(vertical = 4.dp),
    ) {
        Text(
            text = "•  ",
            color = AlertRed,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )
    }
}

@Composable
private fun RiskScoreCard(score: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Risk score",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = "$score / 100",
                    style = MaterialTheme.typography.titleLarge,
                    color = AlertRed,
                    fontWeight = FontWeight.Bold,
                )
                LinearProgressIndicator(
                    progress = { score / 100f },
                    color = AlertRed,
                    trackColor = Color(0xFFE0E0E0),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                )
            }
            Spacer(Modifier.height(6.dp))
            Text(
                text = "We pause anything above 70.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
        }
    }
}

