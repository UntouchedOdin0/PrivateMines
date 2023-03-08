package me.untouchedodin0.privatemines.utils.addon;

import me.untouchedodin0.privatemines.PrivateMines;
import org.bukkit.Bukkit;

@Addon(name = "TestAddon", author = "DevTest", version = "1.0")
@Dependency(name = "test", version = "1.1", isAddon = false)
//@Dependency(name = "test2", version = "1.1", isAddon = true)
public class Test {

  PrivateMines privateMines = PrivateMines.getPrivateMines();

  @Enable
  public void onEnable() {
    Bukkit.getLogger().info("i'm doubting this will work sadly!");
    Addon addon = getClass().getAnnotation(Addon.class);
    Dependency dependency = getClass().getAnnotation(Dependency.class);
    Dependency[] dependencies = getClass().getAnnotationsByType(Dependency.class);
    privateMines.getLogger().info("Loading addon " + addon.name());
  }

  @Disable
  public void onDisable() {
    Addon addon = getClass().getAnnotation(Addon.class);
    privateMines.getLogger().info("Disabling addon " + addon.name());
  }
}
