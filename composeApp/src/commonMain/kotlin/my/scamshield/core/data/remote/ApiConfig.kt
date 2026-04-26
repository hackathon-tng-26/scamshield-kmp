package my.scamshield.core.data.remote

data class ApiConfig(
    val baseUrl: String,
    val timeoutMs: Long = 15_000L,
    val maxRetries: Int = 2,
)

object ApiConfigDefaults {
    const val EMULATOR_HOST_LOOPBACK = "http://10.0.2.2:8000"
    const val LOCALHOST = "http://localhost:8000"
    const val PROD_AWS = "https://cpeudu2ddb.ap-southeast-1.awsapprunner.com"
    const val DEMO_FALLBACK = PROD_AWS
}
