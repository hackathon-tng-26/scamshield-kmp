package my.scamshield.feature.transfer.domain.model

data class Transaction(
    val id: String,
    val senderId: String,
    val recipient: Recipient,
    val amount: Double,
    val note: String = "",
)
