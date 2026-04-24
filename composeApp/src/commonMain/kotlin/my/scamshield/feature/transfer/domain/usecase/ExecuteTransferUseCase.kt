package my.scamshield.feature.transfer.domain.usecase

import my.scamshield.feature.transfer.domain.model.Transaction
import my.scamshield.feature.transfer.domain.repository.TransferRepository

class ExecuteTransferUseCase(
    private val repository: TransferRepository,
) {
    suspend operator fun invoke(transaction: Transaction): Result<String> =
        repository.executeTransfer(transaction)
}
