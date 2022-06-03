package me.untouchedodin0.privatemines.mine.quarries;

import org.bukkit.Location;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class Quarry {

    private UUID owner;
    private Location location;
    private int intervalSeconds;

    private AtomicInteger efficiencyLevel;
    private AtomicInteger fortuneLevel;
    private AtomicInteger lightningLevel;
    private AtomicInteger tornadoLevel;
    private AtomicInteger hailLevel;
    private AtomicInteger acidRainLevel;
    private AtomicInteger jackhammerLevel;
    private AtomicInteger explosiveLevel;
    private AtomicInteger laserLevel;

    public UUID getOwner() {
        return owner;
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
}
