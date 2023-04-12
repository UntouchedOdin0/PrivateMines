package me.untouchedodin0.privatemines.utils.addon.old2;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import me.untouchedodin0.privateminesapi.addon.Addon;
import me.untouchedodin0.privateminesapi.addon.AddonProperty;
import org.bukkit.Bukkit;

public class AddonLoader {

  // https://stackoverflow.com/questions/65953961/java-plugins-with-a-custom-interface
  public static AddonProperty loadAddonProperties(File file) throws IOException {
    URL url = file.toURI().toURL();
    String jarURL = "jar:" + url +"!/plugin.properties";
    URL urls[] = {new URL(jarURL)};
    URLClassLoader ucl = new URLClassLoader(urls);

    InputStream input;
    URL inputURL = new URL(jarURL);
    JarURLConnection connection = (JarURLConnection)inputURL.openConnection();
    input = connection.getInputStream();
    Properties property = new Properties();
    property.load(input);
    String name = property.getProperty("name");
    String version = property.getProperty("version");
    String main = property.getProperty("main");
    AddonProperty addonProperty = new AddonProperty(main, name, version, "test");

//    try {
//      Class<?> clazz = Class.forName("TestAddon", true, ucl);
//
//      Bukkit.getLogger().info("clazz " + clazz);
//    } catch (ClassNotFoundException e) {
//      throw new RuntimeException(e);
//    }
//    try {
//      Addon addon = (Addon) Class.forName(main, true, ucl).getDeclaredConstructor().newInstance();
//    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
//             NoSuchMethodException | ClassNotFoundException e) {
//      throw new RuntimeException(e);
//    }

//    Bukkit.getLogger().info("url " + url);
//    Bukkit.getLogger().info("jarURL " + jarURL);
//    Bukkit.getLogger().info("inputURL " + inputURL);
//    Bukkit.getLogger().info("connection " + connection);
//    Bukkit.getLogger().info("input " + input);
//    Bukkit.getLogger().info("properties " + property);
//    Bukkit.getLogger().info("name " + name);
//    Bukkit.getLogger().info("version " + version);
//    Bukkit.getLogger().info("main " + main);
    return addonProperty;
  }

  public static Addon loadPlugin(File file) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
    AddonProperty property = loadAddonProperties(file);
    URL url = file.toURI().toURL();
    Bukkit.getLogger().info("url " + url);
    String jarURL = "jar:" + url + "!/";
    Bukkit.getLogger().info("jarURL " + jarURL);
    URL urls[] = {new URL(jarURL)};
    Bukkit.getLogger().info("urls[] " + urls);

    URLClassLoader ucl = new URLClassLoader(urls);
    Bukkit.getLogger().info("ucl " + ucl);
    Addon plugin = (Addon) Class.forName("me.untouchedodin0.addon.TestAddon", true, ucl).getDeclaredConstructor().newInstance();
    Bukkit.getLogger().info("plugin " + plugin);

    plugin.setAddonProperty(property);

//    Bukkit.getLogger().info("property " + property);
//    Bukkit.getLogger().info("url " + jarURL);
//    Bukkit.getLogger().info("urls " + urls);
//    Bukkit.getLogger().info("ucl " + ucl);
//    Bukkit.getLogger().info("plugin " + plugin);
    return plugin;
  }
}

