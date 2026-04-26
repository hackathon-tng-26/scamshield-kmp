package my.scamshield.feature.transfer.presentation.warning

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import my.scamshield.core.presentation.component.LocaleToggle
import my.scamshield.core.presentation.component.orbitingTraceBorder
import my.scamshield.core.presentation.i18n.localeText
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
                        text = localeText(
                            bm = "Kenapa kami tanya:",
                            en = "Why we're asking:",
                        ),
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

                    val actionsSlideDp = remember { Animatable(BUTTON_GROUP_SLIDE_OFFSET_DP.toFloat()) }
                    val actionsAlpha = remember { Animatable(0f) }
                    LaunchedEffect(Unit) {
                        launch {
                            actionsSlideDp.animateTo(
                                targetValue = 0f,
                                animationSpec = tween(
                                    durationMillis = BUTTON_GROUP_INTRO_MILLIS,
                                    delayMillis = BUTTON_GROUP_INTRO_DELAY_MILLIS,
                                    easing = FastOutSlowInEasing,
                                ),
                            )
                        }
                        launch {
                            actionsAlpha.animateTo(
                                targetValue = 1f,
                                animationSpec = tween(
                                    durationMillis = BUTTON_GROUP_INTRO_MILLIS,
                                    delayMillis = BUTTON_GROUP_INTRO_DELAY_MILLIS,
                                    easing = FastOutSlowInEasing,
                                ),
                            )
                        }
                    }
                    val cancelPulse = rememberInfiniteTransition(label = "cancelButtonPulse")
                    val cancelScale by cancelPulse.animateFloat(
                        initialValue = 1f,
                        targetValue = CANCEL_BUTTON_PULSE_SCALE,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = CANCEL_BUTTON_PULSE_HALF_MILLIS,
                                easing = FastOutSlowInEasing,
                            ),
                            repeatMode = RepeatMode.Reverse,
                        ),
                        label = "cancelButtonScale",
                    )
                    val cancelBorderHead by cancelPulse.animateFloat(
                        initialValue = 0f,
                        targetValue = 1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(
                                durationMillis = CANCEL_BORDER_ORBIT_MILLIS,
                                easing = LinearEasing,
                            ),
                            repeatMode = RepeatMode.Restart,
                        ),
                        label = "cancelBorderOrbit",
                    )
                    val cancelButtonShape = RoundedCornerShape(20.dp)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(actionsAlpha.value)
                            .offset { IntOffset(0, actionsSlideDp.value.dp.roundToPx()) },
                    ) {
                        Button(
                            onClick = {
                                activityFeed.recordBlocked(transaction, "Akaun keldai")
                                navigator.popUntil { it is HomeScreen }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                            shape = cancelButtonShape,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .graphicsLayer {
                                    scaleX = cancelScale
                                    scaleY = cancelScale
                                }
                                .orbitingTraceBorder(
                                    headFraction = cancelBorderHead,
                                    tailLengthFraction = CANCEL_BORDER_TAIL_FRACTION,
                                    color = Color.White,
                                    width = 2.dp,
                                    shape = cancelButtonShape,
                                ),
                        ) {
                            Text(
                                text = localeText(
                                    bm = "Ya, batalkan — saya tak kenal",
                                    en = "Yes, cancel — I don't know them",
                                ),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
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
                            Text(
                                text = localeText(
                                    bm = "Hubungi NSRC 997",
                                    en = "Call NSRC 997",
                                ),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        OutlinedButton(
                            onClick = {
                                activityFeed.recordHeld(transaction)
                                navigator.popUntil { it is HomeScreen }
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
                            Text(
                                text = localeText(
                                    bm = "Tahan 24 jam — saya semak dulu",
                                    en = "Hold for 24h — let me check first",
                                ),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                        val bypassEnabled = coolingSecondsLeft == 0
                        TextButton(
                            onClick = { showBypassSheet = true },
                            enabled = bypassEnabled,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                        ) {
                            val labelAlpha = if (bypassEnabled) 0.5f else 0.3f
                            val labelColor = MaterialTheme.colorScheme.onBackground.copy(alpha = labelAlpha)
                            val label = if (bypassEnabled) {
                                localeText(
                                    bm = "Saya masih nak teruskan",
                                    en = "I still want to proceed",
                                )
                            } else {
                                localeText(
                                    bm = "Saya masih nak teruskan (tunggu ${coolingSecondsLeft}s)",
                                    en = "I still want to proceed (wait ${coolingSecondsLeft}s)",
                                )
                            }
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                color = labelColor,
                                fontWeight = FontWeight.Medium,
                            )
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
                            navigator.popUntil { it is HomeScreen }
                            navigator.push(TransferSuccessScreen(transaction, txId))
                        }.onFailure {
                            navigator.popUntil { it is HomeScreen }
                        }
                    }
                },
            )
        }
    }

    companion object {
        private const val BYPASS_COOLING_SEC = 30
        private const val BUTTON_GROUP_SLIDE_OFFSET_DP = 56
        private const val BUTTON_GROUP_INTRO_MILLIS = 600
        private const val BUTTON_GROUP_INTRO_DELAY_MILLIS = 180
        private const val CANCEL_BUTTON_PULSE_SCALE = 1.035f
        private const val CANCEL_BUTTON_PULSE_HALF_MILLIS = 1_200
        private const val CANCEL_BORDER_ORBIT_MILLIS = 2_400
        private const val CANCEL_BORDER_TAIL_FRACTION = 0.32f
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
        Text(
            text = localeText(
                bm = "Awak pasti kenal orang ini?",
                en = "Are you sure you know this person?",
            ),
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
        )
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
                text = localeText(
                    bm = "Anda hampir hantar",
                    en = "You were about to send",
                ),
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
                    text = localeText(
                        bm = "kepada  ${recipient.verifiedName}",
                        en = "to  ${recipient.verifiedName}",
                    ),
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
                    text = localeText(
                        bm = "✓ disahkan · DuitNow",
                        en = "✓ verified · DuitNow",
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            } else {
                Text(
                    text = localeText(
                        bm = "kepada  ${recipient.phone}  (penerima baharu)",
                        en = "to  ${recipient.phone}  (new recipient)",
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = localeText(
                        bm = "Nama tidak disahkan — tidak dapat sahkan dengan DuitNow",
                        en = "Name unverified — could not confirm with DuitNow",
                    ),
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
                text = localeText(
                    bm = "Skor risiko",
                    en = "Risk score",
                ),
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
                text = localeText(
                    bm = "Kami berhenti seketika apa-apa yang melebihi 70.",
                    en = "We pause anything above 70.",
                ),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
        }
    }
}

