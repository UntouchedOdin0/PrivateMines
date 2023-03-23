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

package me.untouchedodin0.privatemines.mine;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import dev.lone.itemsadder.api.CustomBlock;
import io.th0rgal.oraxen.api.OraxenBlocks;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import me.untouchedodin0.kotlin.mine.data.MineData;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.kotlin.utils.FlagUtils;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.config.Config;
import me.untouchedodin0.privatemines.events.PrivateMineDeleteEvent;
import me.untouchedodin0.privatemines.events.PrivateMineExpandEvent;
import me.untouchedodin0.privatemines.events.PrivateMineResetEvent;
import me.untouchedodin0.privatemines.events.PrivateMineUpgradeEvent;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.iterator.SchematicIterator;
import me.untouchedodin0.privatemines.storage.SchematicStorage;
import me.untouchedodin0.privatemines.storage.sql.SQLUtils;
import me.untouchedodin0.privatemines.utils.ExpansionUtils;
import me.untouchedodin0.privatemines.utils.Utils;
import me.untouchedodin0.privatemines.utils.world.MineWorldManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;
import redempt.redlib.misc.LocationUtils;
import redempt.redlib.misc.Task;
import redempt.redlib.misc.WeightedRandom;

public class Mine {

  private final PrivateMines privateMines;
  private BlockVector3 location;
  private MineData mineData;
  private boolean canExpand = true;
  private Task task;
  private Task percentageTask;
  private int airBlocks;

  public Mine(PrivateMines privateMines) {
    this.privateMines = privateMines;
  }

  public BlockVector3 getLocation() {
    return location;
  }

  public void setLocation(BlockVector3 location) {
    this.location = location;
  }

  public Location getSpawnLocation() {
    return mineData.getSpawnLocation().clone().add(0.5, 0.5, 0.5);
  }

  public MineData getMineData() {
    return mineData;
  }

  public void setMineData(MineData mineData) {
    this.mineData = mineData;
  }

  public void teleport(Player player) {
    if (getSpawnLocation().getBlock().getType().isBlock()) {
      getSpawnLocation().getBlock().setType(Material.AIR, false);
      player.teleport(getSpawnLocation());
    }
  }

  public void delete(boolean removeStructure) {
    UUID uuid = getMineData().getMineOwner();
    PrivateMineDeleteEvent privateMineDeleteEvent = new PrivateMineDeleteEvent(uuid, this);
    Bukkit.getPluginManager().callEvent(privateMineDeleteEvent);

    if (privateMineDeleteEvent.isCancelled()) {
      return;
    }

    switch (Config.storageType) {
      case YAML -> {
        String fileName = String.format("/%s.yml", uuid);
        File minesDirectory = privateMines.getMinesDirectory().toFile();
        File file = new File(minesDirectory + fileName);

        boolean delete = file.delete();
      }
      case SQLite -> SQLUtils.delete(this);
    }

    MineData mineData = getMineData();

    Location corner1 = mineData.getMinimumFullRegion();
    BlockVector3 corner1BV3 = BukkitAdapter.asBlockVector(mineData.getMinimumFullRegion());
    BlockVector3 corner2BV3 = BukkitAdapter.asBlockVector(mineData.getMaximumFullRegion());
    redempt.redlib.region.CuboidRegion cuboidRegion = new redempt.redlib.region.CuboidRegion(
        mineData.getMinimumMining(), mineData.getMaximumMining());

    String regionName = String.format("mine-%s", uuid);
    String fullRegionName = String.format("full-mine-%s", uuid);

    World world = corner1.getWorld();
    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    RegionManager regionManager = container.get(BukkitAdapter.adapt(Objects.requireNonNull(world)));
    Objects.requireNonNull(regionManager).removeRegion(regionName);
    Objects.requireNonNull(regionManager).removeRegion(fullRegionName);

    Instant start = Instant.now();

    final RandomPattern randomPattern = new RandomPattern();
    Pattern air = BukkitAdapter.adapt(Material.AIR.createBlockData());
    randomPattern.add(air, 1.0);

    if (removeStructure) {
      try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder()
          .world(BukkitAdapter.adapt(world)).fastMode(true).build()) {
        Region region = new CuboidRegion(BukkitAdapter.adapt(world), corner1BV3, corner2BV3);
        editSession.setBlocks(region, randomPattern);
      }

      cuboidRegion.forEachBlock(block -> {
        if (mineData.getMineType().getUseItemsAdder()) {
          CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
          if (customBlock != null) {
            customBlock.remove();
          }
        }
      });
      Instant filled = Instant.now();
      Duration durationToFill = Duration.between(start, filled);
      long durationInMS = TimeUnit.NANOSECONDS.toMillis(durationToFill.toNanos());

      privateMines.getLogger().info(String.format("It took %dms to delete the mine", durationInMS));
    }

