package me.untouchedodin0.privatemines.listener;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import me.untouchedodin0.kotlin.mine.data.MineData;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mine.Mine;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.Objects;

public class MaxPlayersListener implements Listener {

    PrivateMines privateMines = PrivateMines.getPrivateMines();
    MineStorage mineStorage = privateMines.getMineStorage();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();

        player.sendMessage("private mines " + privateMines);
        player.sendMessage("storage: " + mineStorage);

        Mine mine = mineStorage.getClosest(location);
        if (mine != null) {
            ApplicableRegionSet set = Objects
                    .requireNonNull(WorldGuard.getInstance()
                            .getPlatform()
                            .getRegionContainer()
                            .get(new BukkitWorld(location.getWorld())))
                    .getApplicableRegions(BlockVector3.at(location.getX(),location.getY(),location.getZ()));

            MineData mineData = mine.getMineData();
            int maxPlayers = mineData.getMaxPlayers();

            player.sendMessage("mine: " + mine);
            player.sendMessage("mine data: " + mineData);
        }
    }
}
