package me.untouchedodin0.kotlin.menu

import redempt.redlib.config.annotations.ConfigMappable
import redempt.redlib.config.annotations.ConfigPath

@ConfigMappable
class Menu {

    @ConfigPath
    val name: String? = null
    val rows: Int = 1

    var items: Map<String, MenuItem> = LinkedHashMap()
}
