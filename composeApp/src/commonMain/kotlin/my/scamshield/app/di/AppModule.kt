package my.scamshield.app.di

import my.scamshield.core.di.coreModule
import my.scamshield.core.di.networkModule
import my.scamshield.feature.auth.di.authModule
import my.scamshield.feature.devicetrust.di.deviceTrustModule
import my.scamshield.feature.home.di.homeModule
import my.scamshield.feature.scenarios.di.scenariosModule
import my.scamshield.feature.transfer.di.transferModule
import org.koin.dsl.module

val appModule = module {
    includes(
        coreModule,
        networkModule,
        authModule,
        homeModule,
        transferModule,
        deviceTrustModule,
        scenariosModule,
    )
}
