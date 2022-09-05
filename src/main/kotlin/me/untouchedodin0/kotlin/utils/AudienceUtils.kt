package me.untouchedodin0.kotlin.utils

import me.untouchedodin0.privatemines.PrivateMines
import net.kyori.adventure.text.minimessage.MiniMessage
import org.bukkit.entity.Player

class AudienceUtils {

    var privateMines: PrivateMines = PrivateMines.getPrivateMines()


    fun sendMessage(player: Player, message: String) {
        val miniMessage = MiniMessage.miniMessage()
        val audiences = privateMines.adventure
        val parsed = miniMessage.deserialize("Hello <rainbow>world</rainbow>, isn't <underlined>MiniMessage</underlined> fun?")
        val audience = audiences.player(player)
        audience.sendMessage(parsed)
    }
}