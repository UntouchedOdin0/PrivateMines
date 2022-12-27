package me.untouchedodin0.kotlin.mine.storage

import me.untouchedodin0.privatemines.PrivateMines
import me.untouchedodin0.privatemines.mine.Mine
import me.untouchedodin0.privatemines.utils.world.MineWorldManager
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.entity.Player
import java.util.*

class MineStorage {
    var mines: MutableMap<UUID, Mine> = HashMap()
    var privateMines: PrivateMines = PrivateMines.getPrivateMines()
    var mineWorldManager: MineWorldManager = privateMines.mineWorldManager
    var world: World = mineWorldManager.minesWorld
    private val logger = privateMines.logger

    fun addMine(uuid: UUID, mine: Mine) = mines.computeIfAbsent(uuid) { mine }

    fun removeMine(uuid: UUID) = mines.remove(uuid)
            ?: logger.warning("Player $uuid doesn't have a mine!")

    fun replaceMine(uuid: UUID, mine: Mine) {
        mines.replace(uuid, mine).also {
            logger.info("Successfully replaced $uuid's mine!")
        } ?: logger.warning("Player $uuid doesn't have a mine!")
    }

    fun replaceMineNoLog(uuid: UUID, mine: Mine) = mines.replace(uuid, mine)
            ?: logger.warning("Player $uuid doesn't have a mine!")

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

    fun getClosest(player: Player, location: Location): Mine? {
        // Make a distances value and make an empty map for the distances
        val distances: MutableMap<Mine, Double> = HashMap()

        // Iterate over all the mines calculating the distance
        // from the location then add the mine, and it's distance
        // into the map.

        mines.forEach {
            val mineLocation = it.value.mineData.mineLocation
            val distance = location.distance(mineLocation)
            distances.putIfAbsent(it.value, distance)
        }
        val min = distances.entries.minByOrNull { it.value } ?: return null

        if (min.value > 20) {
            player.sendMessage("${ChatColor.RED}You're not in any mines!")
            return null
        }

        return min.key
    }

    fun getClosest(location: Location): Mine? {
        // Make a distances value and make an empty map for the distances
        val distances: MutableMap<Mine, Double> = HashMap()

        if (location.world != world) return null
        // Iterate over all the mines calculating the distance
        // from the location then add the mine, and it's distance
        // into the map.
        mines.forEach {
            val mineLocation = it.value.mineData.mineLocation
            val distance = location.distance(mineLocation)
            distances.putIfAbsent(it.value, distance)
        }
        return distances.entries.minByOrNull {
            it.value
        }?.key
    }
}