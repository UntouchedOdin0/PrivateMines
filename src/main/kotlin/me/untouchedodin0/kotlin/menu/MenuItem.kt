package me.untouchedodin0.kotlin.menu

import org.bukkit.Material
import redempt.redlib.config.annotations.ConfigMappable
import redempt.redlib.config.annotations.ConfigPath

@ConfigMappable
class MenuItem {

    @ConfigPath
    val itemName: String? = null
    val material: Material? = null
    val slot: Int? = null
    val name: String? = null
    val lore: List<String>? = null
    val action: String? = null
}