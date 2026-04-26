package my.scamshield.feature.home.di

import my.scamshield.feature.home.data.repository.InMemoryActivityFeedRepository
import my.scamshield.feature.home.domain.repository.ActivityFeedRepository
import my.scamshield.feature.home.presentation.HomeScreenModel
import org.koin.dsl.module

val homeModule = module {
    single<ActivityFeedRepository> { InMemoryActivityFeedRepository(get()) }
    factory { HomeScreenModel(get()) }
}
