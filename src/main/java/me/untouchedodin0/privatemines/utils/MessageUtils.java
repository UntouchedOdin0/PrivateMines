package me.untouchedodin0.privatemines.utils;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.chat.BaseComponent;

public class MessageUtils {

    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public Component deserialize(String string) {
        return miniMessage.deserialize(string);
    }
}
