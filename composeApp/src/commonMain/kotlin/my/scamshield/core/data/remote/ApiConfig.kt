package my.scamshield.core.data.remote

data class ApiConfig(
    val baseUrl: String,
    val timeoutMs: Long = 5_000L,
    val maxRetries: Int = 2,
)

object ApiConfigDefaults {
    const val EMULATOR_HOST_LOOPBACK = "http://10.0.2.2:8000"
    const val LOCALHOST = "http://localhost:8000"
    const val DEMO_FALLBACK = EMULATOR_HOST_LOOPBACK
}
