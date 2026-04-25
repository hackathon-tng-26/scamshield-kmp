package my.scamshield.feature.transfer.presentation.confirm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlin.math.absoluteValue
import my.scamshield.core.presentation.theme.AlertRed
import my.scamshield.core.presentation.theme.SafeGreen
import my.scamshield.core.presentation.theme.SafeGreenBg
import my.scamshield.core.presentation.theme.TngBlue
import my.scamshield.core.presentation.util.toRmAmount
import my.scamshield.feature.transfer.domain.model.Direction
import my.scamshield.feature.transfer.domain.model.FeatureContribution
import my.scamshield.feature.transfer.domain.model.RiskScore
import my.scamshield.feature.transfer.domain.model.Transaction
import my.scamshield.feature.transfer.domain.model.Verdict
import my.scamshield.feature.transfer.presentation.success.TransferSuccessScreen
import my.scamshield.feature.transfer.presentation.warning.ScamWarningScreen

class TransferConfirmScreen(
    private val transaction: Transaction,
) : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: TransferConfirmViewModel = koinScreenModel()
        val state by viewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(transaction.id) { viewModel.load(transaction) }

        LaunchedEffect(state.score) {
            val score = state.score ?: return@LaunchedEffect
            if (score.verdict == Verdict.RED) {
                navigator.push(ScamWarningScreen(transaction = transaction, score = score))
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Confirm transfer") },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    },
                )
            },
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(padding)
                    .padding(24.dp),
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("You're sending", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "RM ${transaction.amount.toRmAmount()}",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "to ${transaction.recipient.displayName}",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Text(
                            text = transaction.recipient.phone,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        )
                        Spacer(Modifier.height(6.dp))
                        VerifiedBadge(transaction.recipient.verifiedName)
                    }
                }

                Spacer(Modifier.height(16.dp))

                when {
                    state.isScoring -> ScoringCard()
                    state.score != null && state.score!!.verdict != Verdict.RED -> VerifiedCard(state.score!!)
                }

                Spacer(Modifier.weight(1f))

                Button(
                    onClick = {
                        viewModel.confirmSend { txId ->
                            navigator.replaceAll(TransferSuccessScreen(transaction, txId))
                        }
                    },
                    enabled = state.score != null && state.score!!.verdict != Verdict.RED && !state.isSending,
                    colors = ButtonDefaults.buttonColors(containerColor = TngBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                ) {
                    if (state.isSending) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.height(24.dp).padding(2.dp))
                    } else {
                        Text("Send RM ${transaction.amount.toRmAmount()}", style = MaterialTheme.typography.labelLarge)
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Face ID to confirm",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                )
            }
        }
    }
}

@Composable
private fun ScoringCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            CircularProgressIndicator(modifier = Modifier.height(20.dp))
            Text("ScamShield is scoring your transfer…")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun VerifiedCard(score: RiskScore) {
    var expanded by remember { mutableStateOf(false) }
    val isSafe = score.verdict == Verdict.GREEN
    val containerBg = if (isSafe) SafeGreenBg else MaterialTheme.colorScheme.tertiaryContainer
    val accent = if (isSafe) SafeGreen else MaterialTheme.colorScheme.tertiary

    Card(
        onClick = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerBg),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = accent,
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isSafe) "ScamShield: low risk" else "ScamShield: medium risk — please reconfirm",
                        style = MaterialTheme.typography.labelLarge,
                        color = if (isSafe) SafeGreen else MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                    Text(
                        text = "scored in ${score.latencyMs} ms · tap to see why",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    )
                }
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = accent,
                )
            }
            if (expanded && score.attribution.isNotEmpty()) {
                HorizontalDivider(color = accent.copy(alpha = 0.2f))
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    score.attribution.forEach { c -> AttributionRow(c) }
                }
            }
        }
    }
}

@Composable
private fun VerifiedBadge(verifiedName: String?) {
    val verified = verifiedName != null
    val text = if (verified) "✓ verified · DuitNow" else "Name unverified — proceed with caution"
    val color = if (verified) SafeGreen else MaterialTheme.colorScheme.tertiary
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = color,
        fontWeight = FontWeight.Medium,
    )
}

@Composable
private fun AttributionRow(c: FeatureContribution) {
    val raisesRisk = c.direction == Direction.POSITIVE
    val deltaColor = if (raisesRisk) AlertRed else SafeGreen
    val deltaSign = if (raisesRisk) "+" else "−"
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = c.feature,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
        )
        Text(
            text = "$deltaSign${c.contribution.absoluteValue}",
            style = MaterialTheme.typography.labelMedium,
            color = deltaColor,
            fontWeight = FontWeight.Bold,
        )
    }
}
