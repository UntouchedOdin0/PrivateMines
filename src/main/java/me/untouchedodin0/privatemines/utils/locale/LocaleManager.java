package me.untouchedodin0.privatemines.utils.locale;

import java.io.File;
import java.util.*;

public class LocaleManager {

    public Map<File, LocaleObject> localeObjects = new HashMap<>();
    public Map<UUID, LocaleObject> localeObjectMap = new HashMap<>();
    public void putPlayerLocale(UUID uuid, LocaleObject localeObject) {
        localeObjectMap.put(uuid, localeObject);
    }

    public void removePlayerLocale(UUID uuid) {
        localeObjectMap.remove(uuid);
    }

    public LocaleObject getPlayerLocale(UUID uuid) {
        return localeObjectMap.get(uuid);
    }

    public void addLocaleObject(File file, LocaleObject localeObject) {
        localeObjects.put(file, localeObject);
    }

    public Map<File, LocaleObject> getLocaleObjects() {
        return localeObjects;
    }
}
