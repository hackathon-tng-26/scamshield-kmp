package my.scamshield.feature.transfer.domain.model

enum class Verdict {
    GREEN,
    YELLOW,
    RED;

    companion object {
        fun fromScore(score: Int): Verdict = when {
            score < 30 -> GREEN
            score < 70 -> YELLOW
            else -> RED
        }
    }
}
