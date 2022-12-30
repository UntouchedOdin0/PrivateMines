package me.untouchedodin0.kotlin.utils

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import com.sk89q.worldguard.protection.ApplicableRegionSet
import com.sk89q.worldguard.protection.flags.Flags
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.annotations.NotNull

object ProtectionUtils {
    fun canBuild(@NotNull player: Player?, @NotNull location: Location?): Boolean {
        val localPlayer = WorldGuardPlugin.inst().wrapPlayer(player)
        val query = WorldGuard.getInstance().platform.regionContainer.createQuery()
        return query.testState(BukkitAdapter.adapt(location), localPlayer, Flags.BLOCK_BREAK)
    }

    fun getFirstRegion(@NotNull location: Location?): ProtectedRegion? {
        return if (getProtectedRegion(location).regions.stream().findFirst().isPresent) {
            getProtectedRegion(location).regions.stream().findFirst().get()
        } else null
    }

    fun getMine(location: Location?): ProtectedRegion {
        return getProtectedRegion(location).regions.stream()
            .filter { protectedRegion: ProtectedRegion ->
                protectedRegion.id.startsWith("mine-")
            }.findFirst().orElseThrow()
    }

    fun isMine(protectedRegion: ProtectedRegion): Boolean {
        return protectedRegion.id.startsWith("mine-")
    }

    private fun getProtectedRegion(@NotNull location: Location?): ApplicableRegionSet {
        val regionContainer = WorldGuard.getInstance().platform.regionContainer
        val query = regionContainer.createQuery()
        return query.getApplicableRegions(BukkitAdapter.adapt(location))
    }
}