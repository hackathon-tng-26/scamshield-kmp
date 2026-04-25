package my.scamshield.feature.transfer.domain.model

data class Recipient(
    val id: String,
    val displayName: String,
    val phone: String,
    val isInContacts: Boolean = false,
    val priorTransferCount: Int = 0,
    val verifiedName: String? = null,
)
