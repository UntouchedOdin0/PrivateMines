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
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import dev.drawethree.xprison.autosell.XPrisonAutoSell;
import dev.drawethree.xprison.autosell.model.SellRegion;
import dev.drawethree.xprison.utils.compat.CompMaterial;
import dev.lone.itemsadder.api.CustomBlock;
import io.th0rgal.oraxen.api.OraxenBlocks;
import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import me.untouchedodin0.kotlin.mine.data.MineData;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.kotlin.utils.FlagUtils;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.config.Config;
import me.untouchedodin0.privatemines.events.PrivateMineDeleteEvent;
import me.untouchedodin0.privatemines.events.PrivateMineExpandEvent;
import me.untouchedodin0.privatemines.events.PrivateMineResetEvent;
import me.untouchedodin0.privatemines.events.PrivateMineUpgradeEvent;
import me.untouchedodin0.privatemines.storage.sql.SQLUtils;
import me.untouchedodin0.privatemines.utils.ExpansionUtils;
import me.untouchedodin0.privatemines.utils.Utils;
import me.untouchedodin0.privatemines.utils.world.MineWorldManager;
import me.untouchedodin0.privatemines.utils.worldedit.PasteHelper;
import me.untouchedodin0.privatemines.utils.worldedit.objects.PastedMine;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import redempt.redlib.misc.Task;
import redempt.redlib.misc.WeightedRandom;

public class Mine {

