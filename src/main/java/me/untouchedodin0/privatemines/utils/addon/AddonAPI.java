package me.untouchedodin0.privatemines.utils.addon;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import me.untouchedodin0.privatemines.PrivateMines;

public class AddonAPI {

  static PrivateMines privateMines = PrivateMines.getPrivateMines();
  private static AddonsManager addonsManager = privateMines.getAddonsManager();

  public static void load(Class<?> clazz) {
    Addon addon = clazz.getAnnotation(Addon.class);
    String name = addon.name();
    String author = addon.author();

    Method[] methods = clazz.getMethods();

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

      if (method.isAnnotationPresent(Disable.class)) {
        try {
          method.invoke(clazz.newInstance());
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException e) {
          throw new RuntimeException(e);
        }
      }
    }
    addonsManager.addAddon(name, addon);
  }
}
