package me.untouchedodin0.privatemines.utils.addon;

public abstract class Addon {

  protected AddonProperty addonProperty;

  public abstract void onEnable();
  public abstract void onDisable();

  public AddonProperty getAddonProperty() {
    return addonProperty;
  }

  public void setAddonProperty(AddonProperty addonProperty) {
    this.addonProperty = addonProperty;
  }
}
