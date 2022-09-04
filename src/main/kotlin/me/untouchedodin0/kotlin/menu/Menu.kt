package me.untouchedodin0.kotlin.menu

import me.untouchedodin0.privatemines.utils.ActionUtils
import me.untouchedodin0.privatemines.utils.Utils
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import redempt.redlib.config.annotations.ConfigMappable
import redempt.redlib.config.annotations.ConfigPath
import redempt.redlib.inventorygui.InventoryGUI
import redempt.redlib.inventorygui.ItemButton
import redempt.redlib.itemutils.ItemBuilder

@ConfigMappable
class Menu {

    @ConfigPath
    val name: String? = null
    private val title: String? = null
    val rows: Int = 1
    var items: Map<String, MenuItem> = LinkedHashMap()

    fun open(player: Player) {
        val slots = rows * 9
        val inventoryGUI = InventoryGUI(slots, Utils.colorBukkit(title))

        items.forEach {
            val material = it.value.material
            val slot = it.value.slot
            val name = it.value.name
            val nameColored = Utils.color(name)
            val lore = it.value.lore
            val action = it.value.action

            val itemButton = ItemButton.create(
                ItemBuilder(material).setName(nameColored).addLore(lore)
            ) { event: InventoryClickEvent? ->
                run {
                    event?.isCancelled = true
                    ActionUtils.handleClick(player, action)
                }
            }
            if (slot != null) {
                inventoryGUI.addButton(slot, itemButton)
            }
        }
        inventoryGUI.open(player)
    }
}
