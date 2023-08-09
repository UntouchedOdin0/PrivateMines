/**
 * MIT License
 * <p>
 * Copyright (c) 2021 - 2023 Kyle Hicks
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.untouchedodin0.kotlin.utils

import me.clip.placeholderapi.PlaceholderAPI
import me.untouchedodin0.privatemines.PrivateMines
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.OfflinePlayer
import org.bukkit.command.CommandSender
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

    fun sendMessage(commandSender: CommandSender, message: String) {
        if (privateMines.adventure != null) {
            val miniMessage = MiniMessage.miniMessage()
            val audiences = privateMines.adventure
            val parsed  = miniMessage.deserialize(message)
            val audience = audiences.sender(commandSender)
            audience.sendMessage(parsed)
        } else {
            commandSender.sendMessage(message)
        }
    }

    fun sendMessage(player: Player, target: Player, message: String) {

        if (privateMines.adventure != null) {
            val miniMessage = MiniMessage.miniMessage()
            val audiences = privateMines.adventure
            val placeholderAPI = PlaceholderAPI.setPlaceholders(target, message.replace("{name}", target.name))
            val parsed = miniMessage.deserialize(placeholderAPI)
            val audience = audiences.player(player)
            audience.sendMessage(parsed)
        } else {
            val placeholderAPI = PlaceholderAPI.setPlaceholders(target, message.replace("{name}", target.name))
            player.sendMessage(placeholderAPI)
        }
    }

    fun sendMessage(player: Player, target: OfflinePlayer, message: String) {
        if (privateMines.adventure != null) {
            val miniMessage = MiniMessage.miniMessage()
            val audiences = privateMines.adventure
            val placeholderAPI =
                PlaceholderAPI.setPlaceholders(target, message.replace("{name}", target.name!!))
            val parsed = miniMessage.deserialize(placeholderAPI)
            val audience = audiences.player(player)
            audience.sendMessage(parsed)
        } else {
            val placeholderAPI =
                PlaceholderAPI.setPlaceholders(target, message.replace("{name}", target.name!!))
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
            val placeholderAPI =
                PlaceholderAPI.setPlaceholders(player, message.replace("{amount}", int.toString()))
            player.sendMessage(placeholderAPI)
        }
    }

    fun sendMessage(player: Player, target: OfflinePlayer, message: String, int: Int) {
        if (privateMines.adventure != null) {
            val miniMessage = MiniMessage.miniMessage()
            val audiences = privateMines.adventure
            val parsed = miniMessage.deserialize(
                message.replace("{target}", target.name!!).replace("{amount}", int.toString())
            )
            val audience = audiences.player(player)
            audience.sendMessage(parsed)
        } else {
            val placeholderAPI = PlaceholderAPI.setPlaceholders(
                player,
                message.replace("{target}", target.name!!).replace("{amount}", int.toString())
            )
            player.sendMessage(placeholderAPI)
        }
    }

    fun sendMessage(player: Player, message: String, double: Double) {
        if (privateMines.adventure != null) {
            val miniMessage = MiniMessage.miniMessage()
            val audiences = privateMines.adventure
            val parsed = miniMessage.deserialize(message.replace("{amount}", double.toString()))
            val audience = audiences.player(player)
            audience.sendMessage(parsed)
        } else {
            val placeholderAPI =
                PlaceholderAPI.setPlaceholders(player, message.replace("{amount}", double.toString()))
            player.sendMessage(placeholderAPI)
        }
    }
}