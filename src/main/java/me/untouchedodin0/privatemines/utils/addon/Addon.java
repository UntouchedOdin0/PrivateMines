package me.untouchedodin0.privatemines.utils.addon;

public interface Addon {

  void onEnable();

  public abstract String getName();
  public abstract String getAuthor();
  public abstract String getVersion();
}
