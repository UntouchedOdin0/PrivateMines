package me.untouchedodin0.privatemines.utils.addon.old;

public abstract class Addon {

  /**
   * Executes code when enabling the addon.
   * This is called after {@link #onLoad()}.
   * <br/>
   * Note that commands and worlds registration <b>must</b> be done in {@link #onLoad()}, if need be.
   * Failure to do so <b>will</b> result in issues such as tab-completion not working for commands.
   */
  public abstract void onEnable();

  /**
   * Executes code when disabling the addon.
   */
  public abstract void onDisable();

  /**
   * Executes code when loading the addon.
   * This is called before {@link #onEnable()}.
   * This <b>must</b> be used to setup configuration, worlds and commands.
   */
  public void onLoad() {}

  /**
   * Executes code when reloading the addon.
   */
  public void onReload() {}
}
