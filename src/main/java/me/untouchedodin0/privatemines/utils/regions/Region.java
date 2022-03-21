package me.untouchedodin0.privatemines.utils.regions;

import org.bukkit.Location;
import org.bukkit.block.Block;

import java.util.stream.Stream;

/**
 * Credits to Redempt with Redlib.
 */
public abstract class Region {

    /**
     * @return The least extreme corner of this Region
     */
    public abstract Location getMinimumPoint();

    /**
     * @return The most extreme corner of this Region
     */
    public abstract Location getMaximumPoint();

    public abstract Stream<Block> stream();

    /**
     * Get the dimensions of this Region [x, y, z]
     *
     * @return The dimensions of this Region
     */
    public int[] getBlockDimensions() {
        return new int[] {getMaximumPoint().getBlockX() - getMinimumPoint().getBlockX(),
                getMaximumPoint().getBlockY() - getMinimumPoint().getBlockY(),
                getMaximumPoint().getBlockZ() - getMinimumPoint().getBlockZ()};
    }
}

