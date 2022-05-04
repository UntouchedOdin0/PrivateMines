package me.untouchedodin0.privatemines.utils.addon;

import me.untouchedodin0.privatemines.PrivateMines;
import org.apache.commons.lang.WordUtils;

import java.util.Collection;
import java.util.HashMap;

public class AddonManager {

    private final PrivateMines privateMines;
    private final HashMap<String, Addon> addons;

    public AddonManager(PrivateMines privateMines) {
        this.privateMines = privateMines;
        this.addons = new HashMap<>();
    }

    /**
     * Gets the amount of Addon's loaded into the HashMap
     */
    public int getAddonCount() {
        return this.addons.size();
    }

    /**
     * Gets a collection of loaded addons
     */
    public final Collection<Addon> getAddons() {
        return this.addons.values();
    }

    /**
     * Adds an addon to the HashMap of addons
     * and logs to console
     */
    public void addAddon(final Addon addon) {
        final String addonName = WordUtils.capitalize(addon.getName());
        final String author = addon.getAuthor();
        final String version = addon.getVersion();

        if (this.addons.containsKey(addonName)) {
            privateMines.getLogger().warning("Addon " + addonName + " is already loaded!");
        } else {
            this.addons.putIfAbsent(addonName, addon);
            privateMines.getLogger().info(String.format("Loaded addon %s v%s created by %s", addonName, author, version));
        }
    }
}
