package my.scamshield.feature.transfer.presentation.success

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import my.scamshield.core.presentation.i18n.localeText
import my.scamshield.core.presentation.theme.SafeGreen
import my.scamshield.core.presentation.util.toRmAmount
import my.scamshield.feature.home.domain.repository.ActivityFeedRepository
import my.scamshield.feature.home.presentation.HomeScreen
import my.scamshield.feature.transfer.domain.model.Transaction
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.compose.koinInject
import scamshield.composeapp.generated.resources.Res

private const val CHECKED_HOLD_PROGRESS: Float = 50f / 63f
private const val CHECKED_PLAYBACK_MILLIS: Int = 2_080
private const val LABEL_INTRO_MILLIS: Int = 600
private const val LABEL_INTRO_OFFSET_DP: Int = 16
private const val LABEL_REST_ALPHA: Float = 0.85f

class TransferSuccessScreen(
    private val transaction: Transaction,
    private val transactionId: String,
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val activityFeed: ActivityFeedRepository = koinInject()
        LaunchedEffect(transactionId) {
            activityFeed.recordSent(transaction, transactionId)
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(32.dp),
        ) {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                var tickComplete by remember { mutableStateOf(false) }
                CheckedTickLottie(
                    modifier = Modifier.size(120.dp),
                    onLanded = { tickComplete = true },
                )
                Spacer(Modifier.height(24.dp))
                Text(
                    text = localeText(bm = "Dihantar", en = "Sent"),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = localeText(
                        bm = "RM ${transaction.amount.toRmAmount()} kepada ${transaction.recipient.displayName}",
                        en = "RM ${transaction.amount.toRmAmount()} to ${transaction.recipient.displayName}",
                    ),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = localeText(bm = "Ruj: $transactionId", en = "Ref: $transactionId"),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                )
                Spacer(Modifier.height(20.dp))
                WatchTransactionLabel(
                    visible = tickComplete,
                    text = localeText(
                        bm = "Kami akan pantau transaksi ini. Laporkan jika ada apa-apa tidak kena.",
                        en = "We'll watch this transaction. Report if anything goes wrong.",
                    ),
                )
            }
            OutlinedButton(
                onClick = { navigator.popUntil { it is HomeScreen } },
                colors = ButtonDefaults.outlinedButtonColors(contentColor = SafeGreen),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .align(Alignment.BottomCenter),
            ) {
                Text(
                    text = localeText(bm = "Kembali ke laman utama", en = "Back to home"),
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun CheckedTickLottie(
    modifier: Modifier = Modifier,
    onLanded: () -> Unit = {},
) {
    val composition by rememberLottieComposition {
        val bytes = Res.readBytes("files/checked.json")
        LottieCompositionSpec.JsonString(bytes.decodeToString())
    }
    val progress = remember { Animatable(0f) }
    var landed by remember { mutableStateOf(false) }

    LaunchedEffect(composition) {
        if (composition == null) return@LaunchedEffect
        progress.snapTo(0f)
        progress.animateTo(
            targetValue = CHECKED_HOLD_PROGRESS,
            animationSpec = tween(durationMillis = CHECKED_PLAYBACK_MILLIS, easing = LinearEasing),
        )
        if (!landed) {
            landed = true
            onLanded()
        }
    }

    Image(
        painter = rememberLottiePainter(composition = composition, progress = { progress.value }),
        contentDescription = null,
        modifier = modifier,
    )
}

@Composable
private fun WatchTransactionLabel(
    visible: Boolean,
    text: String,
    modifier: Modifier = Modifier,
) {
    val alpha = remember { Animatable(0f) }
    val offsetDp = remember { Animatable(LABEL_INTRO_OFFSET_DP.toFloat()) }

    LaunchedEffect(visible) {
        if (!visible) return@LaunchedEffect
        alpha.animateTo(
            targetValue = LABEL_REST_ALPHA,
            animationSpec = tween(durationMillis = LABEL_INTRO_MILLIS, easing = FastOutSlowInEasing),
        )
    }
    LaunchedEffect(visible) {
        if (!visible) return@LaunchedEffect
        offsetDp.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = LABEL_INTRO_MILLIS, easing = FastOutSlowInEasing),
        )
    }

    Text(
        text = text,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground,
        fontWeight = FontWeight.Medium,
        textAlign = TextAlign.Center,
        modifier = modifier
            .alpha(alpha.value)
            .offset { IntOffset(0, offsetDp.value.dp.roundToPx()) },
    )
}
