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

package me.untouchedodin0.privatemines.factory;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import me.untouchedodin0.kotlin.mine.data.MineData;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.config.Config;
import me.untouchedodin0.privatemines.events.PrivateMineCreationEvent;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.storage.sql.SQLUtils;
import me.untouchedodin0.privatemines.utils.worldedit.PasteHelper;
import me.untouchedodin0.privatemines.utils.worldedit.objects.PastedMine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import redempt.redlib.misc.Task;

public class MineFactory {

  PrivateMines privateMines = PrivateMines.getPrivateMines();
  MineStorage mineStorage = privateMines.getMineStorage();

  /**
   * Creates a mine for the {@link Player} at {@link Location} with {@link MineType}
   *
   * @param player   the player the mine should be created for
   * @param location the location of the mine
   * @param mineType the type of mine to paste
   */
  public void create(Player player, Location location, MineType mineType) {
    UUID uuid = player.getUniqueId();
    File schematicFile = new File("plugins/PrivateMines/schematics/" + mineType.getFile());
    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    RegionManager regionManager = container.get(BukkitAdapter.adapt(location.getWorld()));

    Map<Material, Double> materials = mineType.getMaterials();
    if (materials != null) {
      Map<Material, Double> prices = new HashMap<>(materials);
    }

    if (!schematicFile.exists()) {
      privateMines.getLogger().warning("Schematic file does not exist: " + schematicFile.getName());
      return;
    }

    String mineRegionName = String.format("mine-%s", player.getUniqueId());
    String fullRegionName = String.format("full-mine-%s", player.getUniqueId());

    Task.asyncDelayed(() -> {
      PasteHelper pasteHelper = new PasteHelper();
      PastedMine pastedMine = pasteHelper.paste(schematicFile, location);

      Location spawn = location.clone().add(0, 0, 1);
      Location corner1 = pastedMine.getLowerRailsLocation();
      Location corner2 = pastedMine.getUpperRailsLocation();
      Location minimum = pasteHelper.getMinimum();
      Location maximum = pasteHelper.getMaximum();
      BlockVector3 miningRegionMin = BukkitAdapter.asBlockVector(corner1);
      BlockVector3 miningRegionMax = BukkitAdapter.asBlockVector(corner2);
      BlockVector3 fullRegionMin = BukkitAdapter.asBlockVector(minimum);
      BlockVector3 fullRegionMax = BukkitAdapter.asBlockVector(maximum);

      ProtectedCuboidRegion miningRegion = new ProtectedCuboidRegion(mineRegionName, miningRegionMin, miningRegionMax);
      ProtectedCuboidRegion fullRegion = new ProtectedCuboidRegion(fullRegionName, fullRegionMin, fullRegionMax);

      if (regionManager != null) {
        regionManager.addRegion(miningRegion);
        regionManager.addRegion(fullRegion);
      }

      Mine mine = new Mine(privateMines);
      MineData mineData = new MineData(uuid, corner2, corner1, minimum, maximum, location, spawn,
          mineType, false, 5.0);
      if (!Config.defaultClosed) {
        mineData.setOpen(true);
      }
      mine.setMineData(mineData);
      SQLUtils.insert(mine);
      mineStorage.addMine(uuid, mine);
      mine.handleReset();
      Task.syncDelayed(() -> {
        spawn.getBlock().setType(Material.AIR);
        player.teleport(spawn);

        PrivateMineCreationEvent creationEvent = new PrivateMineCreationEvent(uuid, mine);
        Bukkit.getPluginManager().callEvent(creationEvent);
      });
    });
  }

  public Mine createMine(Player player, Location location, MineType mineType) {
    Mine mine = new Mine(privateMines);
    UUID uuid = player.getUniqueId();
    File schematicFile = new File("plugins/PrivateMines/schematics/" + mineType.getFile());
    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    RegionManager regionManager = container.get(BukkitAdapter.adapt(location.getWorld()));

    Map<Material, Double> materials = mineType.getMaterials();
    if (materials != null) {
      Map<Material, Double> prices = new HashMap<>(materials);
    }

    if (!schematicFile.exists()) {
      privateMines.getLogger().warning("Schematic file does not exist: " + schematicFile.getName());
      return null;
    }

    String mineRegionName = String.format("mine-%s", player.getUniqueId());
    String fullRegionName = String.format("full-mine-%s", player.getUniqueId());

    Task.asyncDelayed(() -> {
      PasteHelper pasteHelper = new PasteHelper();
      PastedMine pastedMine = pasteHelper.paste(schematicFile, location);

      Location spawn = location.clone().add(0, 0, 1);
      Location corner1 = pastedMine.getLowerRailsLocation();
      Location corner2 = pastedMine.getUpperRailsLocation();
      Location minimum = pasteHelper.getMinimum();
      Location maximum = pasteHelper.getMaximum();
      BlockVector3 miningRegionMin = BukkitAdapter.asBlockVector(corner1);
      BlockVector3 miningRegionMax = BukkitAdapter.asBlockVector(corner2);
      BlockVector3 fullRegionMin = BukkitAdapter.asBlockVector(minimum);
      BlockVector3 fullRegionMax = BukkitAdapter.asBlockVector(maximum);

      ProtectedCuboidRegion miningRegion = new ProtectedCuboidRegion(mineRegionName, miningRegionMin, miningRegionMax);
      ProtectedCuboidRegion fullRegion = new ProtectedCuboidRegion(fullRegionName, fullRegionMin, fullRegionMax);

      if (regionManager != null) {
        regionManager.addRegion(miningRegion);
        regionManager.addRegion(fullRegion);
      }

      MineData mineData = new MineData(uuid, corner2, corner1, minimum, maximum, location, spawn,
          mineType, false, 5.0);
      if (!Config.defaultClosed) {
        mineData.setOpen(true);
      }
      mine.setMineData(mineData);
      SQLUtils.insert(mine);
      mineStorage.addMine(uuid, mine);
      mine.handleReset();
      Task.syncDelayed(() -> {
        spawn.getBlock().setType(Material.AIR);
        player.teleport(spawn);

        PrivateMineCreationEvent creationEvent = new PrivateMineCreationEvent(uuid, mine);
        Bukkit.getPluginManager().callEvent(creationEvent);
      });
    });
    return mine;
  }

  public void createUpgraded(UUID uuid, Location location, MineType mineType) {
    MineStorage mineStorage = privateMines.getMineStorage();
    if (mineStorage.hasMine(uuid)) {
      Mine mine = createMine(Objects.requireNonNull(Bukkit.getPlayer(uuid)), location, mineType);
      mineStorage.replaceMine(uuid, mine);
      SQLUtils.replace(mine);

      PrivateMineCreationEvent creationEvent = new PrivateMineCreationEvent(uuid, mine);
      Bukkit.getPluginManager().callEvent(creationEvent);
    }
  }
}
