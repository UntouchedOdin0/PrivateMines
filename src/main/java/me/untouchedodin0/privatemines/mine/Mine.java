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
import com.sk89q.worldguard.protection.regions.RegionContainer;
import io.papermc.lib.PaperLib;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.config.Config;
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
    private final MineTypeManager mineTypeManager;
    private UUID mineOwner;
    private BlockVector3 location;
    private MineData mineData;
    private boolean canExpand = true;

    public Mine(PrivateMines privateMines) {
        this.privateMines = privateMines;
        this.mineTypeManager = privateMines.getMineTypeManager();
        Utils utils = new Utils(privateMines);
    }

    public UUID getMineOwner() {
        return mineOwner;
    }

    public void setMineOwner(UUID uuid) {
        this.mineOwner = uuid;
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

    public MineType getMineType() {
        return mineTypeManager.getMineType(mineData.getMineType());
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

    public void delete(UUID uuid) {
        MineData mineData = getMineData();

        Location corner1 = mineData.getMinimumFullRegion();
        BlockVector3 corner1BV3 = BukkitAdapter.asBlockVector(mineData.getMinimumFullRegion());
        BlockVector3 corner2BV3 = BukkitAdapter.asBlockVector(mineData.getMaximumFullRegion());

        Player player = Bukkit.getOfflinePlayer(uuid).getPlayer();
        String regionName = String.format("mine-%s", Objects.requireNonNull(player).getUniqueId());

        World world = corner1.getWorld();
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(BukkitAdapter.adapt(Objects.requireNonNull(world)));
        Objects.requireNonNull(regionManager).removeRegion(regionName);
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
    public void replace(UUID uuid, MineType newType) {
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

        Map<Material, Double> materials = mineType.getMaterials();
        final RandomPattern randomPattern = new RandomPattern();

        if (materials != null) {
            materials.forEach((material, chance) -> {
                Pattern pattern = BukkitAdapter.adapt(material.createBlockData());
                randomPattern.add(pattern, chance);
            });
        }

        World world = location.getWorld();

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

    public void startResetTask() {
        MineTypeManager mineTypeManager = privateMines.getMineTypeManager();
        MineType mineType = mineTypeManager.getMineType(mineData.getMineType());
        int resetTime = mineType.getResetTime();
        Task.asyncRepeating(this::reset, 0L, resetTime * 20L);
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
        region.expand(ExpansionUtils.expansionVectors(amount + 1));
        region.forEach(blockVector3 -> {
            Material type = Utils.toLocation(blockVector3, world).getBlock().getType();
            if (type.equals(Material.OBSIDIAN)) canExpand = false;
        });
        return canExpand;
    }

    public void expand(final int amount) {
        final World world = privateMines.getMineWorldManager().getMinesWorld();
        boolean canExpand = canExpand(amount);
        if (world == null) {
            privateMines.getLogger().info("Failed to expand the mine due to the world being null");
        } else {
            final var fillType = BlockTypes.DIAMOND_BLOCK;
            final var wallType = BlockTypes.BEDROCK;
            final var min = getMineData().getMinimumMining();
            final var max = getMineData().getMaximumMining();
            final var mine = new CuboidRegion(BukkitAdapter.asBlockVector(min), BukkitAdapter.asBlockVector(max));
            final var walls = new CuboidRegion(BukkitAdapter.asBlockVector(min), BukkitAdapter.asBlockVector(max));

            if (fillType == null || wallType == null) return;

            mine.expand(ExpansionUtils.expansionVectors(amount));
            walls.expand(ExpansionUtils.expansionVectors(amount));

            mineData.setMinimumMining(BukkitAdapter.adapt(world, mine.getMinimumPoint()));
            mineData.setMinimumMining(BukkitAdapter.adapt(world, mine.getMaximumPoint()));
            setMineData(mineData);
            privateMines.getMineStorage().replaceMine(getMineOwner(), this);
        }
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

        if (file.exists()) {
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
            try {
                if (file.createNewFile()) {
                    privateMines.getLogger().info("Created new file: " + file.getPath());
                    yml.set("mineOwner", owner.toString());
                    yml.set("mineType", mineTypeName);
                    yml.set("mineLocation", LocationUtils.toString(mineLocation));
                    yml.set("corner1", LocationUtils.toString(corner1));
                    yml.set("corner2", LocationUtils.toString(corner2));
                    yml.set("fullRegionMin", LocationUtils.toString(fullRegionMin));
                    yml.set("fullRegionMax", LocationUtils.toString(fullRegionMax));
                    yml.set("spawn", LocationUtils.toString(spawn));

                    try {
                        yml.save(file);
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void upgrade() {
        MineTypeManager mineTypeManager = privateMines.getMineTypeManager();
        MineFactory mineFactory = privateMines.getMineFactory();
        MineData mineData = getMineData();
        UUID mineOwner = mineData.getMineOwner();
        Player player = Bukkit.getOfflinePlayer(mineOwner).getPlayer();
        MineType currentType = mineTypeManager.getMineType(mineData.getMineType());
        MineType nextType = mineTypeManager.getNextMineType(currentType.getName());
        double upgradeCost = nextType.getUpgradeCost();

        if (player != null) {
            if (currentType == nextType) {
                privateMines.getLogger().info("Failed to upgrade " + player.getName() + "'s mine as it was fully upgraded!");
            } else {
                if (upgradeCost == 0) {
                    Location mineLocation = mineData.getMineLocation();
                    delete(mineOwner);
                    mineFactory.create(Objects.requireNonNull(player), mineLocation, nextType);
                } else {
                    Economy economy = PrivateMines.getEconomy();
                    double balance = economy.getBalance(player);
                    if (balance < upgradeCost) {
                        player.sendMessage(ChatColor.RED + "You don't have enough money to upgrade your mine!");
                    } else {
                        Location mineLocation = mineData.getMineLocation();
                        delete(mineOwner);
                        mineFactory.create(Objects.requireNonNull(player), mineLocation, nextType);
                        economy.withdrawPlayer(player, nextType.getUpgradeCost());
                    }
                }
            }
        }
    }
}
