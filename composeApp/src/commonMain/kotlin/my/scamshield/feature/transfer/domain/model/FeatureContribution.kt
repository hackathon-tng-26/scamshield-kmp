package my.scamshield.feature.transfer.domain.model

enum class Direction { POSITIVE, NEGATIVE }

data class FeatureContribution(
    val feature: String,
    val contribution: Int,
    val direction: Direction,
)
