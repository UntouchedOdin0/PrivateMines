package me.untouchedodin0.privatemines.utils.addon;

import java.util.HashMap;
import java.util.Map;

public class AddonsManager {

  public Map<String, Addon> addons = new HashMap<>();

  public Map<String, Addon> getAddons() {
    return addons;
  }

  private void addAddon(String string, Addon addon) {
    addons.putIfAbsent(string, addon);
  }

  public Addon getAddon(String string) {
    return addons.get(string);
  }
}
