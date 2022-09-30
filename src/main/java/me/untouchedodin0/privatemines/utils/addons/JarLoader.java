package me.untouchedodin0.privatemines.utils.addons;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarLoader {

    public AddonDescriptionFile getAddonDescription(File file) {

        JarFile jarFile = null;
        InputStream stream = null;

        try {
            jarFile = new JarFile(file);
            JarEntry jarEntry = jarFile.getJarEntry("addon.yml");

            if (jarEntry == null) {
                throw new RuntimeException("Jar file does not contain addon.yml");
            }

            stream = jarFile.getInputStream(jarEntry);
            return new AddonDescriptionFile(stream);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
