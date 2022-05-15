package me.untouchedodin0.privatemines.messages;

import co.aikar.locales.MessageKey;
import co.aikar.locales.MessageKeyProvider;

public enum LangKeys implements MessageKeyProvider {

    INFO_PRIVATEMINE_GIVEN,
    INFO_PRIVATEMINE_GIVEN_TO,
    INFO_PRIVATEMINE_DELETED,
    INFO_PRIVATEMINE_RESET,
    INFO_PRIVATEMINE_TELEPORTED,
    INFO_PRIVATEMINE_PLAYER_DOESNT_OWN_A_MINE;

    private final MessageKey messageKey = MessageKey.of("privatemines." + name().toLowerCase());

    @Override
    public MessageKey getMessageKey() {
        return messageKey;
    }
}
