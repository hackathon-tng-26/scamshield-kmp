package my.scamshield.feature.transfer.data.repository

import my.scamshield.core.domain.util.AppClock
import my.scamshield.core.domain.util.Logger
import my.scamshield.feature.transfer.data.dto.ScoreTransferRequestDto
import my.scamshield.feature.transfer.data.mapper.toDomain
import my.scamshield.feature.transfer.data.remote.TransferRemoteDataSource
import my.scamshield.feature.transfer.domain.model.RiskScore
import my.scamshield.feature.transfer.domain.model.Transaction
import my.scamshield.feature.transfer.domain.repository.TransferRepository

class TransferRepositoryImpl(
    private val remote: TransferRemoteDataSource,
    private val clock: AppClock,
    private val logger: Logger,
) : TransferRepository {

    override suspend fun scoreTransfer(transaction: Transaction): Result<RiskScore> = runCatching {
        val req = ScoreTransferRequestDto(
            senderId = transaction.senderId,
            recipientId = transaction.recipient.id,
            recipientPhone = transaction.recipient.phone,
            amount = transaction.amount,
            deviceFingerprint = DEMO_DEVICE_FINGERPRINT,
            timestampMs = clock.currentTimeMillis(),
        )
        logger.info("Scoring transfer: ${transaction.amount} → ${transaction.recipient.phone}", TAG)
        remote.scoreTransfer(req).toDomain()
    }.onFailure { logger.error("Score failed: ${it.message}", TAG, it) }

    override suspend fun executeTransfer(transaction: Transaction): Result<String> = runCatching {
        val req = ScoreTransferRequestDto(
            senderId = transaction.senderId,
            recipientId = transaction.recipient.id,
            recipientPhone = transaction.recipient.phone,
            amount = transaction.amount,
            deviceFingerprint = DEMO_DEVICE_FINGERPRINT,
            timestampMs = clock.currentTimeMillis(),
        )
        remote.executeTransfer(req).transactionId
    }.onFailure { logger.error("Execute failed: ${it.message}", TAG, it) }

    companion object {
        private const val TAG = "TransferRepo"
        private const val DEMO_DEVICE_FINGERPRINT = "demo-device-01"
    }
}
