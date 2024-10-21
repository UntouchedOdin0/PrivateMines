package me.untouchedodin0.privatemines.utils.regions;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;

import java.util.ArrayList;
import java.util.List;

public class RegionUtils {

  // Enum to specify the split mode (NUMBER or SIZE)
  public enum SplitMode {
    NUMBER, SIZE
  }

  public static Region[] getSubRegions(
      Region region) {
    int midX = (region.getMinimumPoint().getX() + region.getMaximumPoint().getX()) / 2;
    int midY = (region.getMinimumPoint().getY() + region.getMaximumPoint().getY()) / 2;
    int midZ = (region.getMinimumPoint().getZ() + region.getMaximumPoint().getZ()) / 2;

    return new Region[]{
        new CuboidRegion(region.getMinimumPoint(), BlockVector3.at(midX, midY, midZ)),
        new CuboidRegion(BlockVector3.at(midX + 1, region.getMinimumPoint().getY(),
            region.getMinimumPoint().getZ()), region.getMaximumPoint()), new CuboidRegion(
        BlockVector3.at(region.getMinimumPoint().getX(), midY + 1, region.getMinimumPoint().getZ()),
        region.getMaximumPoint()), new CuboidRegion(
        BlockVector3.at(region.getMinimumPoint().getX(), region.getMinimumPoint().getY(), midZ + 1),
        region.getMaximumPoint())};
  }

  public static Region[] splitRegions(Region region, int value, SplitMode mode) {
    int minX = region.getMinimumPoint().getX();
    int minY = region.getMinimumPoint().getY();
    int minZ = region.getMinimumPoint().getZ();

    int maxX = region.getMaximumPoint().getX();
    int maxY = region.getMaximumPoint().getY();
    int maxZ = region.getMaximumPoint().getZ();

    // Calculate total size of the region
    int totalX = maxX - minX + 1;
    int totalY = maxY - minY + 1;
    int totalZ = maxZ - minZ + 1;

    List<Region> subRegions = new ArrayList<>();

    // Determine the number of splits in each dimension based on the mode
    int splitsX, splitsY, splitsZ;

    if (mode == SplitMode.NUMBER) {
      // Assuming a cubic distribution of regions for a roughly equal split
      splitsX = (int) Math.ceil(Math.cbrt(value));
      splitsY = (int) Math.ceil(Math.cbrt(value));
      splitsZ = (int) Math.ceil(Math.cbrt(value));

      // Ensure the total number of splits
      while (splitsX * splitsY * splitsZ < value) {
        // Increment splits in Z until we have enough regions
        if (splitsZ < splitsY || splitsZ < splitsX) {
          splitsZ++;
        } else if (splitsY < splitsX) {
          splitsY++;
        } else {
          splitsX++;
        }
      }

    } else if (mode == SplitMode.SIZE) {
      splitsX = splitsY = splitsZ = value;
    } else {
      throw new IllegalArgumentException("Invalid split mode");
    }

    // Calculate sizes for each subregion based on splits
    int sizeX = (int) Math.ceil((double) totalX / splitsX);
    int sizeY = (int) Math.ceil((double) totalY / splitsY);
    int sizeZ = (int) Math.ceil((double) totalZ / splitsZ);

    // Divide the region into subregions
    for (int x = minX; x <= maxX; x += sizeX) {
      for (int y = minY; y <= maxY; y += sizeY) {
        for (int z = minZ; z <= maxZ; z += sizeZ) {
          // Define the minimum point of the subregion (current iteration point)
          BlockVector3 minPoint = BlockVector3.at(x, y, z);

          // Calculate the maximum point of the subregion, ensuring we don't exceed the original region's boundaries
          int newMaxX = Math.min(x + sizeX - 1, maxX);
          int newMaxY = Math.min(y + sizeY - 1, maxY);
          int newMaxZ = Math.min(z + sizeZ - 1, maxZ);

          BlockVector3 maxPoint = BlockVector3.at(newMaxX, newMaxY, newMaxZ);

          // Create the subregion and add it to the list
          subRegions.add(new CuboidRegion(minPoint, maxPoint));
        }
      }
    }

    // Return the subregions as an array
    return subRegions.toArray(new Region[0]);
  }
}

