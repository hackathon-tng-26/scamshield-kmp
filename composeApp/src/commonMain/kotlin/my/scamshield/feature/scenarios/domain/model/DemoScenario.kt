package my.scamshield.feature.scenarios.domain.model

data class DemoScenario(
    val id: String,
    val senderId: String,
    val recipientId: String,
    val recipientPhone: String,
    val recipientDisplayName: String,
    val amount: Double,
    val expectedVerdict: String,
    val moment: Int,
)

object DemoScenarios {
    val G1 = DemoScenario(
        id = "G1",
        senderId = "demo_user_01",
        recipientId = "contact_siti",
        recipientPhone = "+60 12-345 6789",
        recipientDisplayName = "Siti Aminah",
        amount = 50.0,
        expectedVerdict = "GREEN",
        moment = 1,
    )
    val R1 = DemoScenario(
        id = "R1",
        senderId = "demo_user_01",
        recipientId = "recipient_mule_01",
        recipientPhone = "+60 11-XXXX 8712",
        recipientDisplayName = "(new recipient)",
        amount = 2_000.0,
        expectedVerdict = "RED",
        moment = 2,
    )
    val ALL = listOf(G1, R1)
}
