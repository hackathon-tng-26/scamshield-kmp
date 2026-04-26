package my.scamshield.feature.transfer.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScoreTransferRequestDto(
    @SerialName("sender_id") val senderId: String,
    @SerialName("recipient_id") val recipientId: String,
    @SerialName("recipient_phone") val recipientPhone: String,
    @SerialName("recipient_display_name") val recipientDisplayName: String,
    val amount: Double,
    val note: String = "",
    @SerialName("device_fingerprint") val deviceFingerprint: String,
    @SerialName("timestamp_ms") val timestampMs: Long,
)
