package me.untouchedodin0.privatemines.utils.addon.old;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;
import me.untouchedodin0.privatemines.PrivateMines;

public class AddonsManager {

  private final Map<String, Class<?>> classes;

  public AddonsManager() {
    classes = new HashMap<>();
  }

  public Map<String, Addon2> addons = new HashMap<>();

  public Map<String, Addon2> getAddons() {
    return addons;
  }

  public void addAddon(String string, Addon2 addon) {
    addons.putIfAbsent(string, addon);
  }

  public Addon2 getAddon(String string) {
    return addons.get(string);
  }

  public void setClass(final String name, final Class<?> clazz) {
    classes.putIfAbsent(name, clazz);
  }

  public Class<?> getClassByName(final String name) {
    try {
      return classes.get(name);
    } catch (Exception ignored) {}
    return null;
  }

  public static void loadAddon(File file) {
    PrivateMines privateMines = PrivateMines.getPrivateMines();

    try (JarFile jarFile = new JarFile(file)) {
      privateMines.getLogger().info("jar file: " + jarFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
