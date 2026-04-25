package my.scamshield.feature.transfer.presentation.compose

enum class TrustLevel {
    UNKNOWN,
    CHECKING,
    GREEN,
    AMBER,
    RED,
}

data class TransferComposeUiState(
    val recipientPhone: String = "",
    val resolvedName: String? = null,
    val trustLevel: TrustLevel = TrustLevel.UNKNOWN,
    val amountRm: String = "",
    val purpose: String? = null,
    val note: String = "",
    val isValid: Boolean = false,
)
