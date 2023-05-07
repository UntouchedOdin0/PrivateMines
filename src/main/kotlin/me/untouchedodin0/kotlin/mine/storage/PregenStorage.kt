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
import org.bukkit.Location

/**
 * PregenStorage is a class that stores pregenerated mines and their locations.
 * The mines are stored as key-value pairs in a MutableMap, where the key is a PregenMine
 * object and the value is its location.
 */
class PregenStorage {

    // Initialize an empty MutableMap to store the pregenerated mines
    private var pregenMines: MutableMap<PregenMine, Location> = HashMap()

    /**
     * Adds a pregenerated mine to the storage.
     * @param pregenMine The pregenerated mine to add.
     */
    fun addMine(pregenMine: PregenMine) {
        pregenMines[pregenMine] = pregenMine.location!!
    }

    /**
     * Gets and removes the oldest pregenerated mine from the storage.
     * @return The oldest pregenerated mine.
     */
    fun getAndRemove(): PregenMine {
        // Get the oldest entry in the map (which is the first entry)
        val oldestEntry = pregenMines.entries.first()
        // Use a let block to avoid nullable warnings and return the oldest pregenerated mine
        oldestEntry.let {
            return oldestEntry.key.also {
                pregenMines.remove(oldestEntry.key)
            }
        }
    }

    /**
     * Checks if all pregenerated mines in the storage have been redeemed.
     * @return True if there are no more pregenerated mines in the storage, false otherwise.
     */
    fun isAllRedeemed(): Boolean {
        return pregenMines.isEmpty()
    }
}