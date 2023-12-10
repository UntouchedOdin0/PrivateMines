package me.untouchedodin0.privatemines.factory;

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
import java.util.Objects;
import me.untouchedodin0.kotlin.mine.pregen.PregenMine;
import me.untouchedodin0.kotlin.mine.storage.PregenStorage;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.iterator.SchematicIterator;
import me.untouchedodin0.privatemines.mine.MineTypeManager;
import me.untouchedodin0.privatemines.storage.SchematicStorage;
import me.untouchedodin0.privatemines.storage.sql.SQLUtils;
import me.untouchedodin0.privatemines.utils.world.MineWorldManager;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import redempt.redlib.misc.Task;

public class PregenFactory {

  public static PrivateMines privateMines = PrivateMines.getPrivateMines();
  public static MineTypeManager mineTypeManager = privateMines.getMineTypeManager();
  public static MineWorldManager mineWorldManager = privateMines.getMineWorldManager();
  private static Clipboard clipboard;
  private static ClipboardHolder clipboardHolder;

  public static void pregen(Player player, int amount) {
    Location location;
    MineType mineType = mineTypeManager.getDefaultMineType();
    File schematicFile = new File("plugins/PrivateMines/schematics/" + mineType.getFile());
    PregenStorage pregenStorage = privateMines.getPregenStorage();

    location = player.getLocation();
    location.getBlock().setType(Material.GLOWSTONE);

    new BukkitRunnable() {
      private int i = 0;

      @Override
      public void run() {
        if (i == amount) {
          cancel();
        } else {
          i++;
          PregenMine pregenMine = new PregenMine();
          ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(schematicFile);
          BlockVector3 vector = BlockVector3.at(location.getBlockX(), location.getBlockY(),
              location.getBlockZ());
          SchematicStorage storage = privateMines.getSchematicStorage();
          SchematicIterator.MineBlocks mineBlocks = storage.getMineBlocksMap().get(schematicFile);
          if (clipboardFormat != null) {
            try (ClipboardReader clipboardReader = clipboardFormat.getReader(
                new FileInputStream(schematicFile))) {
              World world = BukkitAdapter.adapt(location.getWorld());
              try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder()
                  .world(world).fastMode(true).build()) {
                clipboard = clipboardReader.read();
                clipboardHolder = new ClipboardHolder(clipboard);

                Operation operation = clipboardHolder.createPaste(editSession).to(vector)
                    .ignoreAirBlocks(true).build();
                Operations.complete(operation);
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          }

          location.add(0, 0, mineWorldManager.getBorderDistance());
          Chunk chunk = location.getChunk();
          Task.syncDelayed(() -> chunk.load(true));

          BlockVector3 lrailsV = vector.subtract(mineBlocks.getSpawnLocation())
              .add(mineBlocks.getCorner2().add(0, 0, 1));
          BlockVector3 urailsV = vector.subtract(mineBlocks.getSpawnLocation())
              .add(mineBlocks.getCorner1().add(0, 0, 1));

          Region region = clipboard.getRegion();
          Region newRegion;

          BlockVector3 clipboardOffset = clipboard.getRegion().getMinimumPoint()
              .subtract(clipboard.getOrigin());
          Vector3 realTo = vector.toVector3()
              .add(clipboardHolder.getTransform().apply(clipboardOffset.toVector3()));
          Vector3 max = realTo.add(clipboardHolder.getTransform()
              .apply(region.getMaximumPoint().subtract(region.getMinimumPoint()).toVector3()));
          RegionSelector regionSelector = new CuboidRegionSelector(
              BukkitAdapter.adapt(location.getWorld()), realTo.toBlockPoint(), max.toBlockPoint());
          newRegion = regionSelector.getRegion();

          Location spongeL = new Location(location.getWorld(), vector.getBlockX(),
              vector.getBlockY(), vector.getBlockZ() + 1);
          Location lrailsL = new Location(location.getWorld(), lrailsV.getBlockX(),
              lrailsV.getBlockY(), lrailsV.getBlockZ());
          Location urailsL = new Location(location.getWorld(), urailsV.getBlockX(),
              urailsV.getBlockY(), urailsV.getBlockZ());
          Location fullMin = BukkitAdapter.adapt(Objects.requireNonNull(location.getWorld()),
              newRegion.getMinimumPoint());
          Location fullMax = BukkitAdapter.adapt(location.getWorld(), newRegion.getMaximumPoint());
          pregenMine.setLocation(location);
          pregenMine.setSpawnLocation(spongeL);
          pregenMine.setLowerRails(lrailsL);
          pregenMine.setUpperRails(urailsL);
          pregenMine.setFullMin(fullMin);
          pregenMine.setFullMax(fullMax);

          SQLUtils.insertPregen(pregenMine);
          pregenStorage.addMine(pregenMine);

          Task.syncDelayed(() -> spongeL.getBlock().setType(Material.AIR, false));
        }
      }
    }.runTaskTimerAsynchronously(privateMines, 20L, 20L);
  }
}

//    BukkitRunnable bukkitRunnable = new BukkitRunnable() {
//      int i = 0;
//
//      @Override
//      public void run() {
//        i++;
//        Bukkit.broadcastMessage("We're at #" + i);
//
//        if (i == 5) {
//          cancel();
//        }
//      }
//    };

//    bukkitRunnable.run();
//    task = Task.asyncRepeating(() -> {
//      PregenMine pregenMine = new PregenMine();
//      ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(schematicFile);
//      BlockVector3 vector = BlockVector3.at(location.getBlockX(), location.getBlockY(),
//          location.getBlockZ());
//      SchematicStorage storage = privateMines.getSchematicStorage();
//      SchematicIterator.MineBlocks mineBlocks = storage.getMineBlocksMap().get(schematicFile);
//      if (clipboardFormat != null) {
//        try (ClipboardReader clipboardReader = clipboardFormat.getReader(new FileInputStream(schematicFile))) {
//          World world = BukkitAdapter.adapt(location.getWorld());
//          try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder()
//              .world(world).fastMode(true).build()) {
//            clipboard = clipboardReader.read();
//            clipboardHolder = new ClipboardHolder(clipboard);
//
//            Operation operation = clipboardHolder.createPaste(editSession).to(vector).ignoreAirBlocks(true).build();
//            Operations.complete(operation);
//          } catch (IOException e) {
//            throw new RuntimeException(e);
//          }
//        } catch (IOException e) {
//          throw new RuntimeException(e);
//        }
//      }
//
//      location.add(0, 0, 100);
//      Chunk chunk = location.getChunk();
//      Task.syncDelayed(() -> chunk.load(true));
//
//      BlockVector3 lrailsV = vector.subtract(mineBlocks.getSpawnLocation())
//          .add(mineBlocks.getCorner2().add(0, 0, 1));
//      BlockVector3 urailsV = vector.subtract(mineBlocks.getSpawnLocation())
//          .add(mineBlocks.getCorner1().add(0, 0, 1));
//
//      Region region = clipboard.getRegion();
//      Region newRegion;
//
//      BlockVector3 clipboardOffset = clipboard.getRegion().getMinimumPoint()
//          .subtract(clipboard.getOrigin());
//      Vector3 realTo = vector.toVector3()
//          .add(clipboardHolder.getTransform().apply(clipboardOffset.toVector3()));
//      Vector3 max = realTo.add(clipboardHolder.getTransform()
//          .apply(region.getMaximumPoint().subtract(region.getMinimumPoint()).toVector3()));
//      RegionSelector regionSelector = new CuboidRegionSelector(BukkitAdapter.adapt(location.getWorld()),
//          realTo.toBlockPoint(),
//          max.toBlockPoint());
//      newRegion = regionSelector.getRegion();
//
//      Location spongeL = new Location(location.getWorld(), vector.getBlockX(),
//          vector.getBlockY(), vector.getBlockZ() + 1);
//      Location lrailsL = new Location(location.getWorld(), lrailsV.getBlockX(),
//          lrailsV.getBlockY(), lrailsV.getBlockZ());
//      Location urailsL = new Location(location.getWorld(), urailsV.getBlockX(),
//          urailsV.getBlockY(), urailsV.getBlockZ());
//      Location fullMin = BukkitAdapter.adapt(Objects.requireNonNull(location.getWorld()),
//          newRegion.getMinimumPoint());
//      Location fullMax = BukkitAdapter.adapt(location.getWorld(),
//          newRegion.getMaximumPoint());
//
//      Bukkit.broadcastMessage("lraisv " + lrailsV);
//      Bukkit.broadcastMessage("urailsV " + urailsV);
//      Bukkit.broadcastMessage("spongeL " + LocationUtils.toString(spongeL));
//      Bukkit.broadcastMessage("lrailsL " + LocationUtils.toString(lrailsL));
//      Bukkit.broadcastMessage("urailsL " + LocationUtils.toString(urailsL));
//
//      pregenMine.setLocation(location);
//      pregenMine.setSpawnLocation(spongeL);
//      pregenMine.setLowerRails(lrailsL);
//      pregenMine.setUpperRails(urailsL);
//      pregenMine.setFullMin(fullMin);
//      pregenMine.setFullMax(fullMax);
//
//      SQLUtils.insertPregen(pregenMine);
//    }, 20, 60);
//
//    Task.syncDelayed(task::cancel, amount * 100L);