    privateMines.getMineStorage().removeMine(uuid);
  }

  public void reset() {
    MineData mineData = getMineData();
    MineType mineType = mineData.getMineType();
    Location location = mineData.getMinimumMining();
    BlockVector3 corner1 = BukkitAdapter.asBlockVector(mineData.getMinimumMining());
    BlockVector3 corner2 = BukkitAdapter.asBlockVector(mineData.getMaximumMining());

    Map<Material, Double> materials = mineType.getMaterials();
    Map<Material, Double> mineBlocks = mineData.getMaterials();

    final RandomPattern randomPattern = new RandomPattern();

    PrivateMineResetEvent privateMineResetEvent = new PrivateMineResetEvent(mineData.getMineOwner(),
        this);
    Task.syncDelayed(() -> Bukkit.getPluginManager().callEvent(privateMineResetEvent));

    if (privateMineResetEvent.isCancelled()) {
      return;
    }

    if (!mineBlocks.isEmpty()) {
      mineBlocks.forEach((material, chance) -> {
        Pattern pattern = BukkitAdapter.adapt(material.createBlockData());
        randomPattern.add(pattern, chance);
      });
    } else {
      if (materials != null) {
        materials.forEach((material, chance) -> {
          Pattern pattern = BukkitAdapter.adapt(material.createBlockData());
          randomPattern.add(pattern, chance);
        });
      }
    }

    World world = location.getWorld();
    Region region = new CuboidRegion(BukkitAdapter.adapt(world), corner1, corner2);

    try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder()
        .world(BukkitAdapter.adapt(world)).fastMode(true).build()) {
      if (Config.addWallGap) {
        editSession.setBlocks(region, BukkitAdapter.adapt(Material.AIR.createBlockData()));
        for (int i = 0; i < Config.wallsGap; i++) {
          region.contract(ExpansionUtils.contractVectors(1));
        }
      }
      editSession.setBlocks(region, randomPattern);
      editSession.flushQueue();
    }
  }

  public void resetOraxen() {
    if (!Bukkit.getPluginManager().isPluginEnabled("Oraxen")) {
      privateMines.getLogger().warning("Failed to reset mine due to Oraxen plugin missing!");
      return;
    }
    MineData mineData = getMineData();
    MineType mineType = mineData.getMineType();
    Location location = mineData.getMinimumMining();
    BlockVector3 corner1 = BukkitAdapter.asBlockVector(mineData.getMinimumMining());
    BlockVector3 corner2 = BukkitAdapter.asBlockVector(mineData.getMaximumMining());
    Region region = new CuboidRegion(BukkitAdapter.adapt(location.getWorld()), corner1, corner2);
    if (Config.addWallGap) {
      for (int i = 0; i < Config.wallsGap; i++) {
        region.contract(ExpansionUtils.contractVectors(1));
      }
    }

    Map<Material, Double> materials = mineType.getMaterials();
    Map<Material, Double> mineBlocks = mineData.getMaterials();
    Map<String, Double> oraxenMaterials = mineType.getOraxen();
    WeightedRandom<String> weightedRandom = new WeightedRandom<>();

    if (oraxenMaterials != null) {
      oraxenMaterials.forEach(weightedRandom::set);
    }

    final RandomPattern randomPattern = new RandomPattern();

    PrivateMineResetEvent privateMineResetEvent = new PrivateMineResetEvent(mineData.getMineOwner(),
        this);
    Task.syncDelayed(() -> Bukkit.getPluginManager().callEvent(privateMineResetEvent));

    if (privateMineResetEvent.isCancelled()) {
      return;
    }

    if (!mineBlocks.isEmpty()) {
      mineBlocks.forEach((material, chance) -> {
        Pattern pattern = BukkitAdapter.adapt(material.createBlockData());
        randomPattern.add(pattern, chance);
      });
    } else {
      if (materials != null) {
        materials.forEach((material, chance) -> {
          Pattern pattern = BukkitAdapter.adapt(material.createBlockData());
          randomPattern.add(pattern, chance);
        });
      }
    }

    final MineWorldManager mineWorldManager = privateMines.getMineWorldManager();

    World world = location.getWorld();
    World privateMinesWorld = mineWorldManager.getMinesWorld();

    Player player = Bukkit.getPlayer(mineData.getMineOwner());
    if (player != null && player.isOnline()) {
      boolean isPlayerInRegion = region.contains(player.getLocation().getBlockX(),
          player.getLocation().getBlockY(), player.getLocation().getBlockZ());
      boolean inWorld = player.getWorld().equals(privateMinesWorld);

      if (isPlayerInRegion && inWorld) {
        teleport(player);
      }
    }

    for (Player online : Bukkit.getOnlinePlayers()) {
      boolean isPlayerInRegion = region.contains(online.getLocation().getBlockX(),
          online.getLocation().getBlockY(), online.getLocation().getBlockZ());
      boolean inWorld = online.getWorld().equals(privateMinesWorld);

      if (isPlayerInRegion && inWorld) {
        teleport(online);
      }
    }

    if (world != null) {

      BlockVector3 regionCorner1 = region.getMinimumPoint();
      BlockVector3 regionCorner2 = region.getMaximumPoint();

      Location min = BukkitAdapter.adapt(world, regionCorner1);
      Location max = BukkitAdapter.adapt(world, regionCorner2);

      int i = (int) Math.min(min.getX(), max.getX());
      int j = (int) Math.min(min.getY(), max.getY());
      int k = (int) Math.min(min.getZ(), max.getZ());
      int m = (int) Math.max(min.getX(), max.getX());
      int n = (int) Math.max(min.getY(), max.getY());
      int i1 = (int) Math.max(min.getZ(), max.getZ());

      for (int i2 = i; i2 <= m; i2++) {
        for (int i3 = j; i3 <= n; i3++) {
          for (int i4 = k; i4 <= i1; i4++) {
            String random = weightedRandom.roll();
            Block block = world.getBlockAt(i2, i3, i4);
            Task.syncDelayed(() -> block.setType(Material.AIR, false));
            Task.syncDelayed(() -> OraxenBlocks.place(random, block.getLocation()));
          }
        }
      }
    }
  }

  public void resetItemsAdder() {
    if (!Bukkit.getPluginManager().isPluginEnabled("ItemsAdder")) {
      privateMines.getLogger().warning("Failed to reset mine due to ItemsAdder plugin missing!");
      return;
    }

    MineData mineData = getMineData();
    MineType mineType = mineData.getMineType();
    Location location = mineData.getMinimumMining();
    BlockVector3 corner1 = BukkitAdapter.asBlockVector(mineData.getMinimumMining());
    BlockVector3 corner2 = BukkitAdapter.asBlockVector(mineData.getMaximumMining());

    Map<Material, Double> materials = mineType.getMaterials();
    Map<Material, Double> mineBlocks = mineData.getMaterials();
    Map<String, Double> itemsAdderMaterials = mineType.getItemsAdder();
    WeightedRandom<String> weightedRandom = new WeightedRandom<>();

    if (itemsAdderMaterials != null) {
      itemsAdderMaterials.forEach(weightedRandom::set);
    }

    final RandomPattern randomPattern = new RandomPattern();

    PrivateMineResetEvent privateMineResetEvent = new PrivateMineResetEvent(mineData.getMineOwner(),
        this);
    Task.syncDelayed(() -> Bukkit.getPluginManager().callEvent(privateMineResetEvent));

    if (privateMineResetEvent.isCancelled()) {
      return;
    }

    if (!mineBlocks.isEmpty()) {
      mineBlocks.forEach((material, chance) -> {
        Pattern pattern = BukkitAdapter.adapt(material.createBlockData());
        randomPattern.add(pattern, chance);
      });
    } else {
      if (materials != null) {
        materials.forEach((material, chance) -> {
          Pattern pattern = BukkitAdapter.adapt(material.createBlockData());
          randomPattern.add(pattern, chance);
        });
      }
    }

    final MineWorldManager mineWorldManager = privateMines.getMineWorldManager();

    World world = location.getWorld();
    World privateMinesWorld = mineWorldManager.getMinesWorld();

    Region region = new CuboidRegion(BukkitAdapter.adapt(world), corner1, corner2);

    Player player = Bukkit.getPlayer(mineData.getMineOwner());
    if (player != null && player.isOnline()) {
      boolean isPlayerInRegion = region.contains(player.getLocation().getBlockX(),
          player.getLocation().getBlockY(), player.getLocation().getBlockZ());
      boolean inWorld = player.getWorld().equals(privateMinesWorld);

      if (isPlayerInRegion && inWorld) {
        teleport(player);
      }
    }

    for (Player online : Bukkit.getOnlinePlayers()) {
      boolean isPlayerInRegion = region.contains(online.getLocation().getBlockX(),
          online.getLocation().getBlockY(), online.getLocation().getBlockZ());
      boolean inWorld = online.getWorld().equals(privateMinesWorld);

      if (isPlayerInRegion && inWorld) {
        teleport(online);
      }
    }

    if (world != null) {
      Location min = BukkitAdapter.adapt(world, corner1);
      Location max = BukkitAdapter.adapt(world, corner2);

      int xMin = (int) Math.min(min.getX(), max.getX());
      int yMin = (int) Math.min(min.getY(), max.getY());
      int zMin = (int) Math.min(min.getZ(), max.getZ());
      int xMax = (int) Math.max(min.getX(), max.getX());
      int yMax = (int) Math.max(min.getY(), max.getY());
      int zMax = (int) Math.max(min.getZ(), max.getZ());

      // Iterate over all blocks in the x, y, and z range
      for (int x = xMin; x <= xMax; x++) {
        for (int y = yMin; y <= yMax; y++) {
          for (int z = zMin; z <= zMax; z++) {
            // Get a random block from the weightedRandom object
            String random = weightedRandom.roll();
            // Get the block at the current x, y, z coordinates
            Block block = world.getBlockAt(x, y, z);
            // Set the block to air
            block.setType(Material.AIR, false);
            // Get the custom block corresponding to the random block
            CustomBlock customBlock = CustomBlock.getInstance(random);
            // Place the custom block at the current location
            customBlock.place(block.getLocation());
          }
        }
      }
    }
  }

  public void handleReset() {
    MineData mineData = getMineData();
    MineType mineType = mineData.getMineType();

    boolean useOraxen = mineType.getUseOraxen();
    boolean useItemsAdder = mineType.getUseItemsAdder();

    if (!useOraxen && !useItemsAdder) {
      reset();
    } else if (mineType.getOraxen() != null && useOraxen) {
      resetOraxen();
    } else if (mineType.getItemsAdder() != null && useItemsAdder) {
      resetItemsAdder();
    }

    World world = PrivateMines.getPrivateMines().getMineWorldManager().getMinesWorld();
    BlockVector3 corner1 = BukkitAdapter.asBlockVector(mineData.getMinimumMining());
    BlockVector3 corner2 = BukkitAdapter.asBlockVector(mineData.getMaximumMining());

    Region region = new CuboidRegion(BukkitAdapter.adapt(world), corner1, corner2);
    Player owner = Bukkit.getPlayer(mineData.getMineOwner());

    for (Player online : Bukkit.getOnlinePlayers()) {
      boolean isPlayerInRegion = region.contains(online.getLocation().getBlockX(),
          online.getLocation().getBlockY(), online.getLocation().getBlockZ());
      boolean inWorld = online.getWorld().equals(world);

      if (isPlayerInRegion && inWorld) {
        Task.syncDelayed(() -> teleport(online));
      }
    }
    if (owner != null) {
      boolean isPlayerInRegion = region.contains(owner.getLocation().getBlockX(),
          owner.getLocation().getBlockY(), owner.getLocation().getBlockZ());
      boolean inWorld = owner.getWorld().equals(world);
      if (isPlayerInRegion && inWorld) {
        Task.syncDelayed(() -> teleport(owner));
      }
    }

    if (percentageTask == null) {
      //Create a new Bukkit task async
      percentageTask = Task.asyncRepeating(() -> {
        double percentage = getPercentage();
        double resetPercentage = mineType.getResetPercentage();
        redempt.redlib.region.CuboidRegion cuboidRegion = new redempt.redlib.region.CuboidRegion(
            mineData.getMinimumMining(), mineData.getMaximumMining());
        if (percentage > resetPercentage) {
          handleReset();
          airBlocks = 0;
        }
      }, 0, 20);
    }
    owner.sendMessage(ChatColor.GREEN + "You've reset your mine!");
  }

  public void stopTasks() {
    if (task != null && percentageTask != null) {
      if (task.isCurrentlyRunning() && percentageTask.isCurrentlyRunning()) {
        task.cancel();
        percentageTask.cancel();
      }
    }
  }

  public List<Task> getTasks() {
    return List.of(task, percentageTask);
  }

  public double getPercentage() {
    CuboidRegion region = new CuboidRegion(BlockVector3.at(mineData.getMinimumMining().getBlockX(),
        mineData.getMinimumMining().getBlockY(), mineData.getMinimumMining().getBlockZ()),
        BlockVector3.at(mineData.getMaximumMining().getBlockX(),
            mineData.getMaximumMining().getBlockY(), mineData.getMaximumMining().getBlockZ()));

    if (Config.addWallGap) {
      for (int i = 0; i < Config.wallsGap; i++) {
        region.contract(ExpansionUtils.contractVectors(1));
      }
    }

    long total = region.getVolume();

   // Calculate the percetage of the region called "region" to then compare with how many blocks have been mined.
    airBlocks = 0;
    for (BlockVector3 vector : region) {
      Block block = Bukkit.getWorld(Objects.requireNonNull(Objects.requireNonNull(getSpawnLocation()).getWorld()).getName()).getBlockAt(vector.getBlockX(),
          vector.getBlockY(), vector.getBlockZ());
      if (block.getType().equals(Material.AIR)) {
        this.airBlocks++;
      }
    }
    return (float) airBlocks * 100L / total;
  }

  public void ban(Player player) {
    if (mineData.getBannedPlayers().contains(player.getUniqueId())) {
      return;
    }
    Player owner = Bukkit.getPlayer(mineData.getMineOwner());
    if (player.equals(owner)) {
      return;
    }
    player.sendMessage(
        ChatColor.RED + "You've been banned from " + Objects.requireNonNull(owner).getName()
            + "'s mine!");
    mineData.getBannedPlayers().add(player.getUniqueId());
    setMineData(mineData);
    saveMineData(Objects.requireNonNull(owner), mineData);
  }

  public void unban(Player player) {
    Player owner = Bukkit.getPlayer(mineData.getMineOwner());
    player.sendMessage(
        ChatColor.RED + "You've been unbanned from " + Objects.requireNonNull(owner).getName()
            + "'s mine!");
    mineData.getBannedPlayers().remove(player.getUniqueId());
    setMineData(mineData);
    saveMineData(Objects.requireNonNull(owner), mineData);
  }

  public boolean canExpand(final int amount) {
    final World world = privateMines.getMineWorldManager().getMinesWorld();
    final var min = getMineData().getMinimumMining();
    final var max = getMineData().getMaximumMining();
    final var region = new CuboidRegion(BukkitAdapter.asBlockVector(min),
        BukkitAdapter.asBlockVector(max));
    final boolean borderUpgrade = Config.borderUpgrade;

    region.expand(ExpansionUtils.expansionVectors(amount + 1));
    region.forEach(blockVector3 -> {
      Material type = Utils.toLocation(blockVector3, world).getBlock().getType();
      if (type.equals(Config.upgradeMaterial)) {
        canExpand = false;
        if (borderUpgrade) {
          upgrade(false);
        }
      }
    });
    return canExpand;
  }

  public void expand() {
    final World world = privateMines.getMineWorldManager().getMinesWorld();
    boolean canExpand = canExpand(1);
    Map<String, Boolean> flags = mineData.getMineType().getFlags();

    if (!canExpand) {
      privateMines.getLogger().info("Failed to expand the mine due to the mine being too large");
    } else {
      final var fillType = BlockTypes.DIAMOND_BLOCK;
      final var wallType = BlockTypes.BEDROCK;
      final var min = getMineData().getMinimumMining();
      final var max = getMineData().getMaximumMining();
      final Region mine = new CuboidRegion(BukkitAdapter.asBlockVector(min),
          BukkitAdapter.asBlockVector(max));
      final Region fillAir = new CuboidRegion(BukkitAdapter.asBlockVector(min),
          BukkitAdapter.asBlockVector(max));
      final Region walls = new CuboidRegion(BukkitAdapter.asBlockVector(min),
          BukkitAdapter.asBlockVector(max));

      if (fillType == null || wallType == null) {
        return;
      }

      mine.expand(ExpansionUtils.expansionVectors(1));
      walls.expand(ExpansionUtils.expansionVectors(1));

      if (Config.shouldWallsGoUp) {
        walls.expand(BlockVector3.UNIT_X, BlockVector3.UNIT_Y, BlockVector3.UNIT_Z,
            BlockVector3.UNIT_MINUS_X, BlockVector3.UNIT_MINUS_Y, BlockVector3.UNIT_MINUS_Z);
      } else {
        walls.expand(BlockVector3.UNIT_X, BlockVector3.UNIT_Z, BlockVector3.UNIT_MINUS_X,
            BlockVector3.UNIT_MINUS_Y, BlockVector3.UNIT_MINUS_Z);
      }
      fillAir.expand(BlockVector3.UNIT_X, BlockVector3.UNIT_Y, BlockVector3.UNIT_Z,
          BlockVector3.UNIT_MINUS_X, BlockVector3.UNIT_MINUS_Z);
      Map<Material, Double> materials = mineData.getMineType().getMaterials();
      final RandomPattern randomPattern = new RandomPattern();
      if (materials != null) {
        materials.forEach((material, chance) -> {
          Pattern pattern = BukkitAdapter.adapt(material.createBlockData());
          randomPattern.add(pattern, chance);
        });
      }

      PrivateMineExpandEvent privateMineExpandEvent = new PrivateMineExpandEvent(
          mineData.getMineOwner(), this, mine.getWidth(), mine.getHeight(), mine.getLength());
      Task.syncDelayed(() -> Bukkit.getPluginManager().callEvent(privateMineExpandEvent));
      if (privateMineExpandEvent.isCancelled()) {
        return;
      }

      try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder()
          .world(BukkitAdapter.adapt(world)).fastMode(true).build()) {
        editSession.setBlocks(walls, BukkitAdapter.adapt(Material.BEDROCK.createBlockData()));
        editSession.setBlocks(fillAir, BukkitAdapter.adapt(Material.AIR.createBlockData()));
      }

      mineData.setMinimumMining(BukkitAdapter.adapt(world, mine.getMinimumPoint()));
      mineData.setMaximumMining(BukkitAdapter.adapt(world, mine.getMaximumPoint()));
      mineData.setMinimumFullRegion(mineData.getMinimumFullRegion().subtract(1, 1, 1));
      mineData.setMaximumFullRegion(mineData.getMaximumFullRegion().add(1, 1, 1));
      String mineRegionName = String.format("mine-%s", mineData.getMineOwner());

      RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
      RegionManager regionManager = container.get(BukkitAdapter.adapt(world));

      if (regionManager != null) {
        regionManager.removeRegion(mineRegionName);
      }

      ProtectedCuboidRegion protectedCuboidRegion = new ProtectedCuboidRegion(mineRegionName,
          mine.getMinimumPoint(), mine.getMaximumPoint());
      if (regionManager != null) {
        regionManager.addRegion(protectedCuboidRegion);
      }

      FlagUtils flagUtils = new FlagUtils();
      flagUtils.setFlags(this);

      setMineData(mineData);
      privateMines.getMineStorage().replaceMineNoLog(mineData.getMineOwner(), this);
      handleReset();
      SQLUtils.update(this);
    }
    this.canExpand = true;
  }

  public void saveMineData(Player player, MineData mineData) {
    String uuidString = String.valueOf(player.getUniqueId());
    String fileName = String.format("/%s.yml", uuidString);

    File minesDirectory = privateMines.getMinesDirectory().toFile();
    File file = new File(minesDirectory + fileName);
    privateMines.getLogger().info("Saving file " + file.getName() + "...");
    YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

    MineType mineType = mineData.getMineType();
    String mineTypeName = mineType.getName();

    UUID owner = player.getUniqueId();
    Location mineLocation = mineData.getMineLocation();
    Location corner1 = mineData.getMinimumMining();
    Location corner2 = mineData.getMaximumMining();
    Location fullRegionMin = mineData.getMinimumFullRegion();
    Location fullRegionMax = mineData.getMaximumFullRegion();

    Location spawn = mineData.getSpawnLocation();
    double tax = mineData.getTax();
    boolean open = mineData.isOpen();
    int maxPlayers = mineData.getMaxPlayers();
    int maxMineSize = mineData.getMaxMineSize();

    List<UUID> bannedPlayers = mineData.getBannedPlayers();
    List<UUID> friends = mineData.getFriends();

    Map<Material, Double> materials = mineData.getMaterials();

    if (!file.exists()) {
      try {
        boolean createdNewFile = file.createNewFile();
        if (createdNewFile) {
          privateMines.getLogger().info("Created new file " + file.getName());
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      yml.set("mineOwner", owner.toString());
      yml.set("mineType", mineTypeName);
      yml.set("mineLocation", LocationUtils.toString(mineLocation));
      yml.set("corner1", LocationUtils.toString(corner1));
      yml.set("corner2", LocationUtils.toString(corner2));
      yml.set("fullRegionMin", LocationUtils.toString(fullRegionMin));
      yml.set("fullRegionMax", LocationUtils.toString(fullRegionMax));
      yml.set("spawn", LocationUtils.toString(spawn));
      yml.set("tax", tax);
      yml.set("isOpen", open);
      yml.set("maxPlayers", maxPlayers);
      yml.set("maxMineSize", maxMineSize);

      if (!materials.isEmpty()) {
        yml.set("materials", materials.toString());
      }

      try {
        yml.save(file);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } else {
      yml.set("corner1", LocationUtils.toString(mineData.getMinimumMining()));
      yml.set("corner2", LocationUtils.toString(mineData.getMaximumMining()));
      yml.set("tax", tax);
      yml.set("isOpen", open);
      yml.set("maxPlayers", maxPlayers);
      yml.set("maxMineSize", maxMineSize);
//            yml.set("bannedPlayers", bannedPlayers);
      if (!materials.isEmpty()) {
        yml.set("materials", materials.toString());
      }
      try {
        yml.save(file);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  public void upgrade(boolean force) {
    Instant start = Instant.now();

    /**
     * New upgrade system
     *
     * Cancel all tasks
     * Get min + max full location
     * Clear the whole region
     * Paste new schematic
     * Get new locations and update the sql location stuff.
     */

    MineTypeManager mineTypeManager = privateMines.getMineTypeManager();
    MineFactory mineFactory = privateMines.getMineFactory();
    MineStorage mineStorage = privateMines.getMineStorage();
    MineData mineData = getMineData();
    UUID mineOwner = mineData.getMineOwner();
    Player player = Bukkit.getOfflinePlayer(mineOwner).getPlayer();
    List<Player> toTeleport = new ArrayList<>();
    MineType currentType = mineTypeManager.getMineType(mineData.getMineType());
    MineType nextType = mineTypeManager.getNextMineType(currentType);
    Location mineLocation = mineData.getMineLocation();
    File schematicFile = new File("plugins/PrivateMines/schematics/" + nextType.getFile());
    mineData.setMineType(nextType);

    Economy economy = PrivateMines.getEconomy();
    double upgradeCost = nextType.getUpgradeCost();
    PrivateMineUpgradeEvent privateMineUpgradeEvent = new PrivateMineUpgradeEvent(mineOwner, this,
        currentType, nextType);
    Bukkit.getPluginManager().callEvent(privateMineUpgradeEvent);
    if (privateMineUpgradeEvent.isCancelled()) {
      return;
    }
    if (player != null) {
      if (Objects.equals(currentType.getFile(), nextType.getFile())) {
        SQLUtils.update(this);
      } else {
        Location fullMin = mineData.getMinimumFullRegion();
        Location fullMax = mineData.getMaximumFullRegion();
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(fullMin.getWorld());
        Region cuboidRegion = new CuboidRegion(BukkitAdapter.asBlockVector(fullMin),
            BukkitAdapter.asBlockVector(fullMax));
        final RandomPattern randomPattern = new RandomPattern();
        Pattern pattern = BukkitAdapter.adapt(Material.AIR.createBlockData());
        randomPattern.add(pattern, 1.0);

        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(schematicFile);
        BlockVector3 vector = BlockVector3.at(mineLocation.getBlockX(), mineLocation.getBlockY(),
            mineLocation.getBlockZ());
        SchematicStorage storage = privateMines.getSchematicStorage();
        SchematicIterator.MineBlocks mineBlocks = storage.getMineBlocksMap().get(schematicFile);

        Task.asyncDelayed(() -> {
          if (clipboardFormat != null) {
            try (ClipboardReader clipboardReader = clipboardFormat.getReader(
                new FileInputStream(schematicFile))) {
              EditSession editSession;
              if (Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit")) {
                editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world)
                    .fastMode(true).build();
              } else {
                editSession = WorldEdit.getInstance().newEditSession(world);
              }
              LocalSession localSession = new LocalSession();

              Clipboard clipboard = clipboardReader.read();
              ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);
              BlockVector3 lrailsV = vector.subtract(mineBlocks.getSpawnLocation())
                  .add(mineBlocks.getCorner2().add(0, 0, 1));
              BlockVector3 urailsV = vector.subtract(mineBlocks.getSpawnLocation())
                  .add(mineBlocks.getCorner1().add(0, 0, 1));

              Location spongeL = new Location(mineLocation.getWorld(), vector.getBlockX(),
                  vector.getBlockY(), vector.getBlockZ() + 1);

              Location lrailsL = new Location(mineLocation.getWorld(), lrailsV.getBlockX(),
                  lrailsV.getBlockY(), lrailsV.getBlockZ());
              Location urailsL = new Location(mineLocation.getWorld(), urailsV.getBlockX(),
                  urailsV.getBlockY(), urailsV.getBlockZ());

              editSession.setBlocks(cuboidRegion,
                  BukkitAdapter.adapt(Material.AIR.createBlockData()));
              Operation operation = clipboardHolder.createPaste(editSession).to(vector)
                      .ignoreAirBlocks(true).build();
              try {
                Operations.complete(operation);
              } finally {
                editSession.flushQueue();
                mineData.setSpawnLocation(spongeL);
                mineData.setMinimumMining(lrailsL);
                mineData.setMaximumMining(urailsL);
                setMineData(mineData);
                handleReset();
                Task.syncDelayed(() -> spongeL.getBlock().setType(Material.AIR, false));
              }
            } catch (IOException e) {
              throw new RuntimeException(e);
            }
          }
        });
        SQLUtils.update(this);
      }
    }

    Instant end = Instant.now();
    Duration upgradeTime = Duration.between(start, end);
  }

  public void createWorldGuardRegions() {

    String mineRegionName = String.format("mine-%s", getMineData().getMineOwner());
    String fullRegionName = String.format("full-mine-%s", getMineData().getMineOwner());
    com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(
        Objects.requireNonNull(getMineData().getMinimumMining().getWorld()));

    MineType mineType = getMineData().getMineType();
    Map<String, Boolean> flags = mineType.getFlags();
    Map<String, Boolean> fullFlags = mineType.getFullFlags();

    BlockVector3 minMining = BukkitAdapter.asBlockVector(getMineData().getMinimumMining());
    BlockVector3 maxMining = BukkitAdapter.asBlockVector(getMineData().getMaximumMining());
    BlockVector3 minFull = BukkitAdapter.asBlockVector(
        getMineData().getMinimumFullRegion());
    BlockVector3 maxFull = BukkitAdapter.asBlockVector(
        getMineData().getMaximumFullRegion());

    ProtectedCuboidRegion miningWorldGuardRegion = new ProtectedCuboidRegion(mineRegionName,
        minMining, maxMining);
    ProtectedCuboidRegion fullWorldGuardRegion = new ProtectedCuboidRegion(fullRegionName,
        minFull, maxFull);
    RegionContainer regionContainer = WorldGuard.getInstance().getPlatform()
        .getRegionContainer();
    RegionManager regionManager = regionContainer.get(world);

    if (regionManager != null) {
      regionManager.addRegion(miningWorldGuardRegion);
      regionManager.addRegion(fullWorldGuardRegion);
    }

    /*
     This sadly has to be called synchronously else it'll throw a
     {@link java.lang.IllegalStateException}
     This is due to how WorldGuard handles their flags...
     @see com.sk89q.worldguard.bukkit.protection.events.flags.FlagContextCreateEvent
     */
    Task.syncDelayed(() -> {
      FlagRegistry flagRegistry = WorldGuard.getInstance().getFlagRegistry();

      if (flags != null) {
        flags.forEach((s, aBoolean) -> {
          Flag<?> flag = Flags.fuzzyMatchFlag(flagRegistry, s);
          if (aBoolean) {
            try {
              Utils.setFlag(miningWorldGuardRegion, flag, "allow");
            } catch (InvalidFlagFormat e) {
              e.printStackTrace();
            }
          } else {
            try {
              Utils.setFlag(miningWorldGuardRegion, flag, "deny");
            } catch (InvalidFlagFormat e) {
              e.printStackTrace();
            }
          }
        });
      }
      if (fullFlags != null) {
        fullFlags.forEach((string, aBoolean) -> {
          Flag<?> flag = Flags.fuzzyMatchFlag(flagRegistry, string);
          if (aBoolean) {
            try {
              Utils.setFlag(fullWorldGuardRegion, flag, "allow");
            } catch (InvalidFlagFormat e) {
              e.printStackTrace();
            }
          } else {
            try {
              Utils.setFlag(fullWorldGuardRegion, flag, "deny");
            } catch (InvalidFlagFormat e) {
              throw new RuntimeException(e);
            }
          }
        });
      }
    });
  }
}
