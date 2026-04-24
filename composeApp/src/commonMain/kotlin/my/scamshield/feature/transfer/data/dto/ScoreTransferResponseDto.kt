package my.scamshield.feature.transfer.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScoreTransferResponseDto(
    @SerialName("transaction_id") val transactionId: String,
    val score: Int,
    val verdict: String,
    val attribution: List<FeatureContributionDto> = emptyList(),
    @SerialName("latency_ms") val latencyMs: Long,
    @SerialName("explanation_highlights") val explanationHighlights: List<String> = emptyList(),
)

@Serializable
data class ExecuteTransferResponseDto(
    val success: Boolean,
    @SerialName("transaction_id") val transactionId: String,
)
