package me.untouchedodin0.privatemines.utils.messages;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public enum Messages {
  TEST("test"),
  WELCOME("welcome"),
  GOODBYE("goodbye");

  private static Map<String, Messages> messageMap = new HashMap<>();
  private String messageKey;
  private Map<UUID, Map<String, String>> languageMap;

  static {
    File configFile = new File("plugins/PrivateMines/messages.yml");
    MessagesConfig config = new MessagesConfig();
    config.load(configFile);

    for (Messages message : Messages.values()) {
      Map<UUID, Map<String, String>> languageMap = new HashMap<>();
      Map<String, String> defaultLanguageMap = config.getMessages(message.getMessageKey());
      if (defaultLanguageMap != null) {
        for (Player player : Bukkit.getOnlinePlayers()) {
          UUID uuid = player.getUniqueId();
          Map<String, String> playerLanguageMap = new HashMap<>(defaultLanguageMap);
          Map<String, String> languageOverrides = config.getMessages(message.getMessageKey());
          if (languageOverrides != null) {
            playerLanguageMap.putAll(languageOverrides);

            System.out.println("Language Overrides for player " + player.getName() + ": " + languageOverrides);

          }
          languageMap.put(uuid, playerLanguageMap);
        }
      }
      message.languageMap = languageMap;
      messageMap.put(message.getMessageKey(), message);
    }
  }

  Messages(String messageKey) {
    this.messageKey = messageKey;
  }

  public String getMessageKey() {
    return messageKey;
  }

  public String getMessage(Player player) {
    Map<String, String> playerLanguageMap = languageMap.get(player.getUniqueId());
    if (playerLanguageMap != null) {
      String message = playerLanguageMap.get(player.getUniqueId().toString());
      if (message != null) {
        message = ChatColor.translateAlternateColorCodes('&', message);
        message = message.replace("%player%", player.getName());
        return message;
      }
    }
    return "";
  }

  public static Messages getMessage(String messageKey) {
    return messageMap.get(messageKey);
  }
}