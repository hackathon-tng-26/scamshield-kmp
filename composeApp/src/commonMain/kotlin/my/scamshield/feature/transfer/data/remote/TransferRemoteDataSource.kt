package my.scamshield.feature.transfer.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import my.scamshield.feature.transfer.data.dto.ExecuteTransferResponseDto
import my.scamshield.feature.transfer.data.dto.ScoreTransferRequestDto
import my.scamshield.feature.transfer.data.dto.ScoreTransferResponseDto

class TransferRemoteDataSource(
    private val client: HttpClient,
) {
    suspend fun scoreTransfer(req: ScoreTransferRequestDto): ScoreTransferResponseDto =
        client.post("/transfer/score") { setBody(req) }.body()

    suspend fun executeTransfer(req: ScoreTransferRequestDto): ExecuteTransferResponseDto =
        client.post("/transfer/execute") { setBody(req) }.body()
}
