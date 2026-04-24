package my.scamshield.feature.transfer.di

import my.scamshield.feature.transfer.data.remote.TransferRemoteDataSource
import my.scamshield.feature.transfer.data.repository.FakeTransferRepository
import my.scamshield.feature.transfer.data.repository.TransferRepositoryImpl
import my.scamshield.feature.transfer.domain.repository.TransferRepository
import my.scamshield.feature.transfer.domain.usecase.ExecuteTransferUseCase
import my.scamshield.feature.transfer.domain.usecase.ScoreTransferUseCase
import my.scamshield.feature.transfer.presentation.compose.TransferComposeViewModel
import my.scamshield.feature.transfer.presentation.confirm.TransferConfirmViewModel
import org.koin.dsl.module

const val USE_FAKE_TRANSFER_REPO = true

val transferModule = module {
    single { TransferRemoteDataSource(get()) }

    single<TransferRepository> {
        if (USE_FAKE_TRANSFER_REPO) {
            FakeTransferRepository()
        } else {
            TransferRepositoryImpl(get(), get(), get())
        }
    }

    factory { ScoreTransferUseCase(get()) }
    factory { ExecuteTransferUseCase(get()) }

    factory { TransferComposeViewModel() }
    factory { TransferConfirmViewModel(get(), get(), get()) }
}
