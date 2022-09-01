package me.untouchedodin0.kotlin.menu

import org.bukkit.Material
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
    val rows: Int = 1

    var items: Map<String, MenuItem> = LinkedHashMap()

    fun open(player: Player) {

        val inventoryGUI = InventoryGUI(9, "gui")

        items.forEach {
            val itemName = it.value.itemName
            val material = it.value.material
            val slot = it.value.slot
            val name = it.value.name
            val lore = it.value.lore
            val action = it.value.action

            player.sendMessage("$itemName")
            player.sendMessage("$material")
            player.sendMessage("$slot")
            player.sendMessage("$name")
            player.sendMessage("$lore")
            player.sendMessage("$action")

            val itemButton = ItemButton.create(
                ItemBuilder(Material.PAPER).setName("Name").setLore("lore")
            ) { event: InventoryClickEvent? ->
                run {
                    player.sendMessage("clicked on itemButton!")
                    event?.isCancelled = true
                }
            }
            if (slot != null) {
                inventoryGUI.addButton(slot, itemButton)
            }
        }
        player.sendMessage("opening the menu for you lol")
        inventoryGUI.open(player)
    }
}
