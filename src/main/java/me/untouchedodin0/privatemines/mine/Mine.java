package me.untouchedodin0.privatemines.mine;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mine.data.MineData;
import me.untouchedodin0.privatemines.type.MineType;
import me.untouchedodin0.privatemines.utils.Utils;
import me.untouchedodin0.privatemines.utils.task.Task;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.region.IWrappedRegion;

import java.util.UUID;

public class Mine {

    private final PrivateMines privateMines;
    private final Utils utils;

    private UUID mineOwner;
    private MineType mineType;
    private Region fullRegion;
    private CuboidRegion miningRegion;
    private BlockVector3 miningRegionMinX, miningRegionMinY, miningRegionZ;
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

    public BlockVector3 getMiningRegionMinX() {
        return miningRegionMinX;
    }

    public void setMiningRegionMinX(BlockVector3 miningRegionMinX) {
        this.miningRegionMinX = miningRegionMinX;
    }

    public BlockVector3 getMiningRegionMinY() {
        return miningRegionMinY;
    }

    public void setMiningRegionMinY(BlockVector3 miningRegionMinY) {
        this.miningRegionMinY = miningRegionMinY;
    }

    public BlockVector3 getMiningRegionZ() {
        return miningRegionZ;
    }

    public void setMiningRegionZ(BlockVector3 miningRegionZ) {
        this.miningRegionZ = miningRegionZ;
    }

    public CuboidRegion getMiningRegion() {
        return miningRegion;
    }

    public void setMiningRegion(CuboidRegion cuboidRegion) {
        this.miningRegion = cuboidRegion;
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

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
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
        if (fullRegion == null) {
            privateMines.getLogger().warning("Region was null!");
            return;
        }
        privateMines.getLogger().info(fullRegion.toString());
    }
}
