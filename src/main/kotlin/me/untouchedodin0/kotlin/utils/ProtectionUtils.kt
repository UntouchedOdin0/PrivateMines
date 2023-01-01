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