package my.scamshield.feature.scenarios.domain.repository

import my.scamshield.feature.scenarios.domain.model.DemoScenario

interface ScenariosRepository {
    suspend fun getScenarios(): List<DemoScenario>
}
