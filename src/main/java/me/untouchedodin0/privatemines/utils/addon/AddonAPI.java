package me.untouchedodin0.privatemines.utils.addon;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;
import me.untouchedodin0.privatemines.PrivateMines;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

public class AddonAPI {

  static PrivateMines privateMines = PrivateMines.getPrivateMines();
  private static final AddonsManager addonsManager = privateMines.getAddonsManager();
  private static final Map<String, Class<?>> classMap = new HashMap<>();
  static boolean foundAddon = false;

  public static void load(Class<?> clazz) {
    Addon addon = clazz.getAnnotation(Addon.class);
    String name = addon.name();

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
    classMap.put(name, clazz);
    addonsManager.addAddon(name, addon);
  }

  public static void load(File file) {
    try {
      URL url = file.toURI().toURL();

      URLClassLoader classLoader = new URLClassLoader(new URL[] {url});
      Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
      URL[] urls = classLoader.getURLs();
      for (URL fileURL : urls) {
        System.out.println("Searching " + fileURL.getFile() + " for classes...");
      }
      try {
        try (JarFile jarFile = new JarFile(file)) {
          Enumeration<JarEntry> entries = jarFile.entries();

          privateMines.getLogger().info("Jar file " + jarFile);
          privateMines.getLogger().info("Jar file entries " + entries);

          while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            privateMines.getLogger().info("latest entry " + entry);
            if (entry.getName().endsWith(".class")) {
              String className = entry.getName().replaceAll("/", ".").replaceAll(".class", "");
              try {
                Class<?> clazz = classLoader.loadClass(className);

                privateMines.getLogger().info("className " + className);
                privateMines.getLogger().info("clazz " + clazz);

                privateMines.getLogger().info("Loaded class " + clazz + "!");
              } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
              }
            }
          }
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

//      privateMines.getLogger().info("url " + url);
//      privateMines.getLogger().info("urlClassLoader " + classLoader);
//      privateMines.getLogger().info("method " + method);
//      privateMines.getLogger().info("urls: " + Arrays.toString(urls));

    } catch (MalformedURLException | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
//    try {
//      try (JarFile jarFile = new JarFile(file)) {
//        final Enumeration<JarEntry> entries = jarFile.entries();
//        while (entries.hasMoreElements()) {
//          final JarEntry entry = entries.nextElement();
//            JarEntry jarEntry = jarFile.getJarEntry(entry.getName());
//            Class<?> clazz = jarEntry.getClass();
//            Method[] methods = clazz.getMethods();
//            load(clazz);
//         }
//      }
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
  }

  public static void load(Path path) {
    privateMines.getLogger().info("Loading adds from path " + path);
    File file = path.toFile();
    File[] files = file.listFiles();

    privateMines.getLogger().info("file: " + file);
    privateMines.getLogger().info("files: " + Arrays.toString(files));

    if (files != null) {
      for (File latestFile : files) {
        privateMines.getLogger().info("latest file " + latestFile);
        load(latestFile);
      }
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
