package me.untouchedodin0.privatemines.utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Chunk;
import org.bukkit.World;

public class ChunkUtils {

  public static Collection<Chunk> around(Chunk origin, int radius) {
    World world = origin.getWorld();

    int length = (radius * 2) + 1;
    Set<Chunk> chunks = new HashSet<>(length * length);

    int cX = origin.getX();
    int cZ = origin.getZ();

    for (int x = -radius; x <= radius; x++) {
      for (int z = -radius; z <= radius; z++) {
        chunks.add(world.getChunkAt(cX + x, cZ + z));
      }
    }
    return chunks;
  }
}
