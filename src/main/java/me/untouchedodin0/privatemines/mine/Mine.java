package me.untouchedodin0.privatemines.mine;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.function.block.BlockReplace;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.function.visitor.RegionVisitor;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import me.untouchedodin0.kotlin.WorldEditUtils;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mine.data.MineData;
import me.untouchedodin0.privatemines.type.MineType;
import me.untouchedodin0.privatemines.utils.Utils;
import me.untouchedodin0.privatemines.utils.task.Task;
import me.untouchedodin0.privatemines.utils.world.MineWorldManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.codemc.worldguardwrapper.region.IWrappedRegion;

import java.util.Objects;
import java.util.UUID;

public class Mine {

    private final PrivateMines privateMines;
    private final Utils utils;
    private final WorldEditUtils worldEditUtils = new WorldEditUtils();

    private UUID mineOwner;
    private MineType mineType;
    private Region fullRegion;
    private CuboidRegion miningRegion;
    private BlockVector3 location, miningRegionMinX, miningRegionMinY, miningRegionZ;
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

    public void reset() {
        MineWorldManager mineWorldManager = privateMines.getMineWorldManager();
        org.bukkit.World privateMinesWorld = mineWorldManager.getMinesWorld();
        MineData mineData = getMineData();
        me.untouchedodin0.privatemines.utils.regions.CuboidRegion cube = mineData.getMiningRegion();

        World world = BukkitAdapter.adapt(Objects.requireNonNull(privateMinesWorld));
        EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).build();
        final RandomPattern randomPattern = new RandomPattern();
        Pattern pattern = BukkitAdapter.adapt(Material.STONE.createBlockData()).toBaseBlock();

        // Correct.
        Location min = cube.getMinimumPoint();
        Location max = cube.getMaximumPoint();

        BlockVector3 minV3 = BlockVector3.at(min.getBlockX(), min.getBlockY(), min.getBlockZ());
        BlockVector3 maxV3 = BlockVector3.at(max.getBlockX(), max.getBlockY(), max.getBlockZ());
        CuboidRegion cuboidRegion = new CuboidRegion(world, minV3, maxV3);

        privateMines.getLogger().info("test: " + mineData.getMiningRegion());
        privateMines.getLogger().info("minV3: " + minV3);
        privateMines.getLogger().info("maxV3: " + maxV3);

        try {
            editSession.setBlocks(cuboidRegion, pattern);
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }
    }
}
