package me.untouchedodin0.kotlin.mine.storage

import me.untouchedodin0.kotlin.mine.pregen.PregenMine
import org.bukkit.Bukkit
import java.io.File

class PregenStorage {

    private var pregenMines: MutableList<PregenMine> = ArrayList()
    private var files: MutableMap<PregenMine, File> = HashMap()

    fun addMine(pregenMine: PregenMine) {
        pregenMines.add(pregenMine)
    }

    fun getMines(): MutableList<PregenMine> {
        return pregenMines
    }

    fun getAndRemove(): PregenMine? {
        return pregenMines.removeFirstOrNull()
    }

    fun isAllRedeemed(): Boolean {
        return pregenMines.isEmpty()
    }

    fun addFile(pregenMine: PregenMine, file: File) {
        files[pregenMine] = file
    }

    fun removeFile(pregenMine: PregenMine) {
        Bukkit.broadcastMessage("File ${pregenMine.file?.name}")
        pregenMine.file?.delete()
        files.keys.removeIf { it.equals(pregenMine.file) }
    }
}