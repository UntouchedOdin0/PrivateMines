package me.untouchedodin0.privatemines.utils.regions;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.Objects;
import java.util.stream.Stream;

/*
    Credits to Redempt for making this initially within RedLib.
 */

public class CuboidRegion extends Region {

    Location minimumPoint;
    Location maximumPoint;

    public CuboidRegion(Location start, Location end) {
        setLocations(start, end);
    }

    protected void setLocations(Location start, Location end) {
        if (!Objects.equals(start.getWorld(), end.getWorld())) {
            throw new IllegalArgumentException("Locations must be in the same world");
        }
        double startX = start.getX();
        double startY = start.getY();
        double startZ = start.getZ();

        double endX = end.getX();
        double endY = end.getY();
        double endZ = end.getZ();

        System.out.printf("start x %f start y %f start z %f", startX, startY, startZ);
        System.out.printf("end x %f end y %f end z %f", endX, endY, endZ);

//        double minX = Math.min(start.getX(), end.getX());
//        double minY = Math.min(start.getY(), end.getY());
//        double minZ = Math.min(start.getZ(), end.getZ());
//
//        double maxX = Math.max(start.getX(), end.getX());
//        double maxY = Math.max(start.getY(), end.getY());
//        double maxZ = Math.max(start.getZ(), end.getZ());

        this.minimumPoint = new Location(start.getWorld(), startX, startY, startZ);
        this.maximumPoint = new Location(end.getWorld(), endX, endY, endZ);
    }

    public Location getMinimumPoint() {
        return minimumPoint;
    }

    public Location getMaximumPoint() {
        return maximumPoint;
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
        minimumPoint = minimumPoint.subtract(negX, negY, negZ);
        maximumPoint = minimumPoint.add(posX, posY, posZ);
        setLocations(minimumPoint, maximumPoint);
        return this;
    }


    public int getBlockVolume() {
        int[] dim = getBlockDimensions();
        return dim[0] * dim[1] * dim[2];
    }

    public Stream<Block> stream() {
        int[] dimensions = getBlockDimensions();
        RegionIterator regionIterator = new RegionIterator(dimensions[0], dimensions[1], dimensions[2]);
        Stream<Block> stream = Stream.generate(() -> {
            int[] pos = regionIterator.getPosition();
            Block block = minimumPoint.clone().add(pos[0], pos[1], pos[2]).getBlock();
            regionIterator.next();
            return block;
        });
        return stream.sequential().limit(getBlockVolume());
    }

    public World getWorld() {
        return Objects.requireNonNull(minimumPoint.getWorld());
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
            return new int[]{x, y, z};
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
