package me.untouchedodin0.kotlin

import com.sk89q.worldedit.WorldEdit
import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.function.pattern.RandomPattern
import com.sk89q.worldedit.regions.CuboidRegion
import com.sk89q.worldedit.world.World
import org.bukkit.Material


class WorldEditUtils {

    fun fillRegion(cuboidRegion: CuboidRegion, world: World) {
        val editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).build()
        val pattern = RandomPattern() // Create the random pattern
        val stone: com.sk89q.worldedit.world.block.BlockState? = BukkitAdapter.adapt(Material.STONE.createBlockData())
        pattern.add(stone, 1.0)
        editSession.setBlocks(cuboidRegion, pattern)
    }
}