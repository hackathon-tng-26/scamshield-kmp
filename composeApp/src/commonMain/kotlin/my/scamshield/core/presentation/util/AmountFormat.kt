package my.scamshield.core.presentation.util

import kotlin.math.roundToLong

fun Double.toRmAmount(): String {
    val cents = (this * 100).roundToLong()
    val whole = cents / 100
    val frac = (cents % 100).toString().padStart(2, '0')
    val grouped = whole.toString()
        .reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()
    return "$grouped.$frac"
}
