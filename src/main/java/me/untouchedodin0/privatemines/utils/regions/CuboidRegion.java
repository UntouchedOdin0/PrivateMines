package me.untouchedodin0.privatemines.utils.regions;

import org.bukkit.Location;

/*
    Credits to Redempt for making this initially within RedLib.
 */

public record CuboidRegion(Location minimumPoint, Location maximumPoint) {

    public Location getMinimumPoint() {
        return minimumPoint;
    }

    public Location getMaximumPoint() {
        return maximumPoint;
    }
}