  private final PrivateMines privateMines;
  private BlockVector3 location;
  private MineData mineData;
  private boolean canExpand = true;
  private Task task;
  private Task percentageTask = null;
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
    SQLUtils.delete(this);
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
      percentageTask = Task.syncRepeating(() -> {
        double percentage = getPercentage();
        double resetPercentage = mineType.getResetPercentage();
        if (percentage > resetPercentage) {
          handleReset();
          airBlocks = 0;
        }
      }, 0, 80);
    }
    if (owner != null) {
      owner.sendMessage(ChatColor.GREEN + "You've reset your mine!");
    }
  }

  public void stopTasks() {
    if (task != null) {
      task.cancel();
    }
    if (percentageTask != null) {
      percentageTask.cancel();
    }
    privateMines.getLogger().info("Stopped tasks for mine " + mineData.getMineOwner());
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
      Block block = Objects.requireNonNull(Bukkit.getWorld(
              Objects.requireNonNull(Objects.requireNonNull(getSpawnLocation()).getWorld()).getName()))
          .getBlockAt(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
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
    SQLUtils.update(this);
  }

  public void unban(Player player) {
    Player owner = Bukkit.getPlayer(mineData.getMineOwner());
    player.sendMessage(
        ChatColor.RED + "You've been unbanned from " + Objects.requireNonNull(owner).getName()
            + "'s mine!");
    mineData.getBannedPlayers().remove(player.getUniqueId());
    setMineData(mineData);
    SQLUtils.update(this);
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
          upgrade();
        }
      }
    });
    return canExpand;
  }

  public void expand() {
    final World world = privateMines.getMineWorldManager().getMinesWorld();
    boolean canExpand = canExpand(1);

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

  public void upgrade() {
    MineTypeManager mineTypeManager = privateMines.getMineTypeManager();
    MineData mineData = getMineData();
    UUID mineOwner = mineData.getMineOwner();
    Player player = Bukkit.getOfflinePlayer(mineOwner).getPlayer();
    MineType currentType = mineTypeManager.getMineType(mineData.getMineType());
    MineType nextType = mineTypeManager.getNextMineType(currentType);

    Bukkit.broadcastMessage("current " + currentType.getName());
    Bukkit.broadcastMessage("next " + nextType.getName());

    Location mineLocation = mineData.getMineLocation();
    File schematicFile = new File("plugins/PrivateMines/schematics/" + nextType.getFile());
    mineData.setMineType(nextType);
    RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
    RegionManager regionManager = container.get(BukkitAdapter.adapt(mineLocation.getWorld()));

    PrivateMineUpgradeEvent privateMineUpgradeEvent = new PrivateMineUpgradeEvent(mineOwner, this,
        currentType, nextType);
    Bukkit.getPluginManager().callEvent(privateMineUpgradeEvent);
    if (privateMineUpgradeEvent.isCancelled()) {
      return;
    }
    if (player != null) {
      if (!Objects.equals(currentType.getFile(), nextType.getFile())) {
        PrivateMines.getEconomy().withdrawPlayer(player, nextType.getUpgradeCost());

        String mineRegionName = String.format("mine-%s", player.getUniqueId());
        String fullRegionName = String.format("full-mine-%s", player.getUniqueId());

        Region cuboidRegion = new CuboidRegion(
            BukkitAdapter.asBlockVector(mineData.getMinimumFullRegion()),
            BukkitAdapter.asBlockVector(mineData.getMaximumFullRegion()));
        final RandomPattern randomPattern = new RandomPattern();
        Pattern pattern = BukkitAdapter.adapt(Material.AIR.createBlockData());
        randomPattern.add(pattern, 1.0);
        Task.asyncDelayed(() -> {
          EditSession editSession;
          if (Bukkit.getPluginManager().isPluginEnabled("FastAsyncWorldEdit")) {
            editSession = WorldEdit.getInstance().newEditSessionBuilder()
                .world(BukkitAdapter.adapt(mineLocation.getWorld())).fastMode(true).build();
          } else {
            editSession = WorldEdit.getInstance()
                .newEditSession(BukkitAdapter.adapt(mineLocation.getWorld()));
          }
          editSession.setBlocks(cuboidRegion, randomPattern);

          PasteHelper pasteHelper = new PasteHelper();
          PastedMine pastedMine = pasteHelper.paste(schematicFile, mineLocation);

          Location spawn = mineLocation.clone().add(0, 0, 1);
          Location corner1 = pastedMine.getLowerRailsLocation();
          Location corner2 = pastedMine.getUpperRailsLocation();
          Location minimum = pasteHelper.getMinimum();
          Location maximum = pasteHelper.getMaximum();
          BlockVector3 miningRegionMin = BukkitAdapter.asBlockVector(corner1);
          BlockVector3 miningRegionMax = BukkitAdapter.asBlockVector(corner2);
          BlockVector3 fullRegionMin = BukkitAdapter.asBlockVector(minimum);
          BlockVector3 fullRegionMax = BukkitAdapter.asBlockVector(maximum);

          ProtectedCuboidRegion miningRegion = new ProtectedCuboidRegion(mineRegionName,
              miningRegionMin, miningRegionMax);
          ProtectedCuboidRegion fullRegion = new ProtectedCuboidRegion(fullRegionName,
              fullRegionMin, fullRegionMax);

          if (regionManager != null) {
            regionManager.removeRegion(fullRegionName);
            regionManager.removeRegion(mineRegionName);
            regionManager.addRegion(miningRegion);
            regionManager.addRegion(fullRegion);
          }

          mineData.setSpawnLocation(spawn);
          mineData.setMinimumMining(corner1);
          mineData.setMaximumMining(corner2);
          mineData.setMinimumFullRegion(minimum);
          mineData.setMaximumFullRegion(maximum);
          setMineData(mineData);
          handleReset();
          Task.asyncDelayed(() -> {
            SQLUtils.update(this);
          });
          Task.syncDelayed(() -> spawn.getBlock().setType(Material.AIR, false));

          XPrisonAutoSell autoSell = XPrisonAutoSell.getInstance();
          SellRegion sellRegion = autoSell.getManager().getAutoSellRegion(mineLocation);

          if (nextType.getPrices() != null) {
            nextType.getPrices().forEach((material, aDouble) -> {
              CompMaterial compMaterial = CompMaterial.fromMaterial(material);
              sellRegion.addSellPrice(compMaterial, aDouble);
            });

            autoSell.getManager().updateSellRegion(sellRegion);
            autoSell.getAutoSellConfig().saveSellRegion(sellRegion);
          }
        });
      }
    }
  }
}

