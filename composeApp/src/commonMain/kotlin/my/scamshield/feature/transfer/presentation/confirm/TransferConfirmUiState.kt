package my.scamshield.feature.transfer.presentation.confirm

import my.scamshield.feature.transfer.domain.model.RiskScore
import my.scamshield.feature.transfer.domain.model.Transaction

data class TransferConfirmUiState(
    val transaction: Transaction? = null,
    val score: RiskScore? = null,
    val isScoring: Boolean = false,
    val isSending: Boolean = false,
    val errorMessage: String? = null,
)
