package my.scamshield.feature.transfer.presentation.confirm

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import my.scamshield.core.domain.util.Logger
import my.scamshield.feature.transfer.domain.model.Transaction
import my.scamshield.feature.transfer.domain.usecase.ExecuteTransferUseCase
import my.scamshield.feature.transfer.domain.usecase.ScoreTransferUseCase

class TransferConfirmViewModel(
    private val scoreTransferUseCase: ScoreTransferUseCase,
    private val executeTransferUseCase: ExecuteTransferUseCase,
    private val logger: Logger,
) : ScreenModel {

    private val _state = MutableStateFlow(TransferConfirmUiState())
    val state: StateFlow<TransferConfirmUiState> = _state.asStateFlow()

    fun load(transaction: Transaction) {
        if (_state.value.transaction == transaction && _state.value.score != null) return
        _state.update { it.copy(transaction = transaction, isScoring = true, errorMessage = null) }
        screenModelScope.launch {
            scoreTransferUseCase(transaction)
                .onSuccess { score ->
                    logger.info("Scored ${score.score} verdict=${score.verdict}", TAG)
                    _state.update { it.copy(score = score, isScoring = false) }
                }
                .onFailure { error ->
                    logger.error("Score failed: ${error.message}", TAG, error)
                    _state.update { it.copy(isScoring = false, errorMessage = error.message) }
                }
        }
    }

    fun confirmSend(onSent: (String) -> Unit) {
        val tx = _state.value.transaction ?: return
        _state.update { it.copy(isSending = true) }
        screenModelScope.launch {
            executeTransferUseCase(tx)
                .onSuccess { txId ->
                    _state.update { it.copy(isSending = false) }
                    onSent(txId)
                }
                .onFailure { error ->
                    _state.update { it.copy(isSending = false, errorMessage = error.message) }
                }
        }
    }

    companion object {
        private const val TAG = "ConfirmVM"
    }
}
