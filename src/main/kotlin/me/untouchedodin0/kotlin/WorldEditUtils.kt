package me.untouchedodin0.kotlin

import com.sk89q.worldedit.math.BlockVector3
import me.untouchedodin0.privatemines.utils.regions.CuboidRegion

class WorldEditUtils {

    fun toWorldEditCuboid(cuboidRegion: CuboidRegion): com.sk89q.worldedit.regions.CuboidRegion {
        val min = BlockVector3.at(
            cuboidRegion.minimumPoint.blockX,
            cuboidRegion.minimumPoint.blockY,
            cuboidRegion.minimumPoint.blockZ
        )
        val max = BlockVector3.at(
            cuboidRegion.maximumPoint.blockX,
            cuboidRegion.maximumPoint.blockY,
            cuboidRegion.maximumPoint.blockZ
        )
        return com.sk89q.worldedit.regions.CuboidRegion(min, max)
    }
}