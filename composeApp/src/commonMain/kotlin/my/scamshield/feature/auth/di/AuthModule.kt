package my.scamshield.feature.auth.di

import my.scamshield.feature.auth.data.repository.HardcodedSessionRepository
import my.scamshield.feature.auth.domain.repository.SessionRepository
import org.koin.dsl.module

val authModule = module {
    single<SessionRepository> { HardcodedSessionRepository() }
}
