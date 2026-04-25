package my.scamshield.feature.transfer.presentation.warning.bypass

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions
import my.scamshield.core.presentation.component.BilingualLabel
import my.scamshield.core.presentation.theme.AlertRed
import my.scamshield.core.presentation.theme.AlertRedBg

enum class BypassReason(val labelBm: String, val labelEn: String, val warningBm: String, val warningEn: String) {
    FAMILY(
        labelBm = "Keluarga atau kawan",
        labelEn = "Family or friend",
        warningBm = "Tipuan biasa: WhatsApp keluarga digodam dan scammer pura-pura jadi mereka. Pernahkah anda call mereka dan dengar suara untuk sahkan?",
        warningEn = "Common scam: a family member's WhatsApp is hacked and a scammer pretends to be them. Have you actually called them and heard their voice?",
    ),
    GOODS(
        labelBm = "Beli barang",
        labelEn = "Goods",
        warningBm = "Tipuan biasa: penjual fake. Sudahkah anda lihat barang itu sendiri atau hanya foto dari WhatsApp/Facebook?",
        warningEn = "Common scam: fake sellers. Have you seen the goods yourself, or only photos from WhatsApp/Facebook?",
    ),
    INVESTMENT(
        labelBm = "Pelaburan",
        labelEn = "Investment",
        warningBm = "Investment sebenar TIDAK PERNAH jamin keuntungan. Skim yang janji 20–100% untung adalah skim Ponzi atau penipuan.",
        warningEn = "Real investments NEVER guarantee returns. Schemes promising 20–100% profit are Ponzi schemes or scams.",
    ),
    VERIFY_ACCOUNT(
        labelBm = "Sahkan akaun",
        labelEn = "Verify account",
        warningBm = "TNG, Maybank, polis TIDAK PERNAH minta transfer wang untuk 'sahkan' akaun. Ini 99% adalah penipuan.",
        warningEn = "TNG, Maybank, the police NEVER ask you to transfer money to 'verify' your account. This is 99% a scam.",
    ),
    JOB_LOAN(
        labelBm = "Tawaran kerja atau pinjaman",
        labelEn = "Job or loan offer",
        warningBm = "Pekerjaan dan pinjaman sah tidak minta bayaran dahulu. Jika diminta bayar untuk dapatkan kerja/pinjaman, ini adalah tipuan.",
        warningEn = "Real jobs and loans don't ask for upfront payment. If you're asked to pay first to get a job or loan, this is a scam.",
    ),
    AUTHORITY(
        labelBm = "Polis atau bank suruh saya",
        labelEn = "Police or bank asked me to",
        warningBm = "Ini adalah pola Macau Scam. Polis sebenar TIDAK PERNAH call anda minta transfer wang. Letak telefon dan call NSRC 997.",
        warningEn = "This is the Macau Scam pattern. Real police NEVER call you asking to transfer money. Hang up and call NSRC 997.",
    ),
}

private sealed interface BypassStep {
    object PickReason : BypassStep
    data class Reflect(val reason: BypassReason) : BypassStep
    data class TypeConfirm(val reason: BypassReason, val typed: String) : BypassStep
}

private const val CONFIRM_PHRASE = "SAYA FAHAM"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BypassReasonSheet(
    onDismiss: () -> Unit,
    onConfirm: (BypassReason) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var step by remember { mutableStateOf<BypassStep>(BypassStep.PickReason) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 24.dp)) {
            when (val s = step) {
                BypassStep.PickReason -> PickReasonContent(
                    onSelected = { reason -> step = BypassStep.Reflect(reason) },
                )
                is BypassStep.Reflect -> ReflectContent(
                    reason = s.reason,
                    onContinue = { step = BypassStep.TypeConfirm(s.reason, "") },
                    onBack = { step = BypassStep.PickReason },
                )
                is BypassStep.TypeConfirm -> TypeConfirmContent(
                    reason = s.reason,
                    typed = s.typed,
                    onTypedChange = { step = BypassStep.TypeConfirm(s.reason, it) },
                    onConfirm = { onConfirm(s.reason) },
                    onBack = { step = BypassStep.Reflect(s.reason) },
                )
            }
        }
    }
}

@Composable
private fun PickReasonContent(onSelected: (BypassReason) -> Unit) {
    var selected by remember { mutableStateOf<BypassReason?>(null) }
    Text(
        text = "Kenapa anda hantar duit ini?",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
    )
    Text(
        text = "Why are you sending this money?",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
    )
    Spacer(Modifier.height(20.dp))
    BypassReason.entries.forEach { reason ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RadioButton(
                selected = selected == reason,
                onClick = { selected = reason },
            )
            Spacer(Modifier.size(4.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = reason.labelBm,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                )
                Text(
                    text = reason.labelEn,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                )
            }
        }
    }
    Spacer(Modifier.height(16.dp))
    Button(
        onClick = { selected?.let(onSelected) },
        enabled = selected != null,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
    ) {
        BilingualLabel(bm = "Teruskan", en = "Continue")
    }
}

@Composable
private fun ReflectContent(
    reason: BypassReason,
    onContinue: () -> Unit,
    onBack: () -> Unit,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = AlertRed,
            modifier = Modifier.size(28.dp),
        )
        Spacer(Modifier.size(12.dp))
        Text(
            text = "Sebelum anda hantar:",
            style = MaterialTheme.typography.titleMedium,
            color = AlertRed,
            fontWeight = FontWeight.Bold,
        )
    }
    Spacer(Modifier.height(16.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AlertRedBg, RoundedCornerShape(12.dp))
            .padding(16.dp),
    ) {
        Text(
            text = reason.warningBm,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = reason.warningEn,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
        )
    }
    Spacer(Modifier.height(20.dp))
    Button(
        onClick = onContinue,
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
    ) {
        BilingualLabel(
            bm = "Saya faham risiko — teruskan",
            en = "I understand the risk — continue",
        )
    }
    Spacer(Modifier.height(4.dp))
    TextButton(
        onClick = onBack,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("Go back")
    }
}

@Composable
private fun TypeConfirmContent(
    reason: BypassReason,
    typed: String,
    onTypedChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onBack: () -> Unit,
) {
    val matches = typed.trim().uppercase() == CONFIRM_PHRASE

    Text(
        text = "Last step",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = "Type SAYA FAHAM to confirm you understand the risk:",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface,
    )
    Spacer(Modifier.height(12.dp))
    OutlinedTextField(
        value = typed,
        onValueChange = onTypedChange,
        placeholder = { Text("SAYA FAHAM") },
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Characters,
            autoCorrect = false,
            keyboardType = KeyboardType.Ascii,
        ),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )
    Spacer(Modifier.height(16.dp))
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AlertRedBg, RoundedCornerShape(8.dp))
            .padding(12.dp),
    ) {
        Text(
            text = "⚠ Jika ini ternyata penipuan, anda mungkin tidak layak untuk pampasan.",
            style = MaterialTheme.typography.bodySmall,
            color = AlertRed,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "If this turns out to be a scam, you may not be eligible for reimbursement.",
            style = MaterialTheme.typography.labelSmall,
            color = AlertRed.copy(alpha = 0.8f),
        )
    }
    Spacer(Modifier.height(20.dp))
    Button(
        onClick = onConfirm,
        enabled = matches,
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
    ) {
        BilingualLabel(bm = "Hantar juga", en = "Send anyway")
    }
    Spacer(Modifier.height(4.dp))
    TextButton(
        onClick = onBack,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text("Go back")
    }
    Text(
        text = "Reason logged: ${reason.labelEn}",
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
    )
}
