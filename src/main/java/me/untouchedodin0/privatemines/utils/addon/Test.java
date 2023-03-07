package me.untouchedodin0.privatemines.utils.addon;

import java.util.Arrays;
import me.untouchedodin0.privatemines.PrivateMines;
import org.bukkit.Bukkit;

@Addon(name = "TestAddon", author = "DevTest", version = "1.0")
@Dependency(name = "PluginA", version = "1.5.6")
public class Test {

  PrivateMines privateMines = PrivateMines.getPrivateMines();

  @Enable
  public void onEnable() {
    Bukkit.getLogger().info("i'm doubting this will work sadly!");
    Dependency dependency = getClass().getAnnotation(Dependency.class);
    Dependency[] dependencies = getClass().getAnnotationsByType(Dependency.class);
    Bukkit.getLogger().info("dependency? " + dependency);
    Bukkit.getLogger().info("dependencies? " + Arrays.toString(dependencies));

    for (Dependency dependency1 : dependencies) {
      String name = dependency1.name();
      String version = dependency1.version();

      Bukkit.getLogger().info("name: " + dependency1.name());
      Bukkit.getLogger().info("version: " + dependency1.version());
    }
  }

  @Disable
  public void onDisable() {
    Bukkit.getLogger().info("Disabling the addon!");
  }
}
