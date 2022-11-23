package me.untouchedodin0.privatemines.utils.addons;

import java.io.File;

/**
 * Handles all addon management from the Server
 */
public interface AddonManager {

    /**
     * Registers the specified plugin loader
     *
     * @param loader Class name of the PluginLoader to register
     * @throws IllegalArgumentException Thrown when the given Class is not a
     *     valid PluginLoader
     */
    public void registerInterface(Class<? extends AddonsLoader> loader) throws IllegalArgumentException;

    /**
     * Checks if the given plugin is loaded and returns it when applicable
     * <p>
     * Please note that the name of the plugin is case-sensitive
     *
     * @param name Name of the plugin to check
     * @return Plugin if it exists, otherwise null
     */
    public Addon getAddon(String name);

    /**
     * Gets a list of all currently loaded plugins
     *
     * @return Array of Plugins
     */
    public Addon[] getAddons();

    /**
     * Checks if the given plugin is enabled or not
     * <p>
     * Please note that the name of the plugin is case-sensitive.
     *
     * @param name Name of the plugin to check
     * @return true if the plugin is enabled, otherwise false
     */
    public boolean isAddonEnabled(String name);

    /**
     * Checks if the given plugin is enabled or not
     *
     * @param addon Addon to check
     * @return true if the addon is enabled, otherwise false
     */
    public boolean isAddonEnabled(Addon addon);

    /**
     * Loads the plugin in the specified file
     * <p>
     * File must be valid according to the current enabled Plugin interfaces
     *
     * @param file File containing the plugin to load
     * @return The Plugin loaded, or null if it was invalid
     * @throws InvalidAddonException Thrown when the specified file is not a
     *     valid plugin
     * @throws InvalidAddonException Thrown when the specified file
     *     contains an invalid description
     * @throws InvalidAddonException If a required dependency could not
     *     be resolved
     */
    public Addon loadAddon(File file) throws InvalidAddonException;

    /**
     * Loads the plugins contained within the specified directory
     *
     * @param directory Directory to check for plugins
     * @return A list of all plugins loaded
     */
    public Addon[] loadAddons(File directory);

    /**
     * Disables all the loaded addons
     */
    public void disableAddons();

    /**
     * Disables and removes all plugins
     */
    public void clearAddons();

    /**
     * Enables the specified plugin
     * <p>
     * Attempting to enable a plugin that is already enabled will have no
     * effect
     *
     * @param addon Addon to enable
     */
    public void enableAddon(Addon addon);

    /**
     * Disables the specified plugin
     * <p>
     * Attempting to disable a plugin that is not enabled will have no effect
     *
     * @param addon Plugin to disable
     */
    public void disableAddon(Addon addon);
}