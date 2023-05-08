package me.untouchedodin0.kotlin.utils

import com.sk89q.worldedit.bukkit.BukkitAdapter
import com.sk89q.worldedit.world.World
import com.sk89q.worldguard.WorldGuard
import com.sk89q.worldguard.protection.flags.Flag
import com.sk89q.worldguard.protection.flags.FlagContext
import com.sk89q.worldguard.protection.flags.Flags
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat
import com.sk89q.worldguard.protection.regions.ProtectedRegion
import com.sk89q.worldguard.protection.regions.RegionContainer
import me.untouchedodin0.privatemines.PrivateMines
import me.untouchedodin0.privatemines.mine.Mine
import me.untouchedodin0.privatemines.utils.world.MineWorldManager
import redempt.redlib.misc.Task

class FlagUtils {

    val privateMines: PrivateMines = PrivateMines.getPrivateMines()
    val mineWorldManager: MineWorldManager = privateMines.mineWorldManager
    val world: World = BukkitAdapter.adapt(mineWorldManager.minesWorld)
    private var container: RegionContainer = WorldGuard.getInstance().platform.regionContainer
    private var regionManager = container.get(world)

    @Throws(InvalidFlagFormat::class)
    fun <V> setFlag(region: ProtectedRegion, flag: Flag<V>?, value: String?) {
        if (flag != null) {
            val `val` = flag.parseInput(
                FlagContext.create().setInput(value).setObject("region", region).build()
            )
            region.setFlag(flag, `val`)
        }
    }

    fun setFlags(mine: Mine) {
        val mineRegion =
            regionManager!!.getRegion(String.format("mine-%s", mine.mineData.mineOwner))
        val fullRegion =
            regionManager!!.getRegion(String.format("full-mine-%s", mine.mineData.mineOwner))

        Task.syncDelayed(Runnable {

            mine.mineData.mineType.flags?.forEach { (flag, boolean) ->
                run {
                    if (mineRegion != null) {
                        if (boolean) {
                            val fuzzyFlag =
                                Flags.fuzzyMatchFlag(WorldGuard.getInstance().flagRegistry, flag)

                            setFlag(mineRegion, fuzzyFlag, "allow")
                        } else {
                            val fuzzyFlag =
                                Flags.fuzzyMatchFlag(WorldGuard.getInstance().flagRegistry, flag)
                            setFlag(mineRegion, fuzzyFlag, "deny")
                        }
                    }
                }
            }

            mine.mineData.mineType.fullFlags?.forEach { (flag, boolean) ->
                run {
                    if (fullRegion != null) {
                        if (boolean) {
                            val fuzzyFlag =
                                Flags.fuzzyMatchFlag(WorldGuard.getInstance().flagRegistry, flag)

                            setFlag(fullRegion, fuzzyFlag, "allow")
                        } else {
                            val fuzzyFlag =
                                Flags.fuzzyMatchFlag(WorldGuard.getInstance().flagRegistry, flag)
                            setFlag(fullRegion, fuzzyFlag, "deny")
                        }
                    }
                }
            }
        })
    }
}