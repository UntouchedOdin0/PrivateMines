package me.untouchedodin0.kotlin.mine.type

import me.untouchedodin0.privatemines.configmanager.annotations.ConfigMappable
import me.untouchedodin0.privatemines.configmanager.annotations.ConfigPath
import org.bukkit.Material

@ConfigMappable
class MineType {
    @ConfigPath
    val name: String? = null
    val file: String? = null
    val resetTime: Int = 0
    val resetPercentage: Double = 0.0
    val materials: Map<Material, Double>? = null
}
