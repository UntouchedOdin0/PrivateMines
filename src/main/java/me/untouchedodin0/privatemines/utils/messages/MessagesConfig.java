package me.untouchedodin0.privatemines.utils.messages;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class MessagesConfig {
  private Map<String, Map<String, String>> messages = new HashMap<>();

  public void load(File configFile) {
    YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);

    ConfigurationSection messagesSection = config.getConfigurationSection("messages");
    if (messagesSection != null) {
      for (String messageKey : messagesSection.getKeys(false)) {
        ConfigurationSection messageSection = messagesSection.getConfigurationSection(messageKey);
        if (messageSection != null) {
          Map<String, String> languageMap = new HashMap<>();
          for (String language : messageSection.getKeys(false)) {
            String message = messageSection.getString(language);
            languageMap.put(language, message);
          }
          messages.put(messageKey, languageMap);
        }
      }
    }
  }

  public Map<String, String> getMessages(String messageKey) {
    return messages.get(messageKey);
  }
}
