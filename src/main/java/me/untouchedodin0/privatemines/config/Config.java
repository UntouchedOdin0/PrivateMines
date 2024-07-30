/**
 * MIT License
 * <p>
 * Copyright (c) 2021 - 2023 Kyle Hicks
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
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
    @Comment("The distance between the private mines")
    public static int mineDistance = 150;
    @Comment("The height of where the private mines are pasted")
    public static int mineYLevel = 50;
    @Comment("Should there be a gap between the wall and the mine?")
    public static boolean addWallGap = true;
    @Comment("The gap between the walls and the mine")
    public static int wallsGap = 0;
    @Comment("Does the walls go up by one?")
    public static boolean shouldWallsGoUp = false;
    @Comment("Should we give players a mine when they first join?")
    public static boolean giveMineOnFirstJoin = false;
    @Comment("Should we upgrade the mine upon reaching the outside border?")
    public static boolean borderUpgrade = true;
    @Comment("Should the tax feature be enabled?")
    public static boolean enableTax = true;
    @Comment("Should we send tax messages to the owner of the mine?")
    public static boolean sendTaxMessages = true;
    @Comment("Should mines be closed by default?")
    public static boolean defaultClosed = true;
    @Comment("Should we have a reset cooldown?")
    public static boolean enableResetCooldown = true;
    @Comment("Specifies the cooldown for the /privatemines reset command")
    public static int resetCooldown = 15;
    @Comment("Use the new schematic loader?")
    public static boolean useNewSchematicLoader = true;
    @Comment("How many threads should be used for the schematic iterator")
    public static int schematicThreads = 4;
}
