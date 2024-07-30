package me.untouchedodin0.privatemines.utils.worldedit.objects;

import com.sk89q.worldedit.math.BlockVector3;
import java.io.File;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.iterator.SchematicIteratorOriginal.MineBlocks;
import me.untouchedodin0.privatemines.storage.SchematicStorage;
import org.bukkit.Location;

public class PastedMine {

  PrivateMines privateMines = PrivateMines.getPrivateMines();
  SchematicStorage schematicStorage = privateMines.getSchematicStorage();
  File file;
  BlockVector3 pasteLocation;
  BlockVector3 lowerRails;
  BlockVector3 upperRails;
  Location location;

  public PastedMine(BlockVector3 pasteLocation) {
    this.pasteLocation = pasteLocation;
  }

  public PastedMine setLocation(Location location) {
    this.location = location;
    return this;
  }

  public PastedMine setFile(File file) {
    this.file = file;
    return this;
  }

  public PastedMine create() {
    MineBlocks mineBlocks = schematicStorage.get(file);
    BlockVector3 lowerRails = pasteLocation.subtract(mineBlocks.getSpawnLocation())
        .add(mineBlocks.getCorner2().add(0, 0, 1));
    BlockVector3 upperRails = pasteLocation.subtract(mineBlocks.getSpawnLocation())
        .add(mineBlocks.getCorner1().add(0, 0, 1));
    this.lowerRails = lowerRails;
    this.upperRails = upperRails;

    return this;
  }

  public Location getLocation() {
    return location;
  }

  public Location getLowerRailsLocation() {
    return new Location(location.getWorld(), lowerRails.getBlockX(), lowerRails.getBlockY(),
        lowerRails.getBlockZ());
  }

  public Location getUpperRailsLocation() {
    return new Location(location.getWorld(), upperRails.getBlockX(), upperRails.getBlockY(),
        upperRails.getBlockZ());
  }
}
