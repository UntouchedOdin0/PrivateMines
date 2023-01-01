/**
 * MIT License
 * <p>
 * Copyright (c) 2021 - 2023 Kyle Hicks
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.untouchedodin0.privatemines.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

public class Utils {

  public static Location getRelative(Region region, int x, int y, int z) {
    final BlockVector3 point = region.getMinimumPoint().getMinimum(region.getMaximumPoint());
    final int regionX = point.getX();
    final int regionY = point.getY();
    final int regionZ = point.getZ();
    final BlockVector3 maxPoint = region.getMaximumPoint().getMaximum(region.getMinimumPoint());
    final int maxX = maxPoint.getX();
    final int maxY = maxPoint.getY();
    final int maxZ = maxPoint.getZ();

    final World worldeditWorld = region.getWorld();
    final org.bukkit.World bukkitWorld;
    if (worldeditWorld != null) {
      bukkitWorld = BukkitAdapter.asBukkitWorld(worldeditWorld).getWorld();
    } else {
      bukkitWorld = null;
    }
    return new Location(bukkitWorld, regionX + x, regionY + y, regionZ + z);
  }

  public static Location toLocation(BlockVector3 vector3, org.bukkit.World world) {
    return new Location(world, vector3.getX(), vector3.getY(), vector3.getZ());
  }

  // Credits to Redempt for this method
  // https://github.com/Redempt/RedLib/blob/master/src/redempt/redlib/region/CuboidRegion.java#L78-L87
  public static boolean contains(Location min, Location max, Location loc) {
    if (min.getWorld() != null && max.getWorld() != null && loc.getWorld() != null) {
      return loc.getWorld().getName().equals(min.getWorld().getName()) && loc.getX() >= min.getX()
          && loc.getY() >= min.getY() && loc.getZ() >= min.getZ() && loc.getX() < max.getX()
          && loc.getY() < max.getY() && loc.getZ() < max.getZ();
    }
    return false;
  }

  public static CuboidRegion toWorldEditCuboid(
      me.untouchedodin0.privatemines.utils.regions.CuboidRegion cuboidRegion) {
    var min = BlockVector3.at(cuboidRegion.getMinimumPoint().getBlockX(),
        cuboidRegion.getMinimumPoint().getBlockY(), cuboidRegion.getMinimumPoint().getBlockZ());

    var max = BlockVector3.at(cuboidRegion.getMaximumPoint().getBlockX(),
        cuboidRegion.getMaximumPoint().getBlockY(), cuboidRegion.getMaximumPoint().getBlockZ());

    return new CuboidRegion(min, max);
  }


  /**
   * Utility method to set a flag.
   * <p>
   * Borrowed from <a
   * href="https://github.com/EngineHub/WorldGuard/blob/bc63119373d4603e5b040460c41e712275a4d062/worldguard-core/src/main/java/com/sk89q/worldguard/commands/region/RegionCommandsBase.java#L414-L427">...</a>
   *
   * @param region the region
   * @param flag   the flag
   * @param value  the value
   * @throws InvalidFlagFormat thrown if the value is invalid
   */
  public static <V> void setFlag(ProtectedRegion region, Flag<V> flag, String value)
      throws InvalidFlagFormat {
    V val = flag.parseInput(
        FlagContext.create().setInput(value).setObject("region", region).build());
    region.setFlag(flag, val);
  }

  public static int getInventorySize(int max) {
    if (max <= 0) {
      return 9;
    }
    int quotient = (int) Math.ceil(max / 9.0);
    return quotient > 5 ? 54 : quotient * 9;
  }

  public static int rowsToSlots(int rows) {
    return rows * 9;
  }

  public static String color(String string) {
    return ChatColor.translateAlternateColorCodes('&', string);
  }

  public static List<String> color(List<String> list) {
    List<String> stringList = new ArrayList<>();
    list.forEach(string -> stringList.add(color(string)));
    return stringList;
  }

  public static String colorBukkit(String string) {
    return ChatColor.translateAlternateColorCodes('&', string);
  }

  public static String format(Material material) {
    return WordUtils.capitalize(material.name().toLowerCase().replaceAll("_", " "));
  }

  public static String getRandom(int digits) {
    Random rand = new Random(); //instance of random class
    String total_characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    StringBuilder randomString = new StringBuilder();
    for (int i = 0; i < digits; i++) {
      int index = rand.nextInt(total_characters.length() - 1);
      randomString.append(total_characters.charAt(index));
    }
    return randomString.toString();
  }
}
