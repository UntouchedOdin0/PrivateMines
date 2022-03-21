package me.untouchedodin0.privatemines.mine;

import com.sk89q.worldedit.math.BlockVector3;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mine.data.MineData;
import me.untouchedodin0.privatemines.type.MineType;
import me.untouchedodin0.privatemines.utils.Utils;
import me.untouchedodin0.privatemines.utils.task.Task;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.region.IWrappedRegion;

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
        me.untouchedodin0.privatemines.utils.regions.CuboidRegion fullRegion = getMineData().getFullRegion();
//        if (fullRegion == null) {
//            privateMines.getLogger().warning("Region was null!");
//            return;
//        }
        privateMines.getLogger().info("fullRegion: " + fullRegion);
        privateMines.getMineStorage().removeMine(getMineOwner());
    }

    public void reset() {
        MineData mineData = getMineData();
        me.untouchedodin0.privatemines.utils.regions.CuboidRegion miningRegion = mineData.getMiningRegion();
        privateMines.getLogger().info("miningRegion: " + miningRegion);
        privateMines.getLogger().info("miningRegion min: " + miningRegion.getMinimumPoint());
        privateMines.getLogger().info("miningRegion max: " + miningRegion.getMaximumPoint());

        Location minimumPoint = miningRegion.getMinimumPoint();
        Location maximumPoint = miningRegion.getMaximumPoint();
        World world = miningRegion.getWorld();

        miningRegion.stream().forEach(block -> {
            block.setType(Material.DIRT);
        });

//        for(int x = maximumPoint.getBlockX(); x < minimumPoint.getBlockX(); x++){
//            for(int y = maximumPoint.getBlockY(); y < minimumPoint.getBlockY(); y++){
//                for(int z = maximumPoint.getBlockZ(); z < minimumPoint.getBlockZ(); z++){
////                    privateMines.getLogger().info(String.format("%d %d %d", x, y, z));
//                    world.getBlockAt(x, y, z).setType(Material.DIAMOND_BLOCK);
//                }
//            }
//        }

        miningRegion.getMinimumPoint().getBlock().setType(Material.EMERALD_BLOCK);
        miningRegion.getMaximumPoint().getBlock().setType(Material.DIAMOND_BLOCK);
    }
}
