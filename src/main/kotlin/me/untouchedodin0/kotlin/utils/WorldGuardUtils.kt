package me.untouchedodin0.kotlin.utils

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.regions.CuboidRegion
import org.bukkit.World
import org.bukkit.entity.Player
import org.codemc.worldguardwrapper.WorldGuardWrapper
import org.codemc.worldguardwrapper.region.IWrappedRegion
import java.util.*


class WorldGuardUtils {

    fun createWorldGuardRegion(player: Player, cuboidRegion: CuboidRegion, world: World): IWrappedRegion? {
        val uuid: UUID = player.getUniqueId()
        val regionName = String.format("mine-%s", uuid)
        return WorldGuardWrapper.getInstance().addCuboidRegion(
            regionName,
            BukkitAdapter.adapt(world, cuboidRegion.minimumPoint),
            BukkitAdapter.adapt(world, cuboidRegion.maximumPoint)
        ).orElseThrow { RuntimeException("Could not create worldguard region named $regionName") }
    }
}