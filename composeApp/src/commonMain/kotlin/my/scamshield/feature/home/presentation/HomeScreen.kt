package my.scamshield.feature.home.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlin.time.Instant
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import io.github.alexzhirkevich.compottie.LottieCompositionSpec
import io.github.alexzhirkevich.compottie.animateLottieCompositionAsState
import io.github.alexzhirkevich.compottie.rememberLottieComposition
import io.github.alexzhirkevich.compottie.rememberLottiePainter
import my.scamshield.core.domain.util.AppClock
import my.scamshield.core.presentation.component.animatedTraceBorder
import my.scamshield.core.presentation.i18n.localeText
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.painterResource
import scamshield.composeapp.generated.resources.Res
import scamshield.composeapp.generated.resources.ic_scan_qr
import my.scamshield.core.presentation.theme.AlertRed
import my.scamshield.core.presentation.theme.AlertRedBg
import my.scamshield.core.presentation.theme.SafeGreen
import my.scamshield.core.presentation.theme.SafeGreenBg
import my.scamshield.core.presentation.theme.WarnOrange
import my.scamshield.core.presentation.theme.WarnOrangeBg
import my.scamshield.core.presentation.util.toRmAmount
import my.scamshield.feature.auth.domain.repository.SessionRepository
import my.scamshield.feature.home.domain.model.ActivityItem
import my.scamshield.feature.home.domain.model.ActivityKind
import my.scamshield.feature.home.domain.model.relativeTime
import my.scamshield.feature.home.presentation.blocked.BlockedListScreen
import my.scamshield.feature.transfer.presentation.compose.TransferComposeScreen
import org.koin.compose.koinInject

class HomeScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val session: SessionRepository = koinInject()
        val appClock: AppClock = koinInject()
        val model = koinScreenModel<HomeScreenModel>()
        val user by session.currentUser.collectAsState()
        val activity by model.activity.collectAsStateWithLifecycle()

        val snackbarHost = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()
        val now = remember { appClock.now() }
        val greetingPair = remember(now) { greetingFor(now) }
        val greeting = localeText(bm = greetingPair.first, en = greetingPair.second)

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHost) },
            containerColor = MaterialTheme.colorScheme.background,
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            ) {
                Text(
                    text = greeting,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                )
                Text(
                    text = user?.name ?: "—",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            text = localeText(bm = "Baki", en = "Balance"),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "RM ${model.balanceRm.toRmAmount()}",
                            style = MaterialTheme.typography.displaySmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                val blockedCount = activity.count { it.kind == ActivityKind.BLOCKED }
                val cardShape = RoundedCornerShape(16.dp)
                var traceTarget by remember { mutableStateOf(0f) }
                var traceComplete by remember { mutableStateOf(false) }
                var countUpTrigger by remember { mutableStateOf(false) }
                val traceProgress by animateFloatAsState(
                    targetValue = traceTarget,
                    animationSpec = tween(
                        durationMillis = 2000,
                        easing = FastOutSlowInEasing,
                    ),
                    finishedListener = { if (it >= 1f) traceComplete = true },
                    label = "scamShieldBorderTrace",
                )
                val animatedBlockedCount by animateIntAsState(
                    targetValue = if (countUpTrigger) blockedCount else 0,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessLow,
                    ),
                    label = "scamShieldBlockedCount",
                )
                LaunchedEffect(Unit) {
                    traceTarget = 1f
                }
                Card(
                    onClick = { navigator.push(BlockedListScreen()) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .animatedTraceBorder(
                            progress = traceProgress,
                            color = SafeGreen,
                            width = 2.5.dp,
                            shape = cardShape,
                        ),
                    colors = CardDefaults.cardColors(containerColor = SafeGreenBg),
                    shape = cardShape,
                ) {
                    Row(
                        modifier = Modifier.padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier.size(44.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            if (traceComplete) {
                                ScamShieldLottieIcon(
                                    modifier = Modifier.size(44.dp),
                                    triggerProgress = 0.30f,
                                    onTrigger = { countUpTrigger = true },
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = SafeGreen,
                                    modifier = Modifier.size(36.dp),
                                )
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "ScamShield",
                                style = MaterialTheme.typography.labelLarge,
                                color = SafeGreen,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                AnimatedContent(
                                    targetState = animatedBlockedCount,
                                    transitionSpec = {
                                        (slideInVertically(tween(150)) { it / 6 } + fadeIn(tween(150)))
                                            .togetherWith(
                                                slideOutVertically(tween(150)) { -it / 6 } + fadeOut(tween(150)),
                                            )
                                            .using(SizeTransform(clip = false))
                                    },
                                    label = "scamShieldBlockedCountSlide",
                                ) { count ->
                                    Text(
                                        text = "$count",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onBackground,
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = localeText(
                                        bm = "penipuan disekat bulan ini",
                                        en = "scams blocked this month",
                                    ),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                    modifier = Modifier.padding(bottom = 6.dp),
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = SafeGreen.copy(alpha = 0.6f),
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    text = localeText(bm = "Aktiviti terkini", en = "Recent activity"),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                )
                Spacer(Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    val visible = activity.take(3)
                    Column {
                        visible.forEachIndexed { index, item ->
                            ActivityRow(item, now)
                            if (index < visible.lastIndex) {
                                HorizontalDivider(
                                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    val scanSoonMsg = localeText(
                        bm = "Imbas untuk bayar akan datang",
                        en = "Scan to pay coming soon",
                    )
                    val scanLabel = localeText(bm = "Imbas QR", en = "Scan QR")
                    Surface(
                        onClick = {
                            scope.launch { snackbarHost.showSnackbar(scanSoonMsg) }
                        },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(56.dp),
                    ) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_scan_qr),
                            contentDescription = scanLabel,
                            tint = Color.White,
                            modifier = Modifier
                                .padding(14.dp)
                                .size(28.dp),
                        )
                    }
                    Button(
                        onClick = { navigator.push(TransferComposeScreen()) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp),
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = localeText(bm = "Hantar Duit", en = "Send Money"),
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ActivityRow(item: ActivityItem, now: Instant) {
    val isBlocked = item.kind == ActivityKind.BLOCKED
    val isHeld = item.kind == ActivityKind.HELD
    val rowBg = when {
        isBlocked -> AlertRedBg
        isHeld -> WarnOrangeBg
        else -> Color.Transparent
    }
    val icon = when {
        isBlocked -> Icons.Default.Block
        isHeld -> Icons.Default.Schedule
        else -> Icons.Default.CheckCircle
    }
    val iconTint = when {
        isBlocked -> AlertRed
        isHeld -> WarnOrange
        else -> SafeGreen
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(rowBg)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(24.dp),
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
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            item.amount?.let {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "RM ${it.toRmAmount()}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = if (isBlocked) SafeGreen else MaterialTheme.colorScheme.onSurface,
                    )
                    if (isBlocked) {
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = localeText(bm = "diselamatkan", en = "saved"),
                            style = MaterialTheme.typography.labelSmall,
                            color = SafeGreen.copy(alpha = 0.7f),
                        )
                    }
                }
            }
            Text(
                text = relativeTime(now, item.timestamp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            )
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun ScamShieldLottieIcon(
    modifier: Modifier = Modifier,
    triggerProgress: Float = 1f,
    onTrigger: () -> Unit = {},
) {
    val composition by rememberLottieComposition {
        val bytes = Res.readBytes("files/security_safe.json")
        LottieCompositionSpec.JsonString(bytes.decodeToString())
    }
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
    )
    var notified by remember { mutableStateOf(false) }
    LaunchedEffect(progress) {
        if (progress >= triggerProgress && !notified) {
            notified = true
            onTrigger()
        }
    }
    Image(
        painter = rememberLottiePainter(composition = composition, progress = { progress }),
        contentDescription = null,
        modifier = modifier,
    )
}

private fun greetingFor(now: Instant): Pair<String, String> {
    val hour = now.toLocalDateTime(TimeZone.currentSystemDefault()).hour
    return when (hour) {
        in 5..11 -> "Selamat pagi," to "Good morning,"
        in 12..16 -> "Selamat tengah hari," to "Good afternoon,"
        in 17..20 -> "Selamat petang," to "Good evening,"
        else -> "Selamat datang kembali," to "Welcome back,"
    }
}
