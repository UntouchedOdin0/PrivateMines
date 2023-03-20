package me.untouchedodin0.privatemines.utils.addon.old;

import com.fastasyncworldedit.core.configuration.file.YamlConfiguration;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.util.Set;
import me.untouchedodin0.privatemines.PrivateMines;

/**
 * Loads addons and sets up permissions
 * @author Tastybento, ComminQ
 */
public class AddonClassLoader extends URLClassLoader {
  private final Map<String, Class<?>> classes = new HashMap<>();
  private final Addon addon;
  private final AddonsManager loader;
  Class<?> javaClass;
  Class<? extends Addon> addonClass;

  /**
   * For testing only
   * @param addon addon
   * @param loader Addons Manager
   * @param jarFile Jar File
   * @throws MalformedURLException exception
   */
  protected AddonClassLoader(Addon addon, AddonsManager loader, File jarFile) throws MalformedURLException {
    super(new URL[]{jarFile.toURL()});
    this.addon = addon;
    this.loader = loader;
  }

  public AddonClassLoader(AddonsManager addonsManager, YamlConfiguration data, File jarFile, ClassLoader parent)
      throws
      MalformedURLException,
      InstantiationException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    super(new URL[]{jarFile.toURI().toURL()}, parent);

    loader = addonsManager;


    try {
      String mainClass = data.getString("main");
      if (mainClass == null) {
        PrivateMines.getPrivateMines().getLogger().info("Failed to find main class");
      }
      javaClass = Class.forName(mainClass, true, this);
      if(mainClass.startsWith("me.untouchedodin0.privatemines")){
        PrivateMines.getPrivateMines().getLogger().warning("Package declaration cannot start with 'me.untouchedodin0.privatemines'");
      }
    } catch (Exception e) {
      PrivateMines.getPrivateMines().getLogger().warning("Could not load '" + jarFile.getName() + "' in folder '" + jarFile.getParent() + "' - " + e.getMessage());
    }

    try {
      addonClass = javaClass.asSubclass(Addon.class);
    } catch (ClassCastException e) {

    }

    addon = addonClass.getDeclaredConstructor().newInstance();
  }


  /* (non-Javadoc)
   * @see java.net.URLClassLoader#findClass(java.lang.String)
   */
  @Override
  protected Class<?> findClass(String name) {
    return findClass(name, true);
  }

  /**
   * This is a custom findClass that enables classes in other addons to be found
   * @param name - class name
   * @param checkGlobal - check globally or not when searching
   * @return Class - class if found
   */
  public Class<?> findClass(String name, boolean checkGlobal) {
    if (name.startsWith("me.untouchedodin0.privatemines")) {
      return null;
    }
    Class<?> result = classes.get(name);
    if (result == null) {
      if (checkGlobal) {
        result = loader.getClassByName(name);
      }

      if (result == null) {
        try {
          result = super.findClass(name);
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
          // Do nothing.
        }
        if (result != null) {
          loader.setClass(name, result);

        }
      }
      classes.put(name, result);
    }
    return result;
  }

  /**
   * @return the addon
   */
  public Addon getAddon() {
    return addon;
  }

  /**
   * @return class list
   */
  public Set<String> getClasses() {
    return classes.keySet();
  }
}