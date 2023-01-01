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

package me.untouchedodin0.privatemines.mine.quarries;

import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mine.Mine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import redempt.redlib.misc.LocationUtils;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Quarry {

    PrivateMines privateMines = PrivateMines.getPrivateMines();
    public UUID owner;
    public Mine mine;
    public Location location;
    public int intervalSeconds;
    public AtomicInteger efficiencyLevel;
    public AtomicInteger fortuneLevel;
    public AtomicInteger lightningLevel;
    public AtomicInteger tornadoLevel;
    public AtomicInteger hailLevel;
    public AtomicInteger acidRainLevel;
    public AtomicInteger jackhammerLevel;
    public AtomicInteger explosiveLevel;
    public AtomicInteger laserLevel;

    public UUID getOwner() {
        return owner;
    }
    public Mine getMine() {
        return mine;
    }

    public Location getLocation() {
        return location;
    }

    public int getIntervalSeconds() {
        return intervalSeconds;
    }

    public AtomicInteger getEfficiencyLevel() {
        return efficiencyLevel;
    }

    public AtomicInteger getFortuneLevel() {
        return fortuneLevel;
    }

    public AtomicInteger getLightningLevel() {
        return lightningLevel;
    }

    public AtomicInteger getTornadoLevel() {
        return tornadoLevel;
    }

    public AtomicInteger getHailLevel() {
        return hailLevel;
    }

    public AtomicInteger getAcidRainLevel() {
        return acidRainLevel;
    }

    public AtomicInteger getJackhammerLevel() {
        return jackhammerLevel;
    }

    public AtomicInteger getExplosiveLevel() {
        return explosiveLevel;
    }

    public AtomicInteger getLaserLevel() {
        return laserLevel;
    }

    public void spawn() {
        Block block = location.getBlock();
        if (!block.isEmpty()) {
            privateMines.getLogger().warning("Failed to spawn "
                                                     + Bukkit.getOfflinePlayer(getOwner()).getName() +
                                                     "'s Quarry as the block wasn't empty!");
        } else {
            block.setType(Material.DIRT);
            privateMines.getLogger().info(LocationUtils.toString(block));
        }
    }
}
