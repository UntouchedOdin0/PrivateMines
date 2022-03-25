package me.untouchedodin0.privatemines.mine;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mine.data.MineData;
import me.untouchedodin0.privatemines.utils.Utils;
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
import java.util.Map;
import java.util.UUID;

public class Mine {

    private final PrivateMines privateMines;
    private final Utils utils;

    private UUID mineOwner;
    private MineType mineType;
    private String mineTypeName;
    private BlockVector3 location;
    private IWrappedRegion iWrappedMiningRegion;
    private IWrappedRegion iWrappedFullRegion;
    private Location spawnLocation;
    private MineData mineData;
    private Task task;
    private double tax = 5;

    public Mine(PrivateMines privateMines) {
        this.privateMines = privateMines;
        this.utils = new Utils(privateMines);
    }

    public UUID getMineOwner() {
        return mineOwner;
    }

    public void setMineOwner(UUID uuid) {
        this.mineOwner = uuid;
    }

    public MineType getMineType() {
        return mineType;
    }

    public void setMineType(MineType mineType) {
        this.mineType = mineType;
    }

    public String getMineTypeName() {
        return mineTypeName;
    }

    public void setMineTypeName(String mineTypeName) {
        this.mineTypeName = mineTypeName;
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

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
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

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
    }

    public void teleport(Player player) {
        player.teleport(getSpawnLocation());
    }

    public void delete() {
//        if (fullRegion == null) {
//            privateMines.getLogger().warning("Region was null!");
//            return;
//        }
//        privateMines.getLogger().info("fullRegion: " + fullRegion);
        com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(privateMines.getMineWorldManager().getMinesWorld());
        MineData mineData = getMineData();

        EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).build();
        try {
            editSession.setBlocks(mineData.getFullRegion(), BukkitAdapter.adapt(Material.AIR.createBlockData()));
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }

        privateMines.getLogger().info("full region min: " + mineData.getFullRegion().getMinimumPoint());
        privateMines.getLogger().info("full region max: " + mineData.getFullRegion().getMaximumPoint());
    }

    public void reset() {
        MineData mineData = getMineData();
        Map<Material, Double> materials = mineData.getMaterials();
        WeightedRandom weightedRandom = new WeightedRandom<>();

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
                        world.getBlockAt(x, y, z).setType(Material.STONE);
                    }
                    blocks++;
                }
            }
        }

        Instant filled = Instant.now();
        Duration durationToFill = Duration.between(start, filled);
        privateMines.getLogger().info(String.format("Time took to fill %d blocks %dms", blocks, durationToFill.toMillis()));
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

        Location spawn = mineData.getSpawnLocation();

        privateMines.getLogger().info("mineTypename: " + mineTypeName);
        privateMines.getLogger().info("mineLocation save: " + mineLocation);
        privateMines.getLogger().info("corner1 save: " + corner1);
        privateMines.getLogger().info("corner2 save: " + corner2);
        privateMines.getLogger().info("spawn save: " + spawn);

        yml.set("mineOwner", owner.toString());
        yml.set("mineType", mineType);
        yml.set("mineLocation", LocationUtils.toString(mineLocation));
        yml.set("corner1", LocationUtils.toString(corner1));
        yml.set("corner2", LocationUtils.toString(corner2));
        yml.set("spawn", LocationUtils.toString(spawn));

        try {
            yml.save(file);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }
}
