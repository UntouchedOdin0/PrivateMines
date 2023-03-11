package me.untouchedodin0.privatemines.utils.addon;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import me.untouchedodin0.privatemines.PrivateMines;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class AddonAPI {

  static PrivateMines privateMines = PrivateMines.getPrivateMines();
  private static final AddonsManager addonsManager = privateMines.getAddonsManager();
  private static final Map<String, Class<?>> classMap = new HashMap<>();
  static boolean foundAddon = false;

  public static void load(Class<?> clazz) {
//    Addon addon = clazz.getAnnotation(Addon.class);
//    privateMines.getLogger().info("addon " + addon);
    privateMines.getLogger().info("clazz " + clazz);

//    String name = addon.name();
//    String author = addon.author();

    Method[] methods = clazz.getMethods();

    for (Method method : methods) {
      if (method.isAnnotationPresent(Enable.class)) {
        try {
          method.invoke(clazz.newInstance());
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
          throw new RuntimeException(e);
        }
      }
    }
//    classMap.put(name, clazz);
//    addonsManager.addAddon(name, addon);
  }

  public static void load(File file) {
    try {
      try (JarFile jarFile = new JarFile(file)) {
        final Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
          final JarEntry entry = entries.nextElement();
            JarEntry jarEntry = jarFile.getJarEntry(entry.getName());
            Class<?> clazz = jarEntry.getClass();
            Method[] methods = clazz.getMethods();
            load(clazz);
         }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public static void reload(CommandSender commandSender, String string) {
    Addon addon = addonsManager.getAddon(string);
    Class<?> clazz = classMap.get(string);

    if (clazz == null) {
      privateMines.getLogger().warning(
          String.format("Unable to find class %s, are you sure you specified the correct name?",
              string));
      commandSender.sendMessage(
          String.format("Unable to find class %s, are you sure you specified the correct name?",
              string));
      return;
    } else {
      Method[] methods = clazz.getMethods();

      Bukkit.broadcastMessage("clazz " + clazz);

      for (Method method : methods) {
        if (method.isAnnotationPresent(Reload.class)) {
          privateMines.getLogger().info("Found reload on method " + method);
          try {
            method.invoke(clazz.newInstance());
          } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            throw new RuntimeException(e);
          }
        }
      }
    }
  }
}
