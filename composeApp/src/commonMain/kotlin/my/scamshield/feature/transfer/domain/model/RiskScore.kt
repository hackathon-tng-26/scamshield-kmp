package my.scamshield.feature.transfer.domain.model

data class RiskScore(
    val score: Int,
    val verdict: Verdict,
    val attribution: List<FeatureContribution>,
    val latencyMs: Long,
    val explanationHighlights: List<String> = emptyList(),
)
