package me.untouchedodin0.kotlin.utils

import me.clip.placeholderapi.PlaceholderAPI
import me.untouchedodin0.privatemines.PrivateMines
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player

class AudienceUtils {

    var privateMines: PrivateMines = PrivateMines.getPrivateMines()

    fun sendMessage(player: Player, message: String) {

        if (privateMines.adventure != null) {
            val miniMessage = MiniMessage.miniMessage()
            val audiences = privateMines.adventure
            val placeholderAPI = PlaceholderAPI.setPlaceholders(player, message)
            val parsed = miniMessage.deserialize(placeholderAPI)
            val audience = audiences.player(player)

            audience.sendMessage(parsed)
        } else {
            val placeholderAPI = PlaceholderAPI.setPlaceholders(player, message)
            player.sendMessage(placeholderAPI)
        }
    }

    fun sendMessage(player: Player, target: Player, message: String) {

        if (privateMines.adventure != null) {
            val miniMessage = MiniMessage.miniMessage()
            val audiences = privateMines.adventure
            val placeholderAPI = PlaceholderAPI.setPlaceholders(target, message)
            val parsed = miniMessage.deserialize(placeholderAPI)
            val audience = audiences.player(player)
            audience.sendMessage(parsed)
        } else {
            val placeholderAPI = PlaceholderAPI.setPlaceholders(target, message)
            player.sendMessage(placeholderAPI)
        }
    }

    fun sendMessage(player: Player, target: OfflinePlayer, message: String) {

        if (privateMines.adventure != null) {
            val miniMessage = MiniMessage.miniMessage()
            val audiences = privateMines.adventure
            val placeholderAPI = PlaceholderAPI.setPlaceholders(target, message.replace("{name}", target.name!!))
            val parsed = miniMessage.deserialize(placeholderAPI)
            val audience = audiences.player(player)
            audience.sendMessage(parsed)
        } else {
            val placeholderAPI = PlaceholderAPI.setPlaceholders(target, message.replace("{name}", target.name!!))
            player.sendMessage(placeholderAPI)
        }
    }

    fun sendMessage(player: Player, message: String, int: Int) {
        if (privateMines.adventure != null) {
            val miniMessage = MiniMessage.miniMessage()
            val audiences = privateMines.adventure
            val parsed = miniMessage.deserialize(message.replace("{amount}", int.toString()))
            val audience = audiences.player(player)
            audience.sendMessage(parsed)
        } else {
            val placeholderAPI = PlaceholderAPI.setPlaceholders(player, message.replace("{amount}", int.toString()))
            player.sendMessage(placeholderAPI)
        }
    }
}