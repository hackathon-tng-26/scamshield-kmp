package my.scamshield.feature.transfer.data.repository

import my.scamshield.core.domain.util.AppClock
import my.scamshield.core.domain.util.Logger
import my.scamshield.feature.transfer.data.dto.ScoreTransferRequestDto
import my.scamshield.feature.transfer.data.mapper.toDomain
import my.scamshield.feature.transfer.data.remote.TransferRemoteDataSource
import my.scamshield.feature.transfer.di.DEMO_FALLBACK_ON_SCORE_FAILURE
import my.scamshield.feature.transfer.domain.model.RiskScore
import my.scamshield.feature.transfer.domain.model.Transaction
import my.scamshield.feature.transfer.domain.repository.TransferRepository

class TransferRepositoryImpl(
    private val remote: TransferRemoteDataSource,
    private val clock: AppClock,
    private val logger: Logger,
    private val scoreFallback: TransferRepository,
) : TransferRepository {

    override suspend fun scoreTransfer(transaction: Transaction): Result<RiskScore> {
        val realResult = runCatching {
            val req = transaction.toRequestDto()
            logger.info("Scoring transfer: ${transaction.amount} → ${transaction.recipient.phone}", TAG)
            remote.scoreTransfer(req).toDomain()
        }
        return realResult.fold(
            onSuccess = { Result.success(it) },
            onFailure = { error ->
                logger.error("Score failed: ${error.message}", TAG, error)
                if (DEMO_FALLBACK_ON_SCORE_FAILURE) {
                    logger.info("Engaging score fallback for ${transaction.recipient.phone}", TAG)
                    scoreFallback.scoreTransfer(transaction)
                } else {
                    Result.failure(error)
                }
            }
        )
    }

    override suspend fun executeTransfer(transaction: Transaction): Result<String> = runCatching {
        remote.executeTransfer(transaction.toRequestDto()).transactionId
    }.onFailure { logger.error("Execute failed: ${it.message}", TAG, it) }

    private fun Transaction.toRequestDto(): ScoreTransferRequestDto = ScoreTransferRequestDto(
        senderId = senderId,
        recipientId = recipient.id,
        recipientPhone = recipient.phone,
        recipientDisplayName = recipient.displayName,
        amount = amount,
        note = note,
        deviceFingerprint = DEMO_DEVICE_FINGERPRINT,
        timestampMs = clock.currentTimeMillis(),
    )

    companion object {
        private const val TAG = "TransferRepo"
        private const val DEMO_DEVICE_FINGERPRINT = "trusted-samsung-s21"
    }
}
