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

package me.untouchedodin0.privatemines.iterator;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.config.Config;
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

        clipboard.getRegion().forEach(blockVector3 -> {
          BlockType blockType = clipboard.getBlock(blockVector3).getBlockType();
          int x = blockVector3.getX();
          int y = blockVector3.getY();
          int z = blockVector3.getZ();

          if (blockType.equals(cornerType)) {
            if (corner1 == null) {
              corner1 = BlockVector3.at(x, y, z);
            } else if (corner2 == null) {
              corner2 = BlockVector3.at(x, y, z);
            }
          } else if (blockType.equals(spawnType)) {
            if (spawn == null) {
              spawn = BlockVector3.at(x, y, z);
            }
          } else if (blockType.equals(npcType)) {
            if (npc == null) {
              npc = BlockVector3.at(x, y, z);
            }
          } else if (blockType.equals(quarryType)) {
            if (quarry == null) {
              quarry = BlockVector3.at(x, y, z);
            }
          }
        });

        if (spawn == null) {
          privateMines.getLogger().info(String.format("Failed to find a spawn block in the mine\nhave you placed a %s block?",
              Utils.format(spawnMaterial)));
          return null;
        } else if (corner1 == null) {
          privateMines.getLogger().info(String.format("Failed to find corner 1 in the mine\nhave you placed 2 %s blocks in the mining region?",
              Utils.format(cornerMaterial)));
          return null;
        } else if (corner2 == null) {
          privateMines.getLogger().info(String.format("Failed to find corner 2 in the mine\nhave you placed 2 %s blocks in the mining region?",
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

        spawn = null;
        npc = null;
        quarry = null;
        corner1 = null;
        corner2 = null;
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return mineBlocks;
  }

  public static class MineBlocks {

    public BlockVector3 spawnLocation;
    public BlockVector3 npcLocation;
    public BlockVector3 quarryLocation;
    public BlockVector3[] corners;

    public BlockVector3 getSpawnLocation() {
      return spawnLocation;
    }

    public BlockVector3 getNpcLocation() {
      return npcLocation;
    }

    public BlockVector3 getQuarryLocation() {
      return quarryLocation;
    }

    public BlockVector3 getCorner1() {
      return corners[0];
    }

    public BlockVector3 getCorner2() {
      return corners[1];
    }
  }
}
