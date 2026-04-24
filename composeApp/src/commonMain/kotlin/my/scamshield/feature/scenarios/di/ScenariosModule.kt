package my.scamshield.feature.scenarios.di

import my.scamshield.feature.scenarios.data.repository.LocalScenariosRepository
import my.scamshield.feature.scenarios.domain.repository.ScenariosRepository
import org.koin.dsl.module

val scenariosModule = module {
    single<ScenariosRepository> { LocalScenariosRepository() }
}
