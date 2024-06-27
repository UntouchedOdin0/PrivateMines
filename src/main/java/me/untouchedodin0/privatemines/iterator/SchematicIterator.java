package me.untouchedodin0.privatemines.iterator;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import com.sk89q.worldedit.world.block.BlockType;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.config.Config;
import me.untouchedodin0.privatemines.iterator.SchematicIteratorOriginal.MineBlocks;
import me.untouchedodin0.privatemines.storage.SchematicStorage;
import me.untouchedodin0.privatemines.utils.Utils;
import org.bukkit.Material;

public class SchematicIterator {

  SchematicStorage schematicStorage;
  BlockVector3 spawn;
  BlockVector3 npc;
  BlockVector3 quarry;
  BlockVector3 corner1;
  BlockVector3 corner2;

  public SchematicIterator(SchematicStorage storage) {
    this.schematicStorage = storage;
  }

  public MineBlocks findRelativePoints(File file) {
    PrivateMines privateMines = PrivateMines.getPrivateMines();
    MineBlocks mineBlocks = new MineBlocks();
    mineBlocks.corners = new BlockVector3[2];

    Clipboard clipboard;
    ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(file);

    if (clipboardFormat != null) {
      try (ClipboardReader clipboardReader = clipboardFormat.getReader(new FileInputStream(file))) {
        clipboard = clipboardReader.read();

        Material cornerMaterial = Config.mineCorner;
        Material spawnMaterial = Config.spawnPoint;
        Material npcMaterial = Config.sellNpc;
        Material quarryMaterial = Config.quarryMaterial;

        BlockType cornerType = BlockType.REGISTRY.get(cornerMaterial.getKey().getKey());
        BlockType spawnType = BlockType.REGISTRY.get(spawnMaterial.getKey().getKey());
        BlockType npcType = BlockType.REGISTRY.get(npcMaterial.getKey().getKey());
        BlockType quarryType = BlockType.REGISTRY.get(quarryMaterial.getKey().getKey());

        Region region = clipboard.getRegion();
        Region[] subRegions = getSubRegions(region);

        ExecutorService executorService = Executors.newFixedThreadPool(Config.schematicThreads);
        CompletionService<BlockVisitor> completionService = new ExecutorCompletionService<>(
            executorService);

        for (Region subRegion : subRegions) {
          privateMines.getLogger().info("Visiting Sub-Region: " + subRegion);
          completionService.submit(() -> {
            BlockVisitor blockVisitor = new BlockVisitor(clipboard, cornerType, spawnType, npcType,
                quarryType);
            subRegion.forEach(blockVisitor::visit);
            return blockVisitor;
          });
        }

        if (executorService instanceof ThreadPoolExecutor threadPoolExecutor) {
          privateMines.getLogger().info("I'm using " + threadPoolExecutor.getActiveCount() + " Threads to iterate the schematic");
        }

        executorService.shutdown();

        int cornerCount = 0;
        for (int i = 0; i < subRegions.length; i++) {
          Future<BlockVisitor> future = completionService.take();
          BlockVisitor blockVisitor = future.get();

          privateMines.getLogger().info("Sub-Region results: " + blockVisitor);

          if (spawn == null && blockVisitor.getSpawn() != null) {
            spawn = blockVisitor.getSpawn();
          }

          if (blockVisitor.getCorner1() != null) {
            if (corner1 == null) {
              corner1 = blockVisitor.getCorner1();
              cornerCount++;
            } else if (corner2 == null) {
              corner2 = blockVisitor.getCorner1();
              cornerCount++;
            }
          }
          if (blockVisitor.getCorner2() != null) {
            if (cornerCount == 1 && !blockVisitor.getCorner2().equals(corner1)) {
              corner2 = blockVisitor.getCorner2();
              cornerCount++;
            } else if (cornerCount == 0 && corner1 == null) {
              corner1 = blockVisitor.getCorner2();
              cornerCount++;
            }
          }
        }

        if (spawn == null) {
          privateMines.getLogger().info(
              String.format("Failed to find a spawn block in the mine\nhave you placed a %s block?",
                  Utils.format(spawnMaterial)));
          return null;
        } else if (corner1 == null) {
          privateMines.getLogger().info(String.format(
              "Failed to find corner 1 in the mine\nhave you placed 2 %s blocks in the mining region?",
              Utils.format(cornerMaterial)));
          return null;
        } else if (corner2 == null) {
          privateMines.getLogger().info(String.format(
              "Failed to find corner 2 in the mine\nhave you placed 2 %s blocks in the mining region?",
              Utils.format(cornerMaterial)));
          return null;
        }

        mineBlocks.spawnLocation = BlockVector3.at(spawn.getX(), spawn.getY(), spawn.getZ());
        if (npc != null) {
          mineBlocks.npcLocation = BlockVector3.at(npc.getX(), npc.getY(), npc.getZ());
        }
        if (quarry != null) {
          mineBlocks.quarryLocation = BlockVector3.at(quarry.getX(), quarry.getY(), quarry.getZ());
        }
        mineBlocks.corners[0] = BlockVector3.at(corner1.getX(), corner1.getY(), corner1.getZ());
        mineBlocks.corners[1] = BlockVector3.at(corner2.getX(), corner2.getY(), corner2.getZ());

        privateMines.getLogger().info("spawn = " + spawn);
        privateMines.getLogger().info("npc = " + npc);
        privateMines.getLogger().info("quarry = " + quarry);
        privateMines.getLogger().info("corner1 = " + corner1);
        privateMines.getLogger().info("corner2 = " + corner2);

        // Reset the variables
        spawn = null;
        npc = null;
        quarry = null;
        corner1 = null;
        corner2 = null;
      } catch (IOException | InterruptedException | ExecutionException |
               RegionOperationException e) {
        privateMines.getLogger().log(Level.WARNING,
            "An error occurred whilst iterating the mine blocks in the schematic", e);
      }
    }
    return mineBlocks;
  }

  private Region[] getSubRegions(Region region) {
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
