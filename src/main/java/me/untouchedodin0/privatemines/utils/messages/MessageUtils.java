package me.untouchedodin0.privatemines.utils.messages;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class MessageUtils {

  public static void send(Player player, String message) {
    player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
  }
}