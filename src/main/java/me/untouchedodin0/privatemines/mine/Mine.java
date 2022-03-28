package me.untouchedodin0.privatemines.mine;

import com.sk89q.worldedit.math.BlockVector3;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.config.MineConfig;
import me.untouchedodin0.privatemines.mine.data.MineData;
import me.untouchedodin0.privatemines.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.region.IWrappedRegion;
import redempt.redlib.misc.LocationUtils;
import redempt.redlib.misc.Task;
import redempt.redlib.misc.WeightedRandom;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

public class Mine {

    private final PrivateMines privateMines;

    private UUID mineOwner;
    private BlockVector3 location;
    private IWrappedRegion iWrappedMiningRegion;
    private IWrappedRegion iWrappedFullRegion;
    private MineData mineData;
    private Task task;

    public Mine(PrivateMines privateMines) {
        this.privateMines = privateMines;
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

    public IWrappedRegion getiWrappedMiningRegion() {
        return iWrappedMiningRegion;
    }

    public void setiWrappedMiningRegion(IWrappedRegion iWrappedMiningRegion) {
        this.iWrappedMiningRegion = iWrappedMiningRegion;
    }

    public IWrappedRegion getiWrappedFullRegion() {
        return iWrappedFullRegion;
    }

    public void setiWrappedFullRegion(IWrappedRegion iWrappedFullRegion) {
        this.iWrappedFullRegion = iWrappedFullRegion;
    }

    public MineData getMineData() {
        return mineData;
    }

    public void setMineData(MineData mineData) {
        this.mineData = mineData;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public void teleport(Player player) {
        player.teleport(getMineData().getSpawnLocation());
    }

    public void delete() {
        MineData mineData = getMineData();

        Location cornerA = mineData.getMinimumFullRegion();
        Location cornerB = mineData.getMaximumFullRegion();

        World world = cornerA.getWorld();

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
                    if (world != null) {
                        world.getBlockAt(x, y, z).setType(Material.AIR);
                    }
                    blocks++;
                }
            }
        }

        Instant filled = Instant.now();
        Duration durationToFill = Duration.between(start, filled);
        privateMines.getLogger().info(String.format("Time took to fill %d blocks %dms", blocks, durationToFill.toMillis()));
    }

    public void reset() {
        MineData mineData = getMineData();
        MineType mineType = MineConfig.getMineTypes().get(mineData.getMineType());

        Map<Material, Double> materials = mineType.getMaterials();

        final WeightedRandom<Material> randomPattern = WeightedRandom.fromDoubleMap(materials);

        Location cornerA = mineData.getMinimumMining();
        Location cornerB = mineData.getMaximumMining();
        World world = cornerA.getWorld();

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
                    if (world != null) {
                        Material material = randomPattern.roll();
                        world.getBlockAt(x, y, z).setType(material);
                    }
                    blocks++;
                }
            }
        }

        Instant filled = Instant.now();
        Duration durationToFill = Duration.between(start, filled);
        privateMines.getLogger().info(String.format("Time took to fill %d blocks %dms", blocks, durationToFill.toMillis()));
    }

    public void startResetTask() {
        int resetTime = 5;
        this.task = Task.syncRepeating(this::reset, 0L, resetTime * 20L);
    }

    public void saveMineData(Player player, MineData mineData) {
        String fileName = String.format("/%s.yml", player.getUniqueId());

        Path minesDirectory = privateMines.getMinesDirectory();
        File file = new File(minesDirectory + fileName);
        privateMines.getLogger().info("Saving file " + file.getName() + "...");

        try {
            if (file.createNewFile()) {
                privateMines.getLogger().info("Created new file: " + file.getPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

        String mineType = mineData.getMineType();

        UUID owner = player.getUniqueId();
        Location mineLocation = mineData.getMineLocation();
        Location corner1 = mineData.getMinimumMining();
        Location corner2 = mineData.getMaximumMining();
        Location fullRegionMin = mineData.getMinimumFullRegion();
        Location fullRegionMax = mineData.getMaximumFullRegion();

        Location spawn = mineData.getSpawnLocation();

        yml.set("mineOwner", owner.toString());
        yml.set("mineType", mineType);
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
}
