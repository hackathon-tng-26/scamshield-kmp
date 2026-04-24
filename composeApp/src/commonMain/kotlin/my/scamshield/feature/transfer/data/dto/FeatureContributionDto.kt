package my.scamshield.feature.transfer.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class FeatureContributionDto(
    val feature: String,
    val contribution: Int,
    val direction: String,
)
