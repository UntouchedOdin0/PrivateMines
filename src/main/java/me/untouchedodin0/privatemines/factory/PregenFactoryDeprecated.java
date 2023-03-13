/**
 * MIT License
 * <p>
 * Copyright (c) 2021 - 2023 Kyle Hicks
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.untouchedodin0.privatemines.factory;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.adapter.UnsupportedVersionEditException;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import me.untouchedodin0.kotlin.mine.pregen.PregenMine;
import me.untouchedodin0.kotlin.mine.storage.PregenStorage;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.iterator.SchematicIterator;
import me.untouchedodin0.privatemines.mine.MineTypeManager;
import me.untouchedodin0.privatemines.storage.SchematicStorage;
import me.untouchedodin0.privatemines.utils.Utils;
import me.untouchedodin0.privatemines.utils.world.MineWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import redempt.redlib.misc.Task;

public class PregenFactoryDeprecated {

  PrivateMines privateMines = PrivateMines.getPrivateMines();
  EditSession editSession;
  MineWorldManager mineWorldManager = privateMines.getMineWorldManager();
  MineTypeManager mineTypeManager = privateMines.getMineTypeManager();
  PregenStorage pregenStorage = privateMines.getPregenStorage();

  List<Location> generatedLocations = new ArrayList<>();
  AtomicInteger generated = new AtomicInteger(1);


  public void generateLocations(int amount) {
    for (int i = 0; i < amount; i++) {
      generatedLocations.add(mineWorldManager.getNextFreeLocation());
    }
  }

  public void generate(Player player, int amount) {
    long start = System.currentTimeMillis();
    int correct = amount++;

    generateLocations(correct);
    MineType defaultType = mineTypeManager.getDefaultMineType();
    File schematicFile = new File("plugins/PrivateMines/schematics/" + defaultType.getFile());

    if (!schematicFile.exists()) {
      privateMines.getLogger().warning("Schematic file does not exist: " + schematicFile.getName());
      return;
    }

    ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(schematicFile);
    SchematicStorage schematicStorage = privateMines.getSchematicStorage();
    SchematicIterator.MineBlocks mineBlocks = schematicStorage.getMineBlocksMap()
        .get(schematicFile);

    for (Location location : generatedLocations) {
      Collection<Chunk> chunks = Utils.around(location.getChunk(), 25);

      chunks.forEach(chunk -> {
        chunk.load(true);
        chunk.setForceLoaded(true);
      });

      BlockVector3 vector = BlockVector3.at(location.getBlockX(), location.getBlockY(),
          location.getBlockZ());

      Task.asyncDelayed(() -> {
        if (clipboardFormat != null) {
          try (ClipboardReader clipboardReader = clipboardFormat.getReader(
              new FileInputStream(schematicFile))) {
            World world = BukkitAdapter.adapt(Objects.requireNonNull(location.getWorld()));
            if (Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit")) {
              editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world)
                  .fastMode(true).build();
            } else {
              editSession = WorldEdit.getInstance().newEditSession(world);
            }
            LocalSession localSession = new LocalSession();

            Clipboard clipboard = clipboardReader.read();
            ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);

//                    mb0|0,50,-150
//                    mb1|13,48,-144
//                    mb2|-12,20,-116
//                    cbo|0,50,-151
//                    min|-30000000,-64,-30000000
//                    loc|763.6692645437984,140.45037494032877,728.8705431310638
//
//                    763,140,729 Sponge
//                    751,110,763 Lower Rails Sponge - mb0 + mb2 -> 763 - 0 +-12 = 751
//                    776,138,735 Upper Rails Sponge - mb0 + mb1 -> 763 - 0 + 13 = 776

            BlockVector3 lrailsV = vector.subtract(mineBlocks.getSpawnLocation())
                .add(mineBlocks.getCorner2().add(0, 0, 1));
            BlockVector3 urailsV = vector.subtract(mineBlocks.getSpawnLocation())
                .add(mineBlocks.getCorner1().add(0, 0, 1));

            BlockVector3 clipboardOffset = clipboard.getRegion().getMinimumPoint()
                .subtract(clipboard.getOrigin());
            Region region = clipboard.getRegion();

            Vector3 min = vector.toVector3()
                .add(clipboardHolder.getTransform().apply(clipboardOffset.toVector3()));
            Vector3 max = min.add(clipboardHolder.getTransform()
                .apply(region.getMaximumPoint().subtract(region.getMinimumPoint()).toVector3()));

            Location spongeL = new Location(location.getWorld(), vector.getBlockX(),
                vector.getBlockY(), vector.getBlockZ() + 1);

            Location lrailsL = new Location(location.getWorld(), lrailsV.getBlockX(),
                lrailsV.getBlockY(), lrailsV.getBlockZ());
            Location urailsL = new Location(location.getWorld(), urailsV.getBlockX(),
                urailsV.getBlockY(), urailsV.getBlockZ());

            Location fullMinL = new Location(location.getWorld(), min.getBlockX(), min.getBlockY(),
                min.getBlockZ());
            Location fullMaxL = new Location(location.getWorld(), max.getBlockX(), max.getBlockY(),
                max.getBlockZ());

            PregenMine pregenMine = new PregenMine();

            pregenMine.setLocation(location);
            pregenMine.setSpawnLocation(spongeL);
            pregenMine.setLowerRails(lrailsL);
            pregenMine.setUpperRails(urailsL);
            pregenMine.setFullMin(fullMinL);
            pregenMine.setFullMax(fullMaxL);
//                        pregenMine.save();

            pregenStorage.addMine(pregenMine);

            localSession.setClipboard(clipboardHolder);

            Operation operation = clipboardHolder.createPaste(editSession).to(vector)
                .ignoreAirBlocks(true).build();

            try {
              Operations.complete(operation);
              editSession.close();
            } catch (WorldEditException worldEditException) {
              if (worldEditException.getCause() instanceof UnsupportedVersionEditException) {
                privateMines.getLogger().warning(
                    "WorldEdit version " + WorldEdit.getVersion() + " is not supported,"
                        + "if this issue persists, please try using FastAsyncWorldEdit.");
                return;
              }
              worldEditException.printStackTrace();
            }
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      });

      player.sendMessage(
          ChatColor.GREEN + "Finished Generating Mine #" + generated.getAndIncrement());
    }

    if (generated.get() == amount) {
      long finished = System.currentTimeMillis();
      long time = finished - start;

      long millis = time % 1000;
      long second = (time / 1000) % 60;
      long minute = (time / (1000 * 60)) % 60;
      long hour = (time / (1000 * 60 * 60)) % 24;
      String formatted = String.format("%02d:%02d:%02d.%d", hour, minute, second, millis);
      player.sendMessage(
          ChatColor.GREEN + String.format("Finished generating the mines in %s!", formatted));
    }
  }

  public void purge() {
    pregenStorage.getMines().clear();
  }

  public List<Location> getGeneratedLocations() {
    return generatedLocations;
  }
}


