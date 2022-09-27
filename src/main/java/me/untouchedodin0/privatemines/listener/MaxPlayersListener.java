package me.untouchedodin0.privatemines.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.untouchedodin0.kotlin.mine.data.MineData;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.kotlin.utils.ProtectionUtils;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mine.Mine;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.concurrent.atomic.AtomicInteger;

public class MaxPlayersListener implements Listener {

    PrivateMines privateMines = PrivateMines.getPrivateMines();
    MineStorage mineStorage = privateMines.getMineStorage();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Location location = event.getBlock().getLocation();
        AtomicInteger count = new AtomicInteger();

        Mine mine = mineStorage.getClosest(location);
        if (mine != null) {

            ProtectedRegion protectedRegion = ProtectionUtils.INSTANCE.getFirstRegion(location);

            if (protectedRegion != null) {
                for (Player player1 : Bukkit.getOnlinePlayers()) {
                    BlockVector3 blockVector3 = BukkitAdapter.asBlockVector(player1.getLocation());
                    if (protectedRegion.contains(blockVector3)) {
                        count.incrementAndGet();
                    }
                }
            }

            MineData mineData = mine.getMineData();
            MineType mineType = mineData.getMineType();

            int maxPlayers = mineType.getMaxPlayers();

            if (count.get() > maxPlayers) {
                player.sendMessage(ChatColor.RED + "I'm sorry, this mine is full!");
                event.setCancelled(true);
            }
        }
    }
}
