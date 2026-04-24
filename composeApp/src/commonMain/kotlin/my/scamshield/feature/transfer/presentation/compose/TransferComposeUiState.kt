package my.scamshield.feature.transfer.presentation.compose

data class TransferComposeUiState(
    val recipientPhone: String = "",
    val recipientDisplayName: String = "",
    val amountRm: String = "",
    val note: String = "",
    val isValid: Boolean = false,
)
