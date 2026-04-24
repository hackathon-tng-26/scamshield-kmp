package my.scamshield.feature.auth.domain.model

data class DemoUser(
    val id: String,
    val name: String,
    val phone: String,
)

object DemoUsers {
    val WAFI = DemoUser(
        id = "demo_user_01",
        name = "Wafi",
        phone = "+60 12-345 0001",
    )
}
