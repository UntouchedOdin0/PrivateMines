/**
 * MIT License
 * <p>
 * Copyright (c) 2021 - 2022 Kyle Hicks
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import io.papermc.lib.PaperLib;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.config.Config;
import me.untouchedodin0.privatemines.events.PrivateMineDeleteEvent;
import me.untouchedodin0.privatemines.events.PrivateMineExpandEvent;
import me.untouchedodin0.privatemines.events.PrivateMineResetEvent;
import me.untouchedodin0.privatemines.events.PrivateMineUpgradeEvent;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.mine.data.MineData;
import me.untouchedodin0.privatemines.utils.ExpansionUtils;
import me.untouchedodin0.privatemines.utils.Utils;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import redempt.redlib.misc.LocationUtils;
import redempt.redlib.misc.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Mine {
    private final PrivateMines privateMines;
    private BlockVector3 location;
    private MineData mineData;
    private boolean canExpand = true;
    private Task task;
    private Task percentageTask;

    public Mine(PrivateMines privateMines) {
        this.privateMines = privateMines;
    }

    public BlockVector3 getLocation() {
        return location;
    }

    public void setLocation(BlockVector3 location) {
        this.location = location;
    }

    public MineData getMineData() {
        return mineData;
    }
    public void setMineData(MineData mineData) {
        this.mineData = mineData;
    }
    public void teleport(Player player) {
        if (getMineData().getSpawnLocation().getBlock().getType().isBlock()) {
            getMineData().getSpawnLocation().getBlock().setType(Material.AIR);
        }
        if (getMineData().getBannedPlayers().contains(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You're banned from this mine!");
        } else {
            if (PaperLib.isPaper()) {
                PaperLib.teleportAsync(player, getMineData().getSpawnLocation());
            } else {
                player.teleport(getMineData().getSpawnLocation());
            }
        }
    }

    public void delete() {
        UUID uuid = getMineData().getMineOwner();
        PrivateMineDeleteEvent privateMineDeleteEvent = new PrivateMineDeleteEvent(uuid, this);
        Bukkit.getPluginManager().callEvent(privateMineDeleteEvent);

        if (privateMineDeleteEvent.isCancelled()) return;

        if (task != null) task.cancel();
        if (percentageTask != null) percentageTask.cancel();
        MineData mineData = getMineData();

        Location corner1 = mineData.getMinimumFullRegion();
        BlockVector3 corner1BV3 = BukkitAdapter.asBlockVector(mineData.getMinimumFullRegion());
        BlockVector3 corner2BV3 = BukkitAdapter.asBlockVector(mineData.getMaximumFullRegion());

        Player player = Bukkit.getOfflinePlayer(uuid).getPlayer();
        String regionName = String.format("mine-%s", Objects.requireNonNull(player).getUniqueId());
        String fullRegionName = String.format("full-mine-%s", Objects.requireNonNull(player).getUniqueId());

        World world = corner1.getWorld();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(Objects.requireNonNull(world)));
        Objects.requireNonNull(regionManager).removeRegion(regionName);
        Objects.requireNonNull(regionManager).removeRegion(fullRegionName);

        Instant start = Instant.now();
        final RandomPattern randomPattern = new RandomPattern();
        Pattern air = BukkitAdapter.adapt(Material.AIR.createBlockData());
        randomPattern.add(air, 1.0);

        try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(BukkitAdapter.adapt(world)).fastMode(true).build()) {
            Region region = new CuboidRegion(BukkitAdapter.adapt(world), corner1BV3, corner2BV3);
            editSession.setBlocks(region, randomPattern);
        }

        Instant filled = Instant.now();
        Duration durationToFill = Duration.between(start, filled);

        long durationInMS = TimeUnit.NANOSECONDS.toMillis(durationToFill.toNanos());

        privateMines.getLogger().info(String.format("It took %dms to reset the mine", durationInMS));
        privateMines.getMineStorage().removeMine(uuid);
        String fileName = String.format("/%s.yml", uuid);
        Path minesDirectory = privateMines.getMinesDirectory();
        File file = new File(minesDirectory + fileName);

        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Deprecated This isn't really used anymore.
     */
    @Deprecated
    public void replace(UUID uuid) {
        MineData mineData = getMineData();

        Location cornerA = mineData.getMinimumFullRegion();
        Location cornerB = mineData.getMaximumFullRegion();

        Player player = Bukkit.getOfflinePlayer(uuid).getPlayer();
        String regionName = String.format("mine-%s", Objects.requireNonNull(player).getUniqueId());

        World world = cornerA.getWorld();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(Objects.requireNonNull(world)));
        Objects.requireNonNull(regionManager).removeRegion(regionName);

        int blocks = 0;

        int xMax = Integer.max(cornerA.getBlockX(), cornerB.getBlockX());
        int xMin = Integer.min(cornerA.getBlockX(), cornerB.getBlockX());
        int yMax = Integer.max(cornerA.getBlockY(), cornerB.getBlockY());
        int yMin = Integer.min(cornerA.getBlockY(), cornerB.getBlockY());
        int zMax = Integer.max(cornerA.getBlockZ(), cornerB.getBlockZ());
        int zMin = Integer.min(cornerA.getBlockZ(), cornerB.getBlockZ());

        Instant start = Instant.now();

        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int z = zMin; z <= zMax; z++) {
                    world.getBlockAt(x, y, z).setType(Material.AIR);
                    blocks++;
                }
            }
        }

        Instant filled = Instant.now();
        Duration durationToFill = Duration.between(start, filled);
        privateMines.getLogger().info(String.format("Time took to fill %d blocks %dms", blocks, durationToFill.toMillis()));
        privateMines.getMineStorage().removeMine(uuid);
        String fileName = String.format("/%s.yml", uuid);
        Path minesDirectory = privateMines.getMinesDirectory();
        File file = new File(minesDirectory + fileName);
        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reset() {
        MineData mineData = getMineData();
        MineType mineType = mineData.getMineType();
        Location location = mineData.getMinimumMining();
        BlockVector3 corner1 = BukkitAdapter.asBlockVector(mineData.getMinimumMining());
        BlockVector3 corner2 = BukkitAdapter.asBlockVector(mineData.getMaximumMining());
        @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
        CuboidRegion fullRegion = new CuboidRegion(BukkitAdapter.adapt(location.getWorld()), corner1, corner2);

        Map<Material, Double> materials = mineType.getMaterials();
        final RandomPattern randomPattern = new RandomPattern();

        PrivateMineResetEvent privateMineResetEvent = new PrivateMineResetEvent(mineData.getMineOwner(), this);
        Task.syncDelayed(() -> Bukkit.getPluginManager().callEvent(privateMineResetEvent));

        if (privateMineResetEvent.isCancelled()) return;

        if (materials != null) {
            materials.forEach((material, chance) -> {
                Pattern pattern = BukkitAdapter.adapt(material.createBlockData());
                randomPattern.add(pattern, chance);
            });
        }

        World world = location.getWorld();
        Player player = Bukkit.getPlayer(mineData.getMineOwner());
        if (player != null && player.isOnline()) {
            boolean isPlayerInRegion = fullRegion.contains(player.getLocation().getBlockX(),
                    player.getLocation().getBlockY(),
                    player.getLocation().getBlockZ());
            if (isPlayerInRegion) {
                teleport(player);
            }
        }

        try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(BukkitAdapter.adapt(world)).fastMode(true).build()) {
            Region region = new CuboidRegion(BukkitAdapter.adapt(world), corner1, corner2);

            if (Config.onlyReplaceAir) {
                if (BlockTypes.AIR != null) {
                    editSession.replaceBlocks(region, Collections.singleton(BlockTypes.AIR.getDefaultState().toBaseBlock()), randomPattern);
                }
            } else {
                editSession.setBlocks(region, randomPattern);
            }
        }
    }

    public void resetNoCheck() {
        MineData mineData = getMineData();
        MineType mineType = mineData.getMineType();
        Location location = mineData.getMinimumMining();
        BlockVector3 corner1 = BukkitAdapter.asBlockVector(mineData.getMinimumMining());
        BlockVector3 corner2 = BukkitAdapter.asBlockVector(mineData.getMaximumMining());

        Map<Material, Double> materials = mineType.getMaterials();
        final RandomPattern randomPattern = new RandomPattern();

        PrivateMineResetEvent privateMineResetEvent = new PrivateMineResetEvent(mineData.getMineOwner(), this);
        Task.syncDelayed(() -> Bukkit.getPluginManager().callEvent(privateMineResetEvent));

        if (privateMineResetEvent.isCancelled()) return;

        if (materials != null) {
            materials.forEach((material, chance) -> {
                Pattern pattern = BukkitAdapter.adapt(material.createBlockData());
                randomPattern.add(pattern, chance);
            });
        }

        World world = location.getWorld();

        try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(BukkitAdapter.adapt(world)).fastMode(true).build()) {
            Region region = new CuboidRegion(BukkitAdapter.adapt(world), corner1, corner2);
            editSession.setBlocks(region, randomPattern);
            editSession.flushQueue();
        }
    }

    public void startResetTask() {
        MineTypeManager mineTypeManager = privateMines.getMineTypeManager();
        MineType mineType = mineTypeManager.getMineType(mineData.getMineType());
        int resetTime = mineType.getResetTime();
        this.task = Task.syncRepeating(this::reset, 0L, resetTime * 20 * 60L);
    }

    public void cancelTask() {
        if (task.isCurrentlyRunning()) task.cancel();
    }

    public void startPercentageTask() {
        this.percentageTask = Task.syncRepeating(() -> {
            double percentage = getPercentage();
            if (percentage >= 50) {
                reset();
                Bukkit.broadcastMessage(ChatColor.GREEN + Bukkit.getOfflinePlayer(mineData.getMineOwner()).getName() + "'s private mine has been reset!");
            }
        }, 0L, 20L);
    }

    public double getPercentage() {
        CuboidRegion region = new CuboidRegion(BlockVector3.at(mineData.getMinimumMining().getBlockX(), mineData.getMinimumMining().getBlockY(), mineData.getMinimumMining().getBlockZ()), BlockVector3.at(mineData.getMaximumMining().getBlockX(), mineData.getMaximumMining().getBlockY(), mineData.getMaximumMining().getBlockZ()));

        long total = region.getVolume();
        int airBlocks = 0;
        Set<BaseBlock> blocks = new HashSet<>();
        if (BlockTypes.AIR != null) {
            blocks.add(BlockTypes.AIR.getDefaultState().toBaseBlock());
            try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(BukkitAdapter.adapt(mineData.getMinimumMining().getWorld())).fastMode(true).build()) {
                airBlocks = editSession.countBlocks(region, blocks);
            }
        }
        return (float) airBlocks * 100L / total;
    }

    public void ban(Player player) {
        if (mineData.getBannedPlayers().contains(player.getUniqueId())) return;
        Player owner = Bukkit.getPlayer(mineData.getMineOwner());
        if (player.equals(owner)) return;
        player.sendMessage(ChatColor.RED + "You've been banned from " + Objects.requireNonNull(owner).getName() + "'s mine!");
        mineData.addBannedPlayer(player.getUniqueId());
        setMineData(mineData);
        saveMineData(Objects.requireNonNull(owner), mineData);
    }

    public void unban(Player player) {
        Player owner = Bukkit.getPlayer(mineData.getMineOwner());
        player.sendMessage(ChatColor.RED + "You've been unbanned from " + Objects.requireNonNull(owner).getName() + "'s mine!");
        mineData.removeBannedPlayer(player.getUniqueId());
        setMineData(mineData);
        saveMineData(Objects.requireNonNull(owner), mineData);
    }

    public boolean canExpand(final int amount) {
        final World world = privateMines.getMineWorldManager().getMinesWorld();
        final var min = getMineData().getMinimumMining();
        final var max = getMineData().getMaximumMining();
        final var region = new CuboidRegion(BukkitAdapter.asBlockVector(min), BukkitAdapter.asBlockVector(max));
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
        Map<String, Boolean> flags = mineData.getMineType().getFlags();

        if (!canExpand) {
            privateMines.getLogger().info("Failed to expand the mine due to the mine being too large");
        } else {
            final var fillType = BlockTypes.DIAMOND_BLOCK;
            final var wallType = BlockTypes.BEDROCK;
            final var min = getMineData().getMinimumMining();
            final var max = getMineData().getMaximumMining();
            final Region mine = new CuboidRegion(BukkitAdapter.asBlockVector(min), BukkitAdapter.asBlockVector(max));
            final Region fillAir = new CuboidRegion(BukkitAdapter.asBlockVector(min), BukkitAdapter.asBlockVector(max));
            final Region walls = new CuboidRegion(BukkitAdapter.asBlockVector(min), BukkitAdapter.asBlockVector(max));

            if (fillType == null || wallType == null) return;

            mine.expand(ExpansionUtils.expansionVectors(1));
            walls.expand(ExpansionUtils.expansionVectors(1));
            walls.expand(BlockVector3.UNIT_X, BlockVector3.UNIT_Y, BlockVector3.UNIT_Z, BlockVector3.UNIT_MINUS_X, BlockVector3.UNIT_MINUS_Y, BlockVector3.UNIT_MINUS_Z);
            fillAir.expand(BlockVector3.UNIT_X, BlockVector3.UNIT_Y, BlockVector3.UNIT_Z, BlockVector3.UNIT_MINUS_X, BlockVector3.UNIT_MINUS_Z);
            Map<Material, Double> materials = mineData.getMineType().getMaterials();
            final RandomPattern randomPattern = new RandomPattern();
            if (materials != null) {
                materials.forEach((material, chance) -> {
                    Pattern pattern = BukkitAdapter.adapt(material.createBlockData());
                    randomPattern.add(pattern, chance);
                });
            }

            PrivateMineExpandEvent privateMineExpandEvent = new PrivateMineExpandEvent(mineData.getMineOwner(), this, mine.getWidth(), mine.getHeight(), mine.getLength());
            Task.syncDelayed(() -> Bukkit.getPluginManager().callEvent(privateMineExpandEvent));
            if (privateMineExpandEvent.isCancelled()) return;

            try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(BukkitAdapter.adapt(world)).fastMode(true).build()) {
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

            ProtectedCuboidRegion protectedCuboidRegion = new ProtectedCuboidRegion(mineRegionName, mine.getMinimumPoint(), mine.getMaximumPoint());
            if (regionManager != null) {
                regionManager.addRegion(protectedCuboidRegion);
            }

            if (flags != null) {
                flags.forEach((string, aBoolean) -> {
                    Flag<?> flag = Flags.fuzzyMatchFlag(WorldGuard.getInstance().getFlagRegistry(), string);
                    if (aBoolean) {
                        try {
                            Utils.setFlag(protectedCuboidRegion, flag, "allow");
                        } catch (InvalidFlagFormat e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        try {
                            Utils.setFlag(protectedCuboidRegion, flag, "deny");
                        } catch (InvalidFlagFormat e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            setMineData(mineData);
            privateMines.getMineStorage().replaceMineNoLog(mineData.getMineOwner(), this);
            reset();
        }
        this.canExpand = true;
    }

    public void expandMine() {
        final World world = privateMines.getMineWorldManager().getMinesWorld();

        final var fillType = BlockTypes.DIAMOND_BLOCK;
        final var wallType = BlockTypes.BEDROCK;
        final var min = getMineData().getMinimumMining();
        final var max = getMineData().getMaximumMining();
        final Region mine = new CuboidRegion(BukkitAdapter.asBlockVector(min), BukkitAdapter.asBlockVector(max));
        final Region fillAir = new CuboidRegion(BukkitAdapter.asBlockVector(min), BukkitAdapter.asBlockVector(max));

        if (fillType == null || wallType == null) return;

        mine.expand(BlockVector3.UNIT_X, BlockVector3.UNIT_Y, BlockVector3.UNIT_Z, BlockVector3.UNIT_MINUS_X, BlockVector3.UNIT_MINUS_Y, BlockVector3.UNIT_MINUS_Z);
        Map<Material, Double> materials = mineData.getMineType().getMaterials();
        final RandomPattern randomPattern = new RandomPattern();
        if (materials != null) {
            materials.forEach((material, chance) -> {
                Pattern pattern = BukkitAdapter.adapt(material.createBlockData());
                randomPattern.add(pattern, chance);
            });
        }

        PrivateMineExpandEvent privateMineExpandEvent = new PrivateMineExpandEvent(mineData.getMineOwner(), this, mine.getWidth(), mine.getHeight(), mine.getLength());
        Task.syncDelayed(() -> Bukkit.getPluginManager().callEvent(privateMineExpandEvent));
        if (privateMineExpandEvent.isCancelled()) return;

        try (EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(BukkitAdapter.adapt(world)).fastMode(true).build()) {
            editSession.setBlocks(mine, BukkitAdapter.adapt(Material.DIRT.createBlockData()));
            editSession.setBlocks(fillAir, BukkitAdapter.adapt(Material.AIR.createBlockData()));
        }

        mineData.setMinimumMining(BukkitAdapter.adapt(world, mine.getMinimumPoint()));
        mineData.setMaximumMining(BukkitAdapter.adapt(world, mine.getMaximumPoint()));
        mineData.setMinimumFullRegion(mineData.getMinimumFullRegion().subtract(1, 1, 1));
        mineData.setMaximumFullRegion(mineData.getMaximumFullRegion().add(1, 1, 1));
        setMineData(mineData);
        privateMines.getMineStorage().replaceMine(mineData.getMineOwner(), this);
        reset();
    }

    public void saveMineData(Player player, MineData mineData) {
        String fileName = String.format("/%s.yml", player.getUniqueId());

        Path minesDirectory = privateMines.getMinesDirectory();
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
        List<UUID> bannedPlayers = mineData.getBannedPlayers();

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
            yml.set("bannedPlayers", bannedPlayers.toString());

            try {
                yml.save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            yml.set("corner1", LocationUtils.toString(mineData.getMinimumMining()));
            yml.set("corner2", LocationUtils.toString(mineData.getMaximumMining()));

            try {
                yml.save(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void upgrade() {
        MineTypeManager mineTypeManager = privateMines.getMineTypeManager();
        MineFactory mineFactory = privateMines.getMineFactory();
        MineStorage mineStorage = privateMines.getMineStorage();

        MineData mineData = getMineData();
        UUID mineOwner = mineData.getMineOwner();
        Player player = Bukkit.getOfflinePlayer(mineOwner).getPlayer();
        MineType currentType = mineTypeManager.getMineType(mineData.getMineType());
        MineType nextType = mineTypeManager.getNextMineType(currentType.getName());
        Economy economy = PrivateMines.getEconomy();

        double upgradeCost = nextType.getUpgradeCost();
        PrivateMineUpgradeEvent privateMineUpgradeEvent = new PrivateMineUpgradeEvent(mineOwner, this, currentType, nextType);
        Bukkit.getPluginManager().callEvent(privateMineUpgradeEvent);
        if (privateMineUpgradeEvent.isCancelled()) {
            return;
        }
        if (player != null) {
            if (currentType == nextType) {
                privateMines.getLogger().info("Failed to upgrade " + player.getName() + "'s mine as it was fully upgraded!");
            } else {
                if (upgradeCost == 0) {
                    Location mineLocation = mineData.getMineLocation();
                    delete();
                    mineFactory.create(Objects.requireNonNull(player), mineLocation, nextType);
                    Mine mine = mineStorage.get(mineOwner);
                    if (mine != null) {
                        mine.resetNoCheck();
                    }
                } else {

                    double balance = economy.getBalance(player);
                    if (balance < upgradeCost) {
                        player.sendMessage(ChatColor.RED + "You don't have enough money to upgrade your mine!");
                    } else {
                        Location mineLocation = mineData.getMineLocation();
                        delete();
                        mineFactory.create(Objects.requireNonNull(player), mineLocation, nextType);

                        player.sendMessage("upgrade cost: " + upgradeCost);
                        economy.withdrawPlayer(player, upgradeCost);
                    }
                }
            }
        }
    }
}