package me.untouchedodin0.privatemines.listener;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import me.untouchedodin0.kotlin.mine.data.MineData;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.kotlin.utils.ProtectionUtils;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.utils.world.MineWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class MaxPlayersListener implements Listener {

  PrivateMines privateMines = PrivateMines.getPrivateMines();
  MineStorage mineStorage = privateMines.getMineStorage();
  MineWorldManager mineWorldManager = privateMines.getMineWorldManager();

  @EventHandler
  public void onBlockBreak(BlockBreakEvent event) {
    Player player = event.getPlayer();
    Location location = event.getBlock().getLocation();
    AtomicInteger count = new AtomicInteger();

    if (!Objects.equals(location.getWorld(), mineWorldManager.getMinesWorld())) {
      return;
    }

    Mine mine = mineStorage.getClosest(location);
    if (mine != null) {
      MineData mineData = mine.getMineData();
      MineType mineType = mineData.getMineType();

      if (mineData.getMineOwner().equals(player.getUniqueId())) {
        return;
      }

      ProtectedRegion protectedRegion = ProtectionUtils.INSTANCE.getFirstRegion(location);

      if (protectedRegion != null) {
        for (Player player1 : Bukkit.getOnlinePlayers()) {
          BlockVector3 blockVector3 = BukkitAdapter.asBlockVector(player1.getLocation());
          if (protectedRegion.contains(blockVector3)) {
            if (player.getUniqueId() != mineData.getMineOwner()) {
              count.incrementAndGet();
            }
          }
        }
      }

      int maxPlayers = mineType.getMaxPlayers();

      if (count.get() > maxPlayers && !player.getUniqueId().equals(mineData.getMineOwner())) {
        player.sendMessage(ChatColor.RED + "I'm sorry, this mine is full!");
        player.performCommand("spawn");
        event.setCancelled(true);
      }
    }
  }
}
