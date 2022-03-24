package me.untouchedodin0.privatemines.mine;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mine.data.MineData;
import me.untouchedodin0.privatemines.type.MineType;
import me.untouchedodin0.privatemines.utils.Utils;
import me.untouchedodin0.privatemines.utils.regions.CuboidRegion;
import me.untouchedodin0.privatemines.utils.task.Task;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.region.IWrappedRegion;

import java.util.Objects;
import java.util.UUID;

public class Mine {

    private final PrivateMines privateMines;
    private final Utils utils;

    private UUID mineOwner;
    private MineType mineType;
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
        privateMines.getMineStorage().removeMine(getMineOwner());
    }

    public void reset() {
        MineData mineData = getMineData();
        Location cornerA = mineData.getMinimumMining();
        Location cornerB = mineData.getMaximumMining();
        World world = cornerA.getWorld();

        privateMines.getLogger().info("min " + mineData.getMinimumMining());
        privateMines.getLogger().info("max " + mineData.getMaximumMining());

        privateMines.getLogger().info("cornerA: " + cornerA);
        privateMines.getLogger().info("cornerB: " + cornerB);

        int xMax = Integer.max(cornerA.getBlockX(), cornerB.getBlockX());
        int xMin = Integer.min(cornerA.getBlockX(), cornerB.getBlockX());
        int yMax = Integer.max(cornerA.getBlockY(), cornerB.getBlockY());
        int yMin = Integer.min(cornerA.getBlockY(), cornerB.getBlockY());
        int zMax = Integer.max(cornerA.getBlockZ(), cornerB.getBlockZ());
        int zMin = Integer.min(cornerA.getBlockZ(), cornerB.getBlockZ());

        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                for (int z = zMin; z <= zMax; z++) {
                    world.getBlockAt(x, y, z).setType(Material.STONE);
                }
            }
        }

//        World world = BukkitAdapter.adapt(privateMines.getMineWorldManager().getMinesWorld());
//        EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).build();
//
//        if (miningRegion == null) {
//            privateMines.getLogger().info("Mining region was null!");
//        }
//
//        privateMines.getLogger().info("miningRegion: " + miningRegion);
//        privateMines.getLogger().info("miningRegion min: " + Objects.requireNonNull(miningRegion).getMinimumPoint());
//        privateMines.getLogger().info("miningRegion max: " + miningRegion.getMaximumPoint());
//
//
//        privateMines.getLogger().info("cuboid region: " + miningRegion.getMinimumPoint() + " " + miningRegion.getMaximumPoint());
//        RandomPattern randomPattern = new RandomPattern(); // Create the random pattern
//        BlockState stone = BukkitAdapter.adapt(Material.STONE.createBlockData());
//        randomPattern.add(stone, 1.0);
//
//        RegionFunction set = new BlockReplace(editSession, randomPattern);
//        RegionVisitor regionVisitor = new RegionVisitor(miningRegion, set);
//        try {
//            Operations.completeLegacy(regionVisitor);
//        } catch (MaxChangedBlocksException e) {
//            e.printStackTrace();
//        }
//    }
    }
}
