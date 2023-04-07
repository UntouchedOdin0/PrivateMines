package me.untouchedodin0.privatemines.utils.worldedit;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import me.untouchedodin0.privatemines.utils.worldedit.objects.PastedMine;
import org.bukkit.Location;

public class PasteHelper {

  private Location spawn, corner1, corner2, minimum, maximum;
  Region newRegion;

  public void setSpawn(Location spawn) {
    this.spawn = spawn;
  }

  public void setCorner1(Location corner1) {
    this.corner1 = corner1;
  }

  public void setCorner2(Location corner2) {
    this.corner2 = corner2;
  }

  public void setMinimum(Location minimum) {
    this.minimum = minimum;
  }

  public Location getMinimum() {
    return minimum;
  }

  public void setMaximum(Location maximum) {
    this.maximum = maximum;
  }

  public Location getMaximum() {
    return maximum;
  }

  private PastedMine create(File file, Location location) {
    BlockVector3 blockVector3 = BukkitAdapter.asBlockVector(location);

    return new PastedMine(blockVector3)
        .setLocation(location)
        .setFile(file)
        .create();
  }

  public PastedMine paste(File file, Location location) {
    PastedMine pastedMine = create(file, location);
    BlockVector3 to = BukkitAdapter.asBlockVector(location);
    Location spawn = pastedMine.getLocation();
    Location upperRails = pastedMine.getUpperRailsLocation();
    Location lowerRails = pastedMine.getLowerRailsLocation();
    World world = BukkitAdapter.adapt(location.getWorld());
    Clipboard clipboard;
    ClipboardHolder clipboardHolder;

    ClipboardFormat format = ClipboardFormats.findByFile(file);
    if (format != null) {
      try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
        clipboard = reader.read();
        clipboardHolder = new ClipboardHolder(clipboard);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      Region region = clipboard.getRegion();

      BlockVector3 clipboardOffset = clipboard.getRegion().getMinimumPoint()
          .subtract(clipboard.getOrigin());
      Vector3 realTo = to.toVector3()
          .add(clipboardHolder.getTransform().apply(clipboardOffset.toVector3()));
      Vector3 max = realTo.add(clipboardHolder.getTransform()
          .apply(region.getMaximumPoint().subtract(region.getMinimumPoint()).toVector3()));
      RegionSelector regionSelector = new CuboidRegionSelector(world, realTo.toBlockPoint(),
          max.toBlockPoint());
      newRegion = regionSelector.getRegion();

      try (EditSession editSession = WorldEdit.getInstance().newEditSession(world)) {
        Operation operation = new ClipboardHolder(clipboard)
            .createPaste(editSession)
            .to(to)
            .ignoreAirBlocks(true)
            // configure here
            .build();
        Operations.complete(operation);
      }
    }

    Location fullMin = BukkitAdapter.adapt(BukkitAdapter.adapt(world),
        newRegion.getMinimumPoint());
    Location fullMax = BukkitAdapter.adapt(BukkitAdapter.adapt(world),
        newRegion.getMaximumPoint());

    setSpawn(spawn);
    setCorner1(upperRails);
    setCorner2(lowerRails);
    setMinimum(fullMin);
    setMaximum(fullMax);

    return pastedMine;
  }
}
