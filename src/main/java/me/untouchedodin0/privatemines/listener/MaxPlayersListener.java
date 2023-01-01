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
