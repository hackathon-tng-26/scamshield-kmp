package my.scamshield.feature.transfer.presentation.compose

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TransferComposeViewModel : ScreenModel {
    private val _state = MutableStateFlow(TransferComposeUiState())
    val state: StateFlow<TransferComposeUiState> = _state.asStateFlow()

    fun onPhoneChanged(value: String) {
        _state.update { it.copy(recipientPhone = value).validate() }
    }

    fun onDisplayNameChanged(value: String) {
        _state.update { it.copy(recipientDisplayName = value).validate() }
    }

    fun onAmountChanged(value: String) {
        val sanitized = value.filter { it.isDigit() || it == '.' }
        _state.update { it.copy(amountRm = sanitized).validate() }
    }

    fun onNoteChanged(value: String) {
        _state.update { it.copy(note = value) }
    }

    fun selectRehearsedContact() {
        _state.update {
            TransferComposeUiState(
                recipientPhone = "+60 12-345 6789",
                recipientDisplayName = "Siti Aminah",
                amountRm = "50",
                note = "Dinner share",
            ).validate()
        }
    }

    fun selectRehearsedMule() {
        _state.update {
            TransferComposeUiState(
                recipientPhone = "+60 11-XXXX 8712",
                recipientDisplayName = "(new recipient)",
                amountRm = "2000",
                note = "Urgent family transfer",
            ).validate()
        }
    }

    private fun TransferComposeUiState.validate(): TransferComposeUiState = copy(
        isValid = recipientPhone.isNotBlank() && (amountRm.toDoubleOrNull() ?: 0.0) > 0.0,
    )
}
