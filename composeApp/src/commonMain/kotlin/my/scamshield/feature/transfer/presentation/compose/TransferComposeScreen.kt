package my.scamshield.feature.transfer.presentation.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import my.scamshield.core.domain.wallet.WALLET_BALANCE_RM
import my.scamshield.core.presentation.i18n.localeText
import my.scamshield.core.presentation.theme.AlertRed
import my.scamshield.core.presentation.theme.AlertRedBg
import my.scamshield.core.presentation.theme.NeutralMuted
import my.scamshield.core.presentation.theme.SafeGreen
import my.scamshield.core.presentation.theme.SafeGreenBg
import my.scamshield.core.presentation.theme.WarnOrange
import my.scamshield.core.presentation.theme.WarnOrangeBg
import my.scamshield.core.presentation.util.toRmAmount
import my.scamshield.feature.home.domain.repository.ActivityFeedRepository
import my.scamshield.feature.transfer.domain.model.Recipient
import my.scamshield.feature.transfer.domain.model.Transaction
import my.scamshield.feature.transfer.presentation.confirm.TransferConfirmScreen
import org.koin.compose.koinInject

private data class PurposeOption(val bm: String, val en: String)

private val PURPOSE_OPTIONS = listOf(
    PurposeOption("Keluarga atau kawan", "Family or friend"),
    PurposeOption("Bil atau sewa", "Bill or rent"),
    PurposeOption("Pembelian online", "Online purchase"),
    PurposeOption("Pelaburan", "Investment"),
    PurposeOption("Hadiah atau bayaran balik", "Prize or refund"),
    PurposeOption("Lain-lain", "Other"),
)

private val AVATAR_PALETTE = listOf(
    Color(0xFF1976D2),
    Color(0xFF7B1FA2),
    Color(0xFF00838F),
    Color(0xFF455A64),
)

class TransferComposeScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: TransferComposeViewModel = koinScreenModel()
        val activityFeed: ActivityFeedRepository = koinInject()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val holds by activityFeed.holdsByPhone.collectAsStateWithLifecycle()
        val isHeld = state.recipientPhone.isNotBlank() && holds.containsKey(state.recipientPhone)

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(localeText(bm = "Hantar Duit", en = "Send Money")) },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                    .padding(horizontal = 24.dp, vertical = 16.dp),
            ) {
                Text(
                    text = localeText(bm = "Terkini", en = "Recents"),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                )
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    viewModel.contacts.forEachIndexed { index, contact ->
                        ContactChip(
                            contact = contact,
                            avatarColor = AVATAR_PALETTE[index % AVATAR_PALETTE.size],
                            isSelected = state.recipientPhone == contact.phone,
                            onClick = { viewModel.selectContact(contact) },
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                OutlinedTextField(
                    value = state.recipientPhone,
                    onValueChange = viewModel::onPhoneChanged,
                    label = { Text(localeText(bm = "Nombor penerima", en = "Recipient phone")) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                if (state.trustLevel != TrustLevel.UNKNOWN) {
                    Spacer(Modifier.height(8.dp))
                    if (state.trustLevel != TrustLevel.CHECKING) {
                        ResolvedNameLabel(state.resolvedName)
                        Spacer(Modifier.height(6.dp))
                    }
                    TrustBadge(state.trustLevel)
                }

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.amountRm,
                    onValueChange = viewModel::onAmountChanged,
                    label = { Text(localeText(bm = "Jumlah (RM)", en = "Amount (RM)")) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = localeText(
                        bm = "Baki: RM ${WALLET_BALANCE_RM.toRmAmount()}",
                        en = "Available: RM ${WALLET_BALANCE_RM.toRmAmount()}",
                    ),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.End),
                )

                Spacer(Modifier.height(12.dp))

                PurposeDropdown(
                    selected = state.purpose,
                    onSelect = viewModel::onPurposeSelected,
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = state.note,
                    onValueChange = viewModel::onNoteChanged,
                    label = { Text(localeText(bm = "Nota (pilihan)", en = "Note (optional)")) },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.weight(1f))

                if (isHeld) {
                    Text(
                        text = localeText(
                            bm = "Anda sudah tahan pindahan ke nombor ini — masih ditahan sehingga esok",
                            en = "You held a transfer to this number earlier — still on hold until tomorrow",
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                }

                val parsedAmount = state.amountRm.toDoubleOrNull()
                Button(
                    onClick = {
                        val matchedContact = viewModel.contacts.firstOrNull {
                            it.phone.filter { c -> c.isDigit() } ==
                                state.recipientPhone.filter { c -> c.isDigit() }
                        }
                        val recipientName = matchedContact?.displayName
                            ?: state.resolvedName
                            ?: state.recipientPhone
                        val isContact = matchedContact?.trust == TrustLevel.GREEN
                        val recipient = Recipient(
                            id = state.recipientPhone,
                            displayName = recipientName,
                            phone = state.recipientPhone,
                            isInContacts = isContact,
                            priorTransferCount = if (isContact) 47 else 0,
                            verifiedName = state.resolvedName,
                        )
                        val transaction = Transaction(
                            id = "pending",
                            senderId = "demo_user_01",
                            recipient = recipient,
                            amount = parsedAmount ?: 0.0,
                            note = state.note,
                        )
                        navigator.push(TransferConfirmScreen(transaction))
                    },
                    enabled = state.isValid && !isHeld,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                ) {
                    val continueLabel = localeText(bm = "Teruskan", en = "Continue")
                    val label = if (parsedAmount != null && parsedAmount > 0.0) {
                        "$continueLabel · RM ${parsedAmount.toRmAmount()}"
                    } else {
                        continueLabel
                    }
                    Text(label, style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
private fun ContactChip(
    contact: MockContact,
    avatarColor: Color,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val isFlagged = contact.trust == TrustLevel.RED
    val ringColor = when {
        isFlagged -> AlertRed
        isSelected -> MaterialTheme.colorScheme.primary
        else -> Color.Transparent
    }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(2.dp),
    ) {
        Box(
            modifier = Modifier
                .size(50.dp)
                .border(2.dp, ringColor, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(avatarColor),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = contact.displayName.first().toString(),
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = contact.displayName,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onBackground,
        )
        if (isFlagged) {
            Text(
                text = localeText(bm = "⚠ ditanda", en = "⚠ flagged"),
                style = MaterialTheme.typography.labelSmall,
                color = AlertRed,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun ResolvedNameLabel(resolvedName: String?) {
    val text = resolvedName?.let {
        localeText(bm = "Pemegang akaun: $it", en = "Account holder: $it")
    } ?: localeText(bm = "Nama tidak tersedia", en = "Name unavailable")
    val color = if (resolvedName != null) {
        MaterialTheme.colorScheme.onBackground
    } else {
        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
    }
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = color,
    )
}

@Composable
private fun TrustBadge(trust: TrustLevel) {
    val bg: Color
    val fg: Color
    val text: String
    val icon: ImageVector?
    when (trust) {
        TrustLevel.CHECKING -> {
            bg = Color(0xFFEEEEEE); fg = NeutralMuted
            text = localeText(bm = "Menyemak…", en = "Checking…"); icon = null
        }
        TrustLevel.GREEN -> {
            bg = SafeGreenBg; fg = SafeGreen
            text = localeText(
                bm = "Disahkan · digunakan 47× · 0 laporan",
                en = "Verified · used 47× · 0 reports",
            ); icon = null
        }
        TrustLevel.AMBER -> {
            bg = WarnOrangeBg; fg = WarnOrange
            text = localeText(
                bm = "Penerima baharu · kali pertama",
                en = "New recipient · first time",
            ); icon = null
        }
        TrustLevel.RED -> {
            bg = AlertRedBg; fg = AlertRed
            text = localeText(
                bm = "7 laporan minggu ini · akaun keldai",
                en = "7 reports this week · mule account",
            ); icon = Icons.Default.Shield
        }
        TrustLevel.UNKNOWN -> return
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = fg,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(6.dp))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = fg,
            fontWeight = FontWeight.Medium,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PurposeDropdown(selected: String?, onSelect: (String?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        val displayValue = selected?.let { sel ->
            PURPOSE_OPTIONS.firstOrNull { it.en == sel || it.bm == sel }?.let {
                localeText(bm = it.bm, en = it.en)
            } ?: sel
        } ?: ""
        OutlinedTextField(
            value = displayValue,
            onValueChange = {},
            readOnly = true,
            label = { Text(localeText(bm = "Untuk apa?", en = "What's this for?")) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            PURPOSE_OPTIONS.forEach { option ->
                DropdownMenuItem(
                    text = { Text(localeText(bm = option.bm, en = option.en)) },
                    onClick = {
                        onSelect(option.en)
                        expanded = false
                    },
                )
            }
        }
    }
}
