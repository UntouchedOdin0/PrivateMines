package me.untouchedodin0.kotlin.utils

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import redempt.redlib.itemutils.ItemBuilder

class BuilderUtils {

    inline fun itemBuilder(material: Material, builderItem: ItemBuilder.() -> Unit): ItemStack? {
        val builder = ItemBuilder(material)
        builder.builderItem()
        return builder.toItemStack()
    }

}