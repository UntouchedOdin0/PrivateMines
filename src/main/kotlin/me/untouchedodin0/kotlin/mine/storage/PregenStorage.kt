/**
 * MIT License
 * <p>
 * Copyright (c) 2021 - 2023 Kyle Hicks
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.untouchedodin0.kotlin.mine.storage

import me.untouchedodin0.kotlin.mine.pregen.PregenMine
import org.bukkit.Bukkit
import org.bukkit.Location
import java.io.File

class PregenStorage {

    private var pregenMines: MutableMap<PregenMine, Location> = HashMap()

    private var files: MutableMap<PregenMine, File> = HashMap()

    fun addMine(pregenMine: PregenMine) {
        pregenMines[pregenMine] = pregenMine.location!!
    }

    fun getMines(): MutableMap<PregenMine, Location> {
        return pregenMines
    }

    fun getAndRemove(): PregenMine {
        val oldestEntry = pregenMines.entries.first()
        oldestEntry.let {
            return oldestEntry.key.also {
                pregenMines.remove(oldestEntry.key)
                Bukkit.broadcastMessage("total left ${pregenMines.keys}")
            }
        }
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