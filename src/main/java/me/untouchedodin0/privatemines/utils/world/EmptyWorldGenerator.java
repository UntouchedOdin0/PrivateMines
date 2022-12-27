/**
 * MIT License
 * <p>
 * Copyright (c) 2021 - 2022 Kyle Hicks
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

package me.untouchedodin0.privatemines.utils.world;

import java.util.Random;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;
import org.jetbrains.annotations.NotNull;

public class EmptyWorldGenerator extends ChunkGenerator {

  /**
   * Shapes the chunk for the given coordinates.
   * <p>
   * This method must return a ChunkData.
   * <p>
   * Notes:
   * <p>
   * This method should <b>never</b> attempt to get the Chunk at
   * the passed coordinates, as doing so may cause an infinite loop
   * <p>
   * This method should <b>never</b> modify a ChunkData after it has
   * been returned.
   * <p>
   * This method <b>must</b> return a ChunkData returned by {@link ChunkGenerator#createChunkData(World)}
   *
   * @param world  The world this chunk will be used for
   * @param random The random generator to use
   * @param x      The X-coordinate of the chunk
   * @param z      The Z-coordinate of the chunk
   * @param biome  Proposed biome values for chunk - can be updated by
   *               generator
   * @return ChunkData containing the types for each block created by this
   * generator
   */

  @Override
  @NotNull
  public ChunkData generateChunkData(@NotNull World world, @NotNull Random random, int x, int z,
      @NotNull BiomeGrid biome) {
    return createChunkData(world);
  }
}