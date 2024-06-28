package me.untouchedodin0.privatemines.utils.regions;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;

public class RegionUtils {

  public static com.sk89q.worldedit.regions.Region[] getSubRegions(
      com.sk89q.worldedit.regions.Region region) {
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
}
