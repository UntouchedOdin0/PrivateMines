package me.untouchedodin0.privatemines.utils.locale;

import redempt.redlib.commandmanager.Messages;

public class LocaleObject {

    String locale;
    Messages messages;

    public LocaleObject(String locale, Messages messages) {
        this.locale = locale;
        this.messages = messages;
    }

    public String getLocale() {
        return locale;
    }

    public Messages getMessages() {
        return messages;
    }
}
