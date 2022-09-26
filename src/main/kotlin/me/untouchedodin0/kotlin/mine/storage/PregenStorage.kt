package me.untouchedodin0.kotlin.mine.storage

import me.untouchedodin0.kotlin.mine.pregen.PregenMine

class PregenStorage {

    private var pregenMines: MutableList<PregenMine> = ArrayList()

    fun addMine(pregenMine: PregenMine) {
        pregenMines.add(pregenMine)
    }

    fun getMines(): MutableList<PregenMine> {
        return pregenMines
    }
}