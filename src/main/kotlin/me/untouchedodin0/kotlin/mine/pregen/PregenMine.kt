package me.untouchedodin0.kotlin.mine.pregen

import me.untouchedodin0.privatemines.PrivateMines
import me.untouchedodin0.privatemines.utils.Utils
import org.bukkit.Location
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Player
import redempt.redlib.misc.LocationUtils
import java.io.File

class PregenMine {

    var location: Location? = null
    var spawnLocation: Location? = null
    var lowerRails: Location? = null
    var upperRails: Location? = null
    var fullMin: Location? = null
    var fullMax: Location? = null
    var file: File? = null
    var privateMines: PrivateMines = PrivateMines.getPrivateMines()


    fun teleport(player: Player) {
        spawnLocation?.let(player::teleport)
    }

    fun save() {
        file = File("plugins/PrivateMines/pregen/" + Utils.getRandom(10) + ".yml")

        val yml = YamlConfiguration.loadConfiguration(file!!)

        yml.set("location", LocationUtils.toString(location))
        yml.set("spawnLocation", LocationUtils.toString(spawnLocation))
        yml.set("lowerRails", LocationUtils.toString(lowerRails))
        yml.set("upperRails", LocationUtils.toString(upperRails))
        yml.set("fullMin", LocationUtils.toString(fullMin))
        yml.set("fullMax", LocationUtils.toString(fullMax))

        yml.save(file!!)
    }
}