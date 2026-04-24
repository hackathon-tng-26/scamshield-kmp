package my.scamshield.feature.transfer.domain.repository

import my.scamshield.feature.transfer.domain.model.RiskScore
import my.scamshield.feature.transfer.domain.model.Transaction

interface TransferRepository {
    suspend fun scoreTransfer(transaction: Transaction): Result<RiskScore>
    suspend fun executeTransfer(transaction: Transaction): Result<String>
}
