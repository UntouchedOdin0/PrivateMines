package me.untouchedodin0.privatemines.iterator;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;

public class BlockVisitor {

  private final Clipboard clipboard;
  private final BlockType cornerType;
  private final BlockType spawnType;
  private final BlockType npcType;
  private final BlockType quarryType;

  private BlockVector3 corner1;
  private BlockVector3 corner2;
  private BlockVector3 spawn;
  private BlockVector3 npc;
  private BlockVector3 quarry;

  public BlockVisitor(Clipboard clipboard, BlockType cornerType, BlockType spawnType,
      BlockType npcType, BlockType quarryType) {
    this.clipboard = clipboard;
    this.cornerType = cornerType;
    this.spawnType = spawnType;
    this.npcType = npcType;
    this.quarryType = quarryType;
  }

  public void visit(BlockVector3 blockVector3) {
    BlockType blockType = clipboard.getBlock(blockVector3).getBlockType();
    int x = blockVector3.getX();
    int y = blockVector3.getY();
    int z = blockVector3.getZ();

    if (blockType.equals(cornerType)) {
      if (corner1 == null) {
        this.corner1 = BlockVector3.at(x, y, z);
      } else if (corner2 == null) {
        if (x == corner1.getX()) return;
        this.corner2 = BlockVector3.at(x, y, z);
      }
    } else if (blockType.equals(spawnType)) {
      if (spawn == null) {
        this.spawn = BlockVector3.at(x, y, z);
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
  }

  public BlockVector3 getSpawn() {
    return spawn;
  }

  public BlockVector3 getCorner1() {
    return corner1;
  }

  public BlockVector3 getCorner2() {
    return corner2;
  }
}
