package me.untouchedodin0.privatemines.utils.addon;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import java.util.zip.ZipException;
import org.bukkit.Bukkit;

public class AddonLoader {

  // https://stackoverflow.com/questions/65953961/java-plugins-with-a-custom-interface
  public static AddonProperty loadAddonProperties(File file) throws ZipException, IOException {
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
    try {
      Addon addon = (Addon) Class.forName(main, true, ucl).getDeclaredConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
             NoSuchMethodException | ClassNotFoundException e) {
      throw new RuntimeException(e);
    }

    Bukkit.getLogger().info("url " + url);
    Bukkit.getLogger().info("jarURL " + jarURL);
    Bukkit.getLogger().info("inputURL " + inputURL);
    Bukkit.getLogger().info("connection " + connection);
    Bukkit.getLogger().info("input " + input);
    Bukkit.getLogger().info("properties " + property);
    Bukkit.getLogger().info("name " + name);
    Bukkit.getLogger().info("version " + version);
    Bukkit.getLogger().info("main " + main);
    return null;
  }
}
