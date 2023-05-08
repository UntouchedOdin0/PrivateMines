package me.untouchedodin0.privatemines.utils.addon;

public abstract class Addon {

  public abstract void onEnable();
  public abstract void onDisable();
  public abstract void onReload();
  public abstract String getName();
  public abstract String getAuthor();
  public abstract String getVersion();
  public abstract String getDescription();
}
