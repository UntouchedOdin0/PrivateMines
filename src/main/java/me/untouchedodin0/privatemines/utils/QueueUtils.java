package me.untouchedodin0.privatemines.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import me.untouchedodin0.kotlin.mine.data.MineData;
import me.untouchedodin0.kotlin.mine.pregen.PregenMine;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.kotlin.mine.storage.PregenStorage;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.mine.MineTypeManager;
import me.untouchedodin0.privatemines.storage.sql.SQLUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import redempt.redlib.misc.Task;

public class QueueUtils {

  PrivateMines privateMines = PrivateMines.getPrivateMines();
  MineTypeManager mineTypeManager = privateMines.getMineTypeManager();

  public Queue<UUID> queue = new LinkedList<>();
  public List<UUID> waitingInQueue = new ArrayList<>();

  public void add(UUID uuid) {
    if (!queue.contains(uuid)) {
      queue.add(uuid);
    }
  }

  public void claim(UUID uuid) {
    if (waitingInQueue.contains(uuid)) {
      return;
    }
    add(uuid);
  }

  public boolean isInQueue(UUID uuid) {
    return waitingInQueue.contains(uuid);
  }

  public void claim(Player player) {
    MineStorage mineStorage = privateMines.getMineStorage();
    String mineRegionName = String.format("mine-%s", player.getUniqueId());
    String fullRegionName = String.format("full-mine-%s", player.getUniqueId());

    PregenStorage pregenStorage = privateMines.getPregenStorage();

    if (mineStorage.hasMine(player)) {
      player.sendMessage(ChatColor.RED + "You already own a mine!");
      return;
    }

    if (queue.contains(player.getUniqueId())) {
      player.sendMessage(ChatColor.RED + "You're already in the queue!");
    }

    claim(player.getUniqueId());

    Task.syncRepeating(() -> {
      AtomicInteger slot = new AtomicInteger(1);
      List<UUID> uuidList = queue.stream().toList();

      for (UUID uuid : uuidList) {
        if (!uuid.equals(player.getUniqueId())) {
          slot.incrementAndGet();
        } else {
          AtomicInteger place = new AtomicInteger(1);
          for (UUID uuid1 : uuidList) {
            if (!uuid1.equals(player.getUniqueId())) {
              place.incrementAndGet();
            }
          }
          int estimateSeconds = place.get() * 3;

          player.sendTitle(ChatColor.GREEN + "You're at slot #" + slot.get(),
              ChatColor.YELLOW + String.format(" Estimated wait time: %d seconds!",
                  estimateSeconds));
        }
      }
    }, 0L, 60L);

    Task.syncRepeating(() -> {
      UUID poll = queue.poll();
      if (poll == null) {
        return;
      }
      if (poll.equals(player.getUniqueId())) {
        player.sendMessage(ChatColor.GREEN + "Creating your mine.....");

        PregenMine pregenMine = pregenStorage.getAndRemove();
        MineType mineType = mineTypeManager.getDefaultMineType();
        Location location = pregenMine.getLocation();
        Location spawn = pregenMine.getSpawnLocation();
        Location corner1 = pregenMine.getLowerRails();
        Location corner2 = pregenMine.getUpperRails();
        Location minimum = pregenMine.getFullMin();
        Location maximum = pregenMine.getFullMax();
        BlockVector3 miningRegionMin = BukkitAdapter.asBlockVector(Objects.requireNonNull(corner1));
        BlockVector3 miningRegionMax = BukkitAdapter.asBlockVector(Objects.requireNonNull(corner2));
        BlockVector3 fullRegionMin = BukkitAdapter.asBlockVector(Objects.requireNonNull(minimum));
        BlockVector3 fullRegionMax = BukkitAdapter.asBlockVector(Objects.requireNonNull(maximum));

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(
            BukkitAdapter.adapt(Objects.requireNonNull(spawn).getWorld()));

        ProtectedCuboidRegion miningRegion = new ProtectedCuboidRegion(mineRegionName,
            miningRegionMin, miningRegionMax);
        ProtectedCuboidRegion fullRegion = new ProtectedCuboidRegion(fullRegionName, fullRegionMin,
            fullRegionMax);

        if (regionManager != null) {
          regionManager.addRegion(miningRegion);
          regionManager.addRegion(fullRegion);
        }

        Mine mine = new Mine(privateMines);
        MineData mineData = new MineData(player.getUniqueId(), corner2, corner1, minimum, maximum,
            Objects.requireNonNull(location), spawn, mineType, false, 5.0);
        mine.setMineData(mineData);
        SQLUtils.claim(location);
        SQLUtils.insert(mine);

        mineStorage.addMine(player.getUniqueId(), mine);

        Task.syncDelayed(() -> spawn.getBlock().setType(Material.AIR, false));
        pregenMine.teleport(player);
        mine.handleReset();
//        mineFactory.create(player, location, defaultMineType);
      }
    }, 0L, 120L);
  }
}
