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

import me.untouchedodin0.privatemines.PrivateMines
import me.untouchedodin0.privatemines.mine.Mine
import me.untouchedodin0.privatemines.utils.world.MineWorldManager
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.*

class MineStorage {
    var mines: MutableMap<UUID, Mine> = HashMap()
    var privateMines: PrivateMines = PrivateMines.getPrivateMines()
    private var mineWorldManager: MineWorldManager = privateMines.mineWorldManager
    var world: World = mineWorldManager.minesWorld
    private val logger = privateMines.logger

    fun addMine(uuid: UUID, mine: Mine) = mines.computeIfAbsent(uuid) { mine }

    fun removeMine(uuid: UUID) =
        mines.remove(uuid) ?: logger.warning("Player $uuid doesn't have a mine!")

    fun replaceMine(uuid: UUID, mine: Mine) {
        mines.replace(uuid, mine).also {
            logger.info("Successfully replaced $uuid's mine!")
        } ?: logger.warning("Player $uuid doesn't have a mine!")
    }

    fun replaceMineNoLog(uuid: UUID, mine: Mine) =
        mines.replace(uuid, mine) ?: logger.warning("Player $uuid doesn't have a mine!")

    fun replaceMineNoLog(player: Player, mine: Mine) {
        mines.replace(player.uniqueId, mine)
    }

    fun hasMine(uuid: UUID): Boolean {
        return mines.containsKey(uuid)
    }

    fun hasMine(player: Player): Boolean {
        return hasMine(player.uniqueId)
    }

    operator fun get(uuid: UUID): Mine? {
        return mines[uuid]
    }

    operator fun get(player: Player): Mine? {
        return get(player.uniqueId)
    }

    val totalMines
        get() = mines.size

    fun getClosest(location: Location): Mine? {
        // Make a distances value and make an empty map for the distances
        val distances: MutableMap<Mine, Double> = HashMap()

        if (location.world != world) return null
        // Iterate over all the mines calculating the distance
        // from the location then add the mine, and it's distance
        // into the map.

        mines.forEach { (uuid, mine) ->
            run {
                val mineLocation = mine.mineData.mineLocation
                val distance = location.distance(mineLocation)
                distances.putIfAbsent(mine, distance)
            }
        }

        return distances.entries.minByOrNull {
            it.value
        }?.key
    }
}