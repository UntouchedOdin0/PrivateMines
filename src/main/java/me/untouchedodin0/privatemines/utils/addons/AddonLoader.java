package me.untouchedodin0.privatemines.utils.addons;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public interface AddonLoader {

    final Map<String, Class<?>> classes = new ConcurrentHashMap<String, Class<?>>();
    public Addon load(File file);
    final List<AddonClassLoader> loaders = new CopyOnWriteArrayList<>();

    default void setClass(String name, Class<?> clazz) {
        if (!classes.containsKey(name)) {
            classes.put(name, clazz);
        }
    }

    default Class<?> getClassByName(final String name) {
        Class<?> cachedClass = classes.get(name);

        if (cachedClass != null) {
            return cachedClass;
        } else {
            for (AddonClassLoader loader : loaders) {
                try {
                    cachedClass = loader.findClass(name, false);
                } catch (ClassNotFoundException classNotFoundException) {
                    classNotFoundException.printStackTrace();
                }
                if (cachedClass != null) {
                    return cachedClass;
                }
            }
        }
        return null;
    }
}
