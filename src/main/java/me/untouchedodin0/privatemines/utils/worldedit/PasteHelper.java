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
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.storage.SchematicStorage;
import me.untouchedodin0.privatemines.utils.worldedit.objects.PastedMine;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public class PasteHelper {

  PrivateMines privateMines = PrivateMines.getPrivateMines();
  SchematicStorage schematicStorage = privateMines.getSchematicStorage();

  private PastedMine create(File file, Location location) {
    BlockVector3 blockVector3 = BukkitAdapter.asBlockVector(location);

//    BlockVector3 pasted = BukkitAdapter.asBlockVector(location);
//    BlockVector3 lowerRails = blockVector3.subtract(pasted)
//        .add(pastedMine.getLowerRails().add(0, 0, 1));
//    BlockVector3 upperRails = blockVector3.subtract(pastedMine.getPasteLocation())
//        .add(pastedMine.getUpperRails().add(0, 0, 1));
//    Location spawn = new Location(world, pasted.getBlockX(), pasted.getBlockY(),
//        pasted.getBlockZ());
//    Location corner1 = new Location(world, lowerRails.getBlockX(), lowerRails.getBlockY(),
//        lowerRails.getBlockZ());
//    Location corner2 = new Location(world, upperRails.getBlockX(), upperRails.getBlockY(),
//        upperRails.getBlockZ());

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

    ClipboardFormat format = ClipboardFormats.findByFile(file);
    if (format != null) {
      try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
        clipboard = reader.read();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }

      Bukkit.broadcastMessage("" + to);

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

    Bukkit.broadcastMessage("spawn " + spawn);
    Bukkit.broadcastMessage("upper rails " + upperRails);
    Bukkit.broadcastMessage("lower rails " + lowerRails);

    return pastedMine;
  }
}
