package me.untouchedodin0.privatemines.utils.worldedit;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import java.io.File;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.storage.SchematicStorage;
import me.untouchedodin0.privatemines.utils.worldedit.objects.PastedMine;
import org.bukkit.Location;
import org.bukkit.World;

public class PasteHelper {

  PrivateMines privateMines = PrivateMines.getPrivateMines();
  SchematicStorage schematicStorage = privateMines.getSchematicStorage();

  private PastedMine create(File file, Location location) {
    World world = location.getWorld();
    BlockVector3 blockVector3 = BukkitAdapter.asBlockVector(location);
    return new PastedMine(blockVector3)
        .setLocation(location)
        .setFile(file)
        .create();

//    BlockVector3 pasted = pastedMine.getPasteLocation();
//    BlockVector3 lowerRails = blockVector3.subtract(pastedMine.getPasteLocation())
//        .add(pastedMine.getLowerRails().add(0, 0, 1));
//    BlockVector3 upperRails = blockVector3.subtract(pastedMine.getPasteLocation())
//        .add(pastedMine.getUpperRails().add(0, 0, 1));
//    Location spawn = new Location(world, pasted.getBlockX(), pasted.getBlockY(),
//        pasted.getBlockZ());
//    Location corner1 = new Location(world, lowerRails.getBlockX(), lowerRails.getBlockY(),
//        lowerRails.getBlockZ());
//    Location corner2 = new Location(world, upperRails.getBlockX(), upperRails.getBlockY(),
//        upperRails.getBlockZ());

//    pastedMine.setLocation(spawn);
//    pastedMine.setUpperRails(corner1);
//    pastedMine.setLowerRails(corner2);
//    return pastedMine.create();
  }

  public PastedMine paste(File file, Location location) {
    PastedMine pastedMine = create(file, location);
    return pastedMine;
  }
}
