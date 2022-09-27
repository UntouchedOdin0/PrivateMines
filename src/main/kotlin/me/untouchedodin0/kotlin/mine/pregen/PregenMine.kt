package me.untouchedodin0.kotlin.mine.pregen

import org.bukkit.Location
import org.bukkit.entity.Player

class PregenMine {

    var spawnLocation: Location? = null
    var lowerRails: Location? = null
    var upperRails: Location? = null

    fun teleport(player: Player) {
        spawnLocation?.let {
            player.teleport(spawnLocation!!)
        }
    }
}