package me.untouchedodin0.privatemines.utils.addon;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddonLoader {

    public static List<LoadingClass> loadAll(File directory) {
        if (!directory.isDirectory()) {
            return Collections.emptyList();
        }
        if (!directory.exists()) {
            return Collections.emptyList();
        }

        List<LoadingClass> loaded = new ArrayList<>();
        JarLoader jarLoader = new JarLoader();
        for(File file : directory.listFiles((file, name) -> name.endsWith(".jar"))) {
            loaded.add(jarLoader.load(file, LoadingClass.class));
        }
        return loaded;
    }
}
