package me.untouchedodin0.privatemines.utils.addons;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarLoader {

    public AddonDescriptionFile getAddonDescription(File file) {

        JarFile jarFile;
        InputStream stream;

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

    private static Method[] getAccessibleMethods(Class<?> clazz) {
        List<Method> result = new ArrayList<>();

        while (clazz != null) {
            for (Method method : clazz.getDeclaredMethods()) {
                int modifiers = method.getModifiers();
                if (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers)) {
                    result.add(method);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return result.toArray(new Method[result.size()]);
    }
}
