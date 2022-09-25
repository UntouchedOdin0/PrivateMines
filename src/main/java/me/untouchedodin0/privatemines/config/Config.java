/**
 * MIT License
 *
 * Copyright (c) 2021 - 2022 Kyle Hicks
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
    @Comment("The material to look for when expanding for upgrades")
    public static Material upgradeMaterial = Material.OBSIDIAN;
    @Comment("The template material for quarries")
    public static Material quarryMaterial = Material.SHULKER_BOX;
//    @Comment("Check for any spigot updates")
//    public static boolean notifyForUpdates = true;
    @Comment("The distance between the private mines")
    public static int mineDistance = 150;
    @Comment("The height of where the private mines are pasted")
    public static int mineYLevel = 50;
    @Comment("The delay until a player is teleported to the private mine")
    public static int teleportDelay = 5;
    @Comment("Should there be a gap between the wall and the mine?")
    public static boolean addWallGap = true;
    @Comment("The gap between the walls and the mine")
    public static int wallsGap = 0;
    @Comment("Does the walls go up by one?")
    public static boolean shouldWallsGoUp = false;
    @Comment("Should we give players a mine when they first join?")
    public static boolean giveMineOnFirstJoin = false;
    @Comment("Used for giving mines before the server has released")
    public static boolean preLoginGiveMine = false;
    @Comment("Should we only replace air blocks when the mine resets?")
    public static boolean onlyReplaceAir = true;
    @Comment("Should we upgrade the mine upon reaching the outside border?")
    public static boolean borderUpgrade = true;

    @Comment("Which locale should we use?")
    public static String locale = "en_US";
    @Comment("Should we support adventure?")
    public static boolean useAdventure = false;
    @Comment("Should the menu be enabled?")
    public static boolean enableMenu = true;
    @Comment("Should the tax feature be enabled?")
    public static boolean enableTax = true;
    @Comment("Should we send tax messages to the owner of the mine?")
    public static boolean sendTaxMessages = true;
}
