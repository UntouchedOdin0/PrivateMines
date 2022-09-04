package me.untouchedodin0.kotlin.utils

import kotlin.math.ceil

class InventoryUtils {

    fun getSlots(slots: Int): Int {
        if (slots <= 0) return 9 // if equals or less than 0 return 9. (Minimum Size)
        val quotient = ceil(slots / 9.0)
        return if (quotient > 5) 54 else (quotient * 9).toInt()
    }

    companion object {
        fun getSlots(slots: Int): Int {
            if (slots <= 0) return 9 // if equals or less than 0 return 9. (Minimum Size)
            val quotient = ceil(slots / 9.0)
            return if (quotient > 5) 54 else (quotient * 9).toInt()
        }
    }
}