package me.untouchedodin0.privatemines.utils;

import com.sk89q.worldedit.math.BlockVector3;

import java.util.List;

public class ExpansionUtils {

    public static final List<BlockVector3> EXPANSION_VECTORS = List.of(BlockVector3.UNIT_X, BlockVector3.UNIT_MINUS_X,
                                                                       BlockVector3.UNIT_Z, BlockVector3.UNIT_MINUS_Z);
    public static BlockVector3[] expansionVectors(final int amount) {
        return EXPANSION_VECTORS.stream().map(it -> it.multiply(amount)).toArray(BlockVector3[]::new);
    }
}
