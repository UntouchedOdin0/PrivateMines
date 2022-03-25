package me.untouchedodin0.privatemines.utils.regions;

import me.untouchedodin0.privatemines.utils.location.LocationUtilsOld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.util.Vector;

import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Represents a cuboid region in a world
 * @author Redempt
 */
public class CuboidRegion extends Overlappable {

    /**
     * Gets a Region covering a cubic radius centered around a Location
     * @param loc The center
     * @param radius The block radius
     * @return A Region covering the specified radius
     */
    public static CuboidRegion cubeRadius(Location loc, int radius) {
        return new CuboidRegion(loc.clone().subtract(radius, radius, radius), loc.clone().add(radius + 1, radius + 1, radius + 1));
    }

    protected Location start;
    protected Location end;

    /**
     * Construct a Region using 2 corners
     * @param start The first corner
     * @param end The second corner
     */
    public CuboidRegion(Location start, Location end) {
        setLocations(start, end);
    }

    protected CuboidRegion() {}

    protected void setLocations(Location start, Location end) {
        if (!start.getWorld().equals(end.getWorld())) {
            throw new IllegalArgumentException("Locations must be in the same world");
        }
        double minX = Math.min(start.getX(), end.getX());
        double minY = Math.min(start.getY(), end.getY());
        double minZ = Math.min(start.getZ(), end.getZ());

        double maxX = Math.max(start.getX(), end.getX());
        double maxY = Math.max(start.getY(), end.getY());
        double maxZ = Math.max(start.getZ(), end.getZ());

        this.start = new Location(start.getWorld(), minX, minY, minZ);
        this.end = new Location(end.getWorld(), maxX, maxY, maxZ);
    }

    /**
     * Get the minimum corner of this Region
     * @return The corner with the lowest X, Y, and Z values
     */
    public Location getMinimumPoint() {
        return start.clone();
    }

    /**
     * Get the maximum corner of this Region
     * @return The corner with the highest X, Y, and Z values
     */
    public Location getMaximumPoint() {
        return end.clone();
    }

    /**
     * Check whether a location is inside this Region
     * @param loc The location to check
     * @return Whether this Region contains the given Location
     */
    public boolean contains(Location loc) {
        return loc.getWorld().getName().equals(start.getWorld().getName()) &&
                loc.getX() >= start.getX() && loc.getY() >= start.getY() && loc.getZ() >= start.getZ() &&
                loc.getX() < end.getX() && loc.getY() < end.getY() && loc.getZ() < end.getZ();
    }

    /**
     * @return The volume of this Region, in cubic meters
     */
    public double getVolume() {
        double[] dim = getDimensions();
        return dim[0] * dim[1] * dim[2];
    }

    /**
     * @return The volume of this Region, in blocks
     */
    public int getBlockVolume() {
        int[] dim = getBlockDimensions();
        return dim[0] * dim[1] * dim[2];
    }

    /**
     * Gets the current state of this Region
     * @return The state of this Region
     */
    public RegionState getState() {
        return new RegionState(this);
    }

    /**
     * Clones this Region
     * @return A clone of this Region
     */
    public CuboidRegion clone() {
        return new CuboidRegion(start.clone(), end.clone());
    }

    /**
     * Expands the region in all directions, or retracts if negative. If this is a MultiRegion,
     * Check if this is a MultiRegion before expanding.
     * @param amount The amount to expand the region by
     * @return Itself
     */
    public CuboidRegion expand(double amount) {
        expand(amount, amount, amount, amount, amount, amount);
        return this;
    }

    /**
     * Expands the region, or retracts where negative values are passed
     * @param posX The amount to expand the region in the positive X direction
     * @param negX The amount to expand the region in the negative X direction
     * @param posY The amount to expand the region in the positive Y direction
     * @param negY The amount to expand the region in the negative Y direction
     * @param posZ The amount to expand the region in the positive Z direction
     * @param negZ The amount to expand the region in the negative Z direction
     * @return Itself
     */
    public CuboidRegion expand(double posX, double negX, double posY, double negY, double posZ, double negZ) {
        start = start.subtract(negX, negY, negZ);
        end = end.add(posX, posY, posZ);
        setLocations(start, end);
        return this;
    }

    /**
     * Expand the region in a given direction, or retracts if negative.
     * @param direction The direction to expand the region in
     * @param amount The amount to expand the region in the given direction
     * @return Itself
     */
    public CuboidRegion expand(BlockFace direction, double amount) {
        Vector vec = LocationUtilsOld.getDirection(direction);
        if (vec.getX() + vec.getY() + vec.getZ() > 0) {
            vec = vec.multiply(amount);
            end = end.add(vec);
        } else {
            vec = vec.multiply(amount);
            start = start.add(vec);
        }
        setLocations(start, end);
        return this;
    }

    /**
     * Move the region
     * @param v The vector to be applied to both corners of the region
     * @return Itself
     */
    public CuboidRegion move(Vector v) {
        start = start.add(v);
        end = end.add(v);
        return this;
    }

    @Override
    public CuboidRegion move(double x, double y, double z) {
        return move(new Vector(x, y, z));
    }

    /**
     * Set the world of this region, while keeping the coordinates the same
     * @param world The world to set
     * @return Itself
     */
    public CuboidRegion setWorld(World world) {
        start.setWorld(world);
        end.setWorld(world);
        return this;
    }

    /**
     * @return Whether this Region is a non-cuboid variant
     */
    public boolean isMulti() {
        return false;
    }

    /**
     * Run a lambda on every Block in this Region
     * @param lambda The lambda to be run on each Block
     */
    public void forEachBlock(Consumer<Block> lambda) {
        stream().forEach(lambda);
    }

    /**
     * @return The World this Region is in
     */
    public World getWorld() {
        return start.getWorld();
    }

    /**
     * @return A Stream of all the blocks in this Region
     */
    public Stream<Block> stream() {
        int[] dimensions = this.getBlockDimensions();
        RegionIterator iterator = new RegionIterator(dimensions[0], dimensions[1], dimensions[2]);
        Stream<Block> stream = Stream.generate(() -> {
            int[] pos = iterator.getPosition();
            Block block = start.clone().add(pos[0], pos[1], pos[2]).getBlock();
            iterator.next();
            return block;
        });
        return stream.sequential().limit(getBlockVolume());
    }

    /**
     * Gets a CuboidRegion representing a 1-block thick slice on a face of this Region
     * @param face The face to get a slice of
     * @return A CuboidRegion representing a slice of the given face
     */
    public CuboidRegion getFace(BlockFace face) {
        CuboidRegion region = clone();
        region.expand(face.getOppositeFace(), -region.measure(face) + 1);
        return region;
    }

    /**
     * Converts this Region to a String which can be converted back with {@link CuboidRegion#fromString(String)} later
     * @return The String representation of this Region
     */
    public String toString() {
        return getWorld().getName() + " " + start.getX() + " " + start.getY() + " " + start.getZ() + " "
                + end.getX() + " " + end.getY() + " " + end.getZ();
    }

    /**
     * Converts a String generated by {@link CuboidRegion#toString()} back to a Region
     * @param input The String representation of a Region
     * @return The Region
     */
    public static CuboidRegion fromString(String input) {
        String[] split = input.split(" ");
        World world = Bukkit.getWorld(split[0]);
        double minX = Double.parseDouble(split[1]);
        double minY = Double.parseDouble(split[2]);
        double minZ = Double.parseDouble(split[3]);
        double maxX = Double.parseDouble(split[4]);
        double maxY = Double.parseDouble(split[5]);
        double maxZ = Double.parseDouble(split[6]);
        return new CuboidRegion(new Location(world, minX, minY, minZ), new Location(world, maxX, maxY, maxZ));
    }

    /**
     * Represents a state of a Region, not necessarily at the current point in time
     * @author Redempt
     *
     */
    public static class RegionState {

        BlockState[][][] blocks;
        private CuboidRegion region;

        private RegionState(CuboidRegion region) {
            this.region = region;
            int[] dimensions = region.getBlockDimensions();
            blocks = new BlockState[dimensions[0]][dimensions[1]][dimensions[2]];
            for (int x = 0; x < dimensions[0]; x++) {
                for (int y = 0; y < dimensions[1]; y++) {
                    for (int z = 0; z < dimensions[2]; z++) {
                        Location loc = region.getMinimumPoint().add(x, y, z);
                        blocks[x][y][z] = loc.getBlock().getState();
                    }
                }
            }
        }

        /**
         * Restores the Region to this state
         */
        public void restore() {
            int[] dimensions = region.getBlockDimensions();
            for (int x = 0; x < dimensions[0]; x++) {
                for (int y = 0; y < dimensions[1]; y++) {
                    for (int z = 0; z < dimensions[2]; z++) {
                        blocks[x][y][z].update(true, false);
                    }
                }
            }
        }

        /**
         * Gets all the BlockStates in this RegionState
         * @return The 3-dimensional array of BlockStates
         */
        public BlockState[][][] getBlocks() {
            return blocks.clone();
        }

    }

    private static class RegionIterator {

        private int maxX;
        private int maxY;
        private int maxZ;
        private int x = 0;
        private int y = 0;
        private int z = 0;

        public RegionIterator(int x, int y, int z) {
            this.maxX = x;
            this.maxY = y;
            this.maxZ = z;
        }

        public int[] getPosition() {
            return new int[] {x, y, z};
        }

        public boolean next() {
            x++;
            if (x >= maxX) {
                x = 0;
                y++;
                if (y >= maxY) {
                    y = 0;
                    z++;
                    if (z >= maxZ) {
                        return false;
                    }
                }
            }
            return true;
        }

    }

}