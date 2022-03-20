package me.untouchedodin0.privatemines.utils.regions;

import org.bukkit.Location;
import org.bukkit.World;
import java.util.Objects;

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

    public World getWorld() {
        return Objects.requireNonNull(minimumPoint.getWorld());
    }
}
