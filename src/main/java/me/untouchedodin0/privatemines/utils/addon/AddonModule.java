package me.untouchedodin0.privatemines.utils.addon;

public abstract class AddonModule {

    /*
     * Called when the module is enabled.
     */

    public abstract void onEnable();

    /*
     * Called when the module is disabled.
     */

    public abstract void onDisable();
}
