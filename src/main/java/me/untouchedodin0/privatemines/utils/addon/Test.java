package me.untouchedodin0.privatemines.utils.addon;

import me.untouchedodin0.privatemines.PrivateMines;
import org.bukkit.Bukkit;

@Addon(name = "TestAddon", author = "DevTest")
public class Test {

  @Enable
  public void onEnable() {
    Bukkit.getLogger().info("i'm doubting this will work sadly!");
  }

  @Disable
  public void onDisable() {
    Bukkit.getLogger().info("Disabling the addon!");
  }
}
