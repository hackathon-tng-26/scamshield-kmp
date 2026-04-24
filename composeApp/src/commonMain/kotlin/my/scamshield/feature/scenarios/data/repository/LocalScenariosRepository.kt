package my.scamshield.feature.scenarios.data.repository

import my.scamshield.feature.scenarios.domain.model.DemoScenario
import my.scamshield.feature.scenarios.domain.model.DemoScenarios
import my.scamshield.feature.scenarios.domain.repository.ScenariosRepository

class LocalScenariosRepository : ScenariosRepository {
    override suspend fun getScenarios(): List<DemoScenario> = DemoScenarios.ALL
}
