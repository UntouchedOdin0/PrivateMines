package me.untouchedodin0.privatemines.utils.regions;

import org.bukkit.Location;

public record CuboidRegion(Location minimumPoint, Location maximumPoint) {

    public Location getMinimumPoint() {
        return minimumPoint;
    }

    public Location getMaximumPoint() {
        return maximumPoint;
    }
}
