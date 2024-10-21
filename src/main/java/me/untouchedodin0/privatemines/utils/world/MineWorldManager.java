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

package me.untouchedodin0.privatemines.utils.world;

import static me.untouchedodin0.privatemines.utils.world.utils.Direction.NORTH;

import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.storage.sql.SQLUtils;
import me.untouchedodin0.privatemines.utils.world.utils.Direction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;

public class MineWorldManager {

  private final Location defaultLocation;
  private Location nextLocation;
  private final int borderDistance;
  private int distance = 0;
  private Direction direction;
  private final World minesWorld;

  public MineWorldManager() {
    if (Bukkit.getWorld("privatemines") == null) {
      this.minesWorld = Bukkit.createWorld(new WorldCreator("privatemines").type(WorldType.FLAT)
          .generator(new EmptyWorldGenerator()));
    } else {
      this.minesWorld = Bukkit.getWorld("privatemines");
    }

    int yLevel = PrivateMines.getPrivateMines().getConfig().getInt("mineYLevel");
    this.borderDistance = PrivateMines.getPrivateMines().getConfig().getInt("mineDistance");

    if (minesWorld != null) {
      PrivateMines privateMines = PrivateMines.getPrivateMines();
      if (yLevel > minesWorld.getMaxHeight()) {
        privateMines.getLogger().info(String.format(
            "Mine Y level was set to %d but the max height of the world is %d Mine Y level has been set to %d!",
            yLevel, minesWorld.getMaxHeight(), 50));
        yLevel = 50;
        PrivateMines.getPrivateMines().getConfig().set("mineYLevel", 50);
        privateMines.saveConfig();
      }
    }
    this.defaultLocation = new Location(minesWorld, 0, yLevel, 0);
  }

  public Location getNextFreeLocation() {
    Location sqlLocation = SQLUtils.getCurrentLocation();

    if (distance == 0) {
      distance++;
      return defaultLocation;
    }

    if (direction == null) {
      direction = NORTH;
    }
    if (sqlLocation == null) {
      sqlLocation = direction.addTo(defaultLocation, distance * borderDistance);
      return sqlLocation;
    } else {
      if (nextLocation == null) {
        this.nextLocation = getDefaultLocation();
      }

      switch (direction) {
        case NORTH -> nextLocation.subtract(0, 0, distance * borderDistance);
        case EAST -> nextLocation.add(distance * borderDistance, 0, 0);
        case SOUTH -> nextLocation.add(0, 0, distance * borderDistance);
        case WEST -> nextLocation.subtract(distance * borderDistance, 0, 0);
      }
    }
    return nextLocation;
  }

  public World getMinesWorld() {
    return minesWorld;
  }

  public Location getDefaultLocation() {
    return defaultLocation;
  }

  public int getBorderDistance() {
    return borderDistance;
  }
}
