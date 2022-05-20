package me.untouchedodin0.kotlin.mine.storage

import me.untouchedodin0.privatemines.PrivateMines
import me.untouchedodin0.privatemines.mine.Mine
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.Player
import java.util.*

class MineStorage {
    var mines: MutableMap<UUID, Mine> = HashMap()
    var privateMines: PrivateMines = PrivateMines.getPrivateMines()

    fun addMine(uuid: UUID, mine: Mine) {
        if (mines.containsKey(uuid)) {
            privateMines.logger.info(String.format("Player %s already has a mine!!", uuid.toString()))
        } else {
            mines[uuid] = mine
        }
    }

    fun removeMine(uuid: UUID) {
        if (!mines.containsKey(uuid)) {
            privateMines.logger.warning(String.format("Player %s doesn't have a mine!!", uuid.toString()))
        } else {
            mines.remove(uuid)
        }
    }

    fun replaceMine(uuid: UUID, mine: Mine) {
        if (!mines.containsKey(uuid)) {
            privateMines.logger.warning(String.format("Player %s doesn't have a mine!!", uuid.toString()))
        } else {
            mines.replace(uuid, mine)
            privateMines.logger.info(String.format("Successfully replaced %s's mine!", uuid.toString()))
        }
    }

    fun replaceMineNoLog(uuid: UUID, mine: Mine) {
        if (!mines.containsKey(uuid)) {
            privateMines.logger.warning(String.format("Player %s doesn't have a mine!", uuid.toString()))
        } else {
            mines.replace(uuid, mine)
        }
    }

    fun hasMine(uuid: UUID): Boolean {
        return mines.containsKey(uuid)
    }

    operator fun get(uuid: UUID): Mine? {
        return mines[uuid]
    }

    fun getTotalMines(): Int {
        return mines.size
    }

    fun getClosest(player: Player, location: Location): Mine? {
        // Make a distances value and make an empty map for the distances
        val distances: MutableMap<Mine, Double> = HashMap()
        var min: Map.Entry<Mine, Double>? = null

        // Iterate over all the mines calculating the distance
        // from the location then add the mine, and it's distance
        // into the map.

        mines.forEach { (_: UUID?, mine: Mine) ->
            val mineData = mine.mineData
            val mineLocation = mineData.mineLocation
            val distance = location.distance(mineLocation)
            distances.putIfAbsent(mine, distance)
            print("distance: $distance")
        }
        for (entry in distances.entries) {

            if (min == null || min.value > entry.value) {
                min = entry
                if (min.value > 20) {
                    player.sendMessage(ChatColor.RED.toString() + "You're not in any mines!")
                    return null
                }
                privateMines.logger.info("min value: ${min.value}")
                privateMines.logger.info("min key: ${min.key}")
            }
        }
        return min!!.key
    }

    fun getClosest(location: Location): Mine {
        // Make a distances value and make an empty map for the distances
        val distances: MutableMap<Mine, Double> = HashMap()
        var min: Map.Entry<Mine, Double>? = null

        // Iterate over all the mines calculating the distance
        // from the location then add the mine, and it's distance
        // into the map.

        mines.forEach { (_: UUID?, mine: Mine) ->
            val mineData = mine.mineData
            val mineLocation = mineData.mineLocation
            val distance = location.distance(mineLocation)
            distances.putIfAbsent(mine, distance)
        }
        for (entry in distances.entries) {

            if (min == null || min.value > entry.value) {
                min = entry
            }
        }
        return min!!.key
    }
}