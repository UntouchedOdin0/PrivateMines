package me.untouchedodin0.privatemines.utils.addon;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import me.untouchedodin0.privatemines.PrivateMines;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class AddonAPI {

  static PrivateMines privateMines = PrivateMines.getPrivateMines();
  private static final AddonsManager addonsManager = privateMines.getAddonsManager();
  private static Map<String, Class<?>> classMap = new HashMap<>();

  public static void load(Class<?> clazz) {
    Addon addon = clazz.getAnnotation(Addon.class);
    String name = addon.name();
    String author = addon.author();

    Method[] methods = clazz.getMethods();
    Dependency[] dependencies = clazz.getDeclaredAnnotationsByType(Dependency.class);

    if (clazz.isAnnotationPresent(Dependency.class)) {
      for (Dependency dependency : dependencies) {
        privateMines.getLogger().info("dependency? " + dependency);
        String dependencyName = dependency.name();
        String dependencyVersion = dependency.version();
        boolean isAddon = dependency.isAddon();

        privateMines.getLogger().info("dependencyName: " + dependencyName);
        privateMines.getLogger().info("dependencyVersion: " + dependencyVersion);
        privateMines.getLogger().info("isAddon: " + isAddon);
      }
    }
    privateMines.getLogger().info("addon annotation " + addon);
    privateMines.getLogger().info("methods: " + Arrays.toString(methods));
    privateMines.getLogger().info("name " + name);
    privateMines.getLogger().info("author " + author);

    for (Method method : methods) {
      if (method.isAnnotationPresent(Enable.class)) {
        try {
          method.invoke(clazz.newInstance());
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
          throw new RuntimeException(e);
        }
      }

//      if (method.isAnnotationPresent(Disable.class)) {
//        try {
//          method.invoke(clazz.newInstance());
//        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
//          throw new RuntimeException(e);
//        }
//      }
    }
    classMap.put(name, clazz);
    addonsManager.addAddon(name, addon);
  }

  public static void reload(CommandSender commandSender, String string) {
    Addon addon = addonsManager.getAddon(string);
    Class<?> clazz = classMap.get(string);

    if (clazz == null) {
      privateMines.getLogger().warning(
          String.format("Unable to find class %s, are you sure you specified the correct name?",
          string));
      commandSender.sendMessage(String.format("Unable to find class %s, are you sure you specified the correct name?",
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
