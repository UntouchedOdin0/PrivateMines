package me.untouchedodin0.kotlin.utils


class Range(private val low: Double, private val high: Double) {
    operator fun contains(number: Double): Boolean {
        return number in low..high
    }
}