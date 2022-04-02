package me.untouchedodin0.privatemines.config;

import org.bukkit.Material;
import redempt.redlib.config.annotations.Comment;

public class Config {

    @Comment("The template material for the spawn point")
    public static Material spawnPoint = Material.SPONGE;
    @Comment("The template material for the corners")
    @Comment("(Needs 2 in a mine to create the cuboid)")
    public static Material mineCorner = Material.POWERED_RAIL;
    @Comment("The template material for the sell non player character")
    public static Material sellNpc = Material.WHITE_WOOL;
    @Comment("Check for any spigot updates")
    public static boolean notifyForUpdates = true;
    @Comment("The distance between the private mines")
    public static int mineDistance = 150;
}
