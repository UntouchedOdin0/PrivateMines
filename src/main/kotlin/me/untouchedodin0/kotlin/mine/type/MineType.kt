package me.untouchedodin0.kotlin.mine.type

import org.bukkit.Material
import redempt.redlib.config.annotations.ConfigMappable
import redempt.redlib.config.annotations.ConfigPath

@ConfigMappable
class MineType {

    @ConfigPath
    val name: String? = null
    val file: String? = null
    val resetTime: Int = 0
    val expand: Int = 0
    val resetPercentage: Double = 0.0
    val upgradeCost: Double = 0.0
    val upgradeCurrency: String? = null
    val materials: Map<Material, Double>? = null
    val maxPlayers: Int = 0
    val maxMineSize: Int = 0
    val flags: Map<String, Boolean>? = null
    val fullFlags: Map<String, Boolean>? = null
}
