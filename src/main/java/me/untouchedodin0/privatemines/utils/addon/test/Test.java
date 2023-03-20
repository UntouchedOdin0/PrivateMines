package me.untouchedodin0.privatemines.utils.addon.test;

import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.utils.addon.old.Addon2;
import me.untouchedodin0.privatemines.utils.addon.old.Dependency;
import me.untouchedodin0.privatemines.utils.addon.old.Disable;
import me.untouchedodin0.privatemines.utils.addon.old.Enable;
import me.untouchedodin0.privatemines.utils.addon.old.Reload;
import org.bukkit.Bukkit;

@Addon2(name = "TestAddon", author = "DevTest", version = "1.0", description = "Addon Description Test")
@Dependency(name = "test", version = "1.1", isAddon = false)
public class Test {
  PrivateMines privateMines = PrivateMines.getPrivateMines();

  @Enable
  public void onEnable() {
    Bukkit.getLogger().info("i'm doubting this will work sadly!");
    Addon2 addon = getClass().getAnnotation(Addon2.class);
    Dependency dependency = getClass().getAnnotation(Dependency.class);
    Dependency[] dependencies = getClass().getAnnotationsByType(Dependency.class);
    privateMines.getLogger().info("Loading addon " + addon.name());
  }

  @Disable
  public void onDisable() {
    Addon2 addon = getClass().getAnnotation(Addon2.class);
    privateMines.getLogger().info("Disabling addon " + addon.name());
  }

  @Reload
  public void onReload() {
    Bukkit.broadcastMessage("reloading the addon! :)");
  }
}
