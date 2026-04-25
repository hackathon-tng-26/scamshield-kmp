package my.scamshield.feature.transfer.presentation.compose

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MockContact(
    val displayName: String,
    val phone: String,
    val suggestedAmount: Double,
    val accountHolder: String,
    val trust: TrustLevel,
)

class TransferComposeViewModel : ScreenModel {

    private val _state = MutableStateFlow(TransferComposeUiState())
    val state: StateFlow<TransferComposeUiState> = _state.asStateFlow()

    val contacts: List<MockContact> = listOf(
        MockContact("Mum",      "+60 12-987 6543", 50.0,   "AISYAH BINTI HAMID",   TrustLevel.GREEN),
        MockContact("Brother",  "+60 11-234 5678", 100.0,  "AHMAD HAFIZ BIN OMAR", TrustLevel.GREEN),
        MockContact("Landlord", "+60 17-555 1234", 1200.0, "TAN CHEE WEI",         TrustLevel.GREEN),
        MockContact("John",     "+60 13-7842 4421", 2000.0, "MOHD ALIF BIN RAZAK", TrustLevel.RED),
    )

    private var resolveJob: Job? = null

    fun onPhoneChanged(value: String) {
        resolveJob?.cancel()
        _state.update { it.copy(recipientPhone = value).validate() }

        val digits = value.filter { it.isDigit() }
        val isValidMyPhone = digits.startsWith("60") && digits.length >= 10

        if (!isValidMyPhone) {
            _state.update { it.copy(resolvedName = null, trustLevel = TrustLevel.UNKNOWN) }
            return
        }

        _state.update { it.copy(resolvedName = null, trustLevel = TrustLevel.CHECKING) }
        resolveJob = screenModelScope.launch {
            delay(300L)
            val match = contacts.firstOrNull { it.phone.filter { ch -> ch.isDigit() } == digits }
            if (match != null) {
                _state.update { it.copy(resolvedName = match.accountHolder, trustLevel = match.trust) }
            } else {
                _state.update { it.copy(resolvedName = null, trustLevel = TrustLevel.AMBER) }
            }
        }
    }

    fun onAmountChanged(value: String) {
        val sanitized = value.filter { it.isDigit() || it == '.' }
        _state.update { it.copy(amountRm = sanitized).validate() }
    }

    fun onNoteChanged(value: String) {
        _state.update { it.copy(note = value) }
    }

    fun onPurposeSelected(value: String?) {
        _state.update { it.copy(purpose = value) }
    }

    fun selectContact(contact: MockContact) {
        resolveJob?.cancel()
        _state.update {
            it.copy(
                recipientPhone = contact.phone,
                amountRm = contact.suggestedAmount.toLong().toString(),
                resolvedName = contact.accountHolder,
                trustLevel = contact.trust,
            ).validate()
        }
    }

    private fun TransferComposeUiState.validate(): TransferComposeUiState = copy(
        isValid = recipientPhone.isNotBlank() && (amountRm.toDoubleOrNull() ?: 0.0) > 0.0,
    )
}
