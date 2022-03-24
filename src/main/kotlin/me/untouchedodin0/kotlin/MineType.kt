package me.untouchedodin0.kotlin

import me.untouchedodin0.privatemines.configmanager.annotations.ConfigMappable
import me.untouchedodin0.privatemines.configmanager.annotations.ConfigPath
import org.bukkit.Material

@ConfigMappable
class MineType {
    @ConfigPath
    val name: String? = null
    val file: String? = null
    val resetTime = 0
    val resetPercentage = 0.0
    val materials: Map<Material, Double>? = null
}
