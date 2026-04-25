package my.scamshield.feature.transfer.data.repository

import kotlinx.coroutines.delay
import kotlin.time.Clock
import my.scamshield.feature.transfer.domain.model.Direction
import my.scamshield.feature.transfer.domain.model.FeatureContribution
import my.scamshield.feature.transfer.domain.model.RiskScore
import my.scamshield.feature.transfer.domain.model.Transaction
import my.scamshield.feature.transfer.domain.model.Verdict
import my.scamshield.feature.transfer.domain.repository.TransferRepository

class FakeTransferRepository : TransferRepository {

    override suspend fun scoreTransfer(transaction: Transaction): Result<RiskScore> {
        val start = Clock.System.now().toEpochMilliseconds()
        delay(142L)
        val latency = Clock.System.now().toEpochMilliseconds() - start
        val score = scoreFor(transaction)
        val verdict = Verdict.fromScore(score)
        return Result.success(
            RiskScore(
                score = score,
                verdict = verdict,
                latencyMs = latency,
                attribution = attributionFor(verdict),
                explanationHighlights = explanationFor(verdict, transaction),
            )
        )
    }

    override suspend fun executeTransfer(transaction: Transaction): Result<String> {
        delay(200L)
        return Result.success(generateTxRef())
    }

    private fun generateTxRef(): String {
        val now = Clock.System.now().toEpochMilliseconds()
        val chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"
        fun chunk(seed: Long): String = buildString {
            var s = seed and 0x7FFFFFFFFFFFFFFFL
            repeat(4) {
                append(chars[(s % chars.length).toInt()])
                s /= chars.length
            }
        }
        return "TNG-2026-${chunk(now)}-${chunk(now shr 20)}"
    }

    private fun scoreFor(tx: Transaction): Int {
        val phone = tx.recipient.phone
        return when {
            phone.contains("8712") -> 87
            phone.contains("4001") -> 91
            phone.contains("4421") -> 87
            tx.recipient.isInContacts -> (15..25).random()
            tx.amount >= 1_000.0 -> (55..65).random()
            else -> (18..30).random()
        }
    }

    private fun attributionFor(verdict: Verdict): List<FeatureContribution> = when (verdict) {
        Verdict.GREEN -> listOf(
            FeatureContribution("recipient trusted", 22, Direction.NEGATIVE),
            FeatureContribution("amount in-pattern", 12, Direction.NEGATIVE),
            FeatureContribution("baseline user risk", 10, Direction.POSITIVE),
        )
        Verdict.YELLOW -> listOf(
            FeatureContribution("new recipient", 15, Direction.POSITIVE),
            FeatureContribution("amount above 90th percentile", 18, Direction.POSITIVE),
            FeatureContribution("user risk history", 8, Direction.NEGATIVE),
        )
        Verdict.RED -> listOf(
            FeatureContribution("recipient mule-likelihood (L3)", 40, Direction.POSITIVE),
            FeatureContribution("velocity cluster (19 senders/2h)", 35, Direction.POSITIVE),
            FeatureContribution("amount vs user history", 20, Direction.POSITIVE),
            FeatureContribution("new recipient", 12, Direction.POSITIVE),
            FeatureContribution("time-of-day in-pattern", 8, Direction.NEGATIVE),
            FeatureContribution("user own risk history", 12, Direction.NEGATIVE),
        )
    }

    private fun explanationFor(verdict: Verdict, tx: Transaction): List<String> = when (verdict) {
        Verdict.GREEN -> listOf(
            "Recipient in your contacts",
            "${tx.recipient.priorTransferCount} previous transfers",
            "Amount typical for you",
        )
        Verdict.YELLOW -> listOf(
            "Never sent to this recipient before",
            "Amount above your usual range",
        )
        Verdict.RED -> listOf(
            "7 other people reported this number as a scam this week",
            "This account behaves like a mule — money comes in from many people and leaves within minutes",
            "Account was opened only 3 days ago",
            "19 people sent money to this number in the last 2 hours",
        )
    }
}
