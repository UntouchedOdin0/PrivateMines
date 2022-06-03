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
