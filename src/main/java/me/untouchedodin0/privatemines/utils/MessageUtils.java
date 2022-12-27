package me.untouchedodin0.privatemines.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class MessageUtils {

  private final static MiniMessage miniMessage = MiniMessage.miniMessage();

  public static Component deserialize(String string) {
    return miniMessage.deserialize(string);
  }
}
