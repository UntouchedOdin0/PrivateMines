package me.untouchedodin0.kotlin.menu

import me.untouchedodin0.kotlin.utils.BuilderUtils
import me.untouchedodin0.privatemines.PrivateMines
import me.untouchedodin0.privatemines.utils.ActionUtils
import me.untouchedodin0.privatemines.utils.Utils
import org.bukkit.Bukkit
import org.bukkit.ChatColor
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
    private val title: String? = null
    val rows: Int = 1
    var items: Map<String, MenuItem> = LinkedHashMap()
    var privateMines: PrivateMines = PrivateMines.getPrivateMines()

    fun open(player: Player) {
        val slots = rows * 9
        val inventoryGUI = InventoryGUI(slots, Utils.colorBukkit(title))
        val mineStorage = privateMines.mineStorage
        var buttonSlot = 0

        if (name.equals("publicMines", true)) {
            mineStorage.mines.forEach { (uuid, mine) ->
                run {
                    val name = "${Bukkit.getOfflinePlayer(uuid).name}'s Mine"
                    val tax = mine.mineData.tax

                    val itemButton = ItemButton.create(BuilderUtils().itemBuilder(Material.BEACON) {
                        setName(name)
                        addLore("${ChatColor.GRAY}Click to teleport")
                        addLore("${ChatColor.GRAY}Tax $tax%")
                    }) { event: InventoryClickEvent? ->
                        run {
                            event?.isCancelled = true
                            mine.teleport(player)
                        }
                    }
                    inventoryGUI.addButton(buttonSlot++, itemButton)
                }
            }
            inventoryGUI.open(player)
        } else {
            items.forEach {
                val material = it.value.material
                val slot = it.value.slot
                val name = it.value.name
                val nameColored = Utils.color(name)
                val lore = it.value.lore
                val loreColored = Utils.color(lore)
                val action = it.value.action

                val itemButton = ItemButton.create(ItemBuilder(material).setName(nameColored).addLore(loreColored)) { event: InventoryClickEvent? ->
                    run {
                        event?.isCancelled = true
                        ActionUtils.handleClick(player, action)
                    }
                }
                slot?.let {
                    inventoryGUI.addButton(slot, itemButton)
                }
            }
            inventoryGUI.open(player)
        }
    }
}
