package me.untouchedodin0.privatemines.utils.regions;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents a region of an unspecified shape in the world
 *  Credits to Redempt with Redlib.
 */

public abstract class Region implements Cloneable {

    /**
     * @return The least extreme corner of this Region
     */
    public abstract Location getMinimumPoint();

    /**
     * @return The most extreme corner of this Region
     */
    public abstract Location getMaximumPoint();

    /**
     * @return The volume of this Region, in cubic meters
     */
    public abstract double getVolume();

    /**
     * @return The volume of this Region, in whole blocks
     */
    public abstract int getBlockVolume();

    /**
     * Expands this Region by a specified amount in each direction
     * @param posX The amount to increase in the positive X direction
     * @param negX The amount to increase in the negative X direction
     * @param posY The amount to increase in the positive Y direction
     * @param negY The amount to increase in the negative Y direction
     * @param posZ The amount to increase in the positive Z direction
     * @param negZ The amount to increase in the negative Z direction
     * @return Itself
     */
    public abstract Region expand(double posX, double negX, double posY, double negY, double posZ, double negZ);

    /**
     * Expands this Region in a specific direction
     * @param face The BlockFace representing the direction to expand in
     * @param amount The amount to expand
     * @return Itself
     */
    public abstract Region expand(BlockFace face, double amount);

    /**
     * Moves this Region
     * @param vec The vector representing the direction and amount to move
     * @return Itself
     */
    public abstract Region move(Vector vec);
    public abstract Region move(double x, double y, double z);

    /**
     * Determines if this Region contains a Location
     * @param loc The location to check
     * @return Whether the location is contained by this Region
     */
    public abstract boolean contains(Location loc);

    /**
     * @return A clone of this Region
     */
    public abstract Region clone();


    /**
     * Sets the World of this Region
     * @param world The World
     * @return Itself
     */
    public abstract Region setWorld(World world);

    /**
     * Streams all Blocks inside this Region
     * @return The stream of all Blocks contained in this Region
     */
    public abstract Stream<Block> stream();

    /**
     * Checks whether a Block is contained by this Region
     * @param block The Block
     * @return Whether the Block is contained by this Region
     */
    public boolean contains(Block block) {
        return contains(block.getLocation());
    }

    /**
     * @return The World this Region is in
     */
    public World getWorld() {
        return getMinimumPoint().getWorld();
    }

    /**
     * Streams every Block in this Region, running your lambda on it
     * @param forEach What to run on each Block
     */
    public void forEachBlock(Consumer<Block> forEach) {
        stream().forEach(forEach);
    }

    /**
     * Get the dimensions of this Region [x, y, z] in blocks
     * @return The dimensions of this Region
     */
    public int[] getBlockDimensions() {
        return new int[] {getMaximumPoint().getBlockX() - getMinimumPoint().getBlockX(),
                getMaximumPoint().getBlockY() - getMinimumPoint().getBlockY(),
                getMaximumPoint().getBlockZ() - getMinimumPoint().getBlockZ()};
    }

    /**
     * Get the dimensions of this Region [x, y, z]
     * @return The dimensions of this Region
     */
    public double[] getDimensions() {
        return new double[] {getMaximumPoint().getX() - getMinimumPoint().getX(),
                getMaximumPoint().getY() - getMinimumPoint().getY(),
                getMaximumPoint().getZ() - getMinimumPoint().getZ()};
    }

    /**
     * @return All 8 cuboid corners of this Region
     */
    public Location[] getCorners() {
        Location start = getMinimumPoint();
        Location end = getMaximumPoint();
        return new Location[] {
                start,
                end,
                new Location(getWorld(), start.getX(), start.getY(), end.getZ()),
                new Location(getWorld(), start.getX(), end.getY(), start.getZ()),
                new Location(getWorld(), end.getX(), start.getY(), start.getZ()),
                new Location(getWorld(), start.getX(), end.getY(), end.getZ()),
                new Location(getWorld(), end.getX(), end.getY(), start.getZ()),
                new Location(getWorld(), end.getX(), start.getY(), end.getZ())
        };
    }

    /**
     * @return A cuboid representation of this Region using the extreme corners
     */
    public CuboidRegion toCuboid() {
        return new CuboidRegion(getMinimumPoint().clone(), getMaximumPoint().clone());
    }

    /**
     * @return The center of this Region, the midpoint of the two extreme corners
     */
    public Location getCenter() {
        return getMinimumPoint().clone().add(getMaximumPoint()).multiply(0.5);
    }

    /**
     * Gets the length of this Region along a given axis
     * @param direction The BlockFace representing the axis - opposites will act the same (i.e. UP, DOWN)
     * @return The length of this Region along the given axis
     */
    public double measure(BlockFace direction) {
        switch (direction) {
            case UP:
            case DOWN:
                return getDimensions()[1];
            case EAST:
            case WEST:
                return getDimensions()[0];
            case NORTH:
            case SOUTH:
                return getDimensions()[2];
            default:
                throw new IllegalArgumentException("Face must be one of UP, DOWN, NORTH, SOUTH, EAST, or WEST");
        }
    }

    /**
     * Gets the block length of this Region along a given axis
     * @param direction The BlockFace representing the axis - opposites will act the same (i.e. UP, DOWN)
     * @return The block length of this Region along the given axis
     */
    public int measureBlocks(BlockFace direction) {
        switch (direction) {
            case UP:
            case DOWN:
                return getBlockDimensions()[1];
            case EAST:
            case WEST:
                return getBlockDimensions()[0];
            case NORTH:
            case SOUTH:
                return getBlockDimensions()[2];
            default:
                throw new IllegalArgumentException("Face must be one of UP, DOWN, NORTH, SOUTH, EAST, or WEST");
        }
    }

}