package my.scamshield.feature.transfer.data.mapper

import my.scamshield.feature.transfer.data.dto.FeatureContributionDto
import my.scamshield.feature.transfer.data.dto.ScoreTransferResponseDto
import my.scamshield.feature.transfer.domain.model.Direction
import my.scamshield.feature.transfer.domain.model.FeatureContribution
import my.scamshield.feature.transfer.domain.model.RiskScore
import my.scamshield.feature.transfer.domain.model.Verdict

fun ScoreTransferResponseDto.toDomain(): RiskScore = RiskScore(
    score = score,
    verdict = runCatching { Verdict.valueOf(verdict.uppercase()) }
        .getOrDefault(Verdict.fromScore(score)),
    attribution = attribution.map { it.toDomain() },
    latencyMs = latencyMs,
    explanationHighlights = explanationHighlights,
)

fun FeatureContributionDto.toDomain(): FeatureContribution = FeatureContribution(
    feature = feature,
    contribution = contribution,
    direction = when (direction.lowercase()) {
        "negative" -> Direction.NEGATIVE
        else -> Direction.POSITIVE
    },
)
