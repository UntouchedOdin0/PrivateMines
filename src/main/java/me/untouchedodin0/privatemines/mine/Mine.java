package me.untouchedodin0.privatemines.mine;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.RegionFunction;
import com.sk89q.worldedit.function.block.BlockReplace;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.function.visitor.RegionVisitor;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockState;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mine.data.MineData;
import me.untouchedodin0.privatemines.type.MineType;
import me.untouchedodin0.privatemines.utils.Utils;
import me.untouchedodin0.privatemines.utils.regions.CuboidRegion;
import me.untouchedodin0.privatemines.utils.task.Task;
import org.bukkit.Location;
import org.bukkit.Material;
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
        CuboidRegion fullRegion = getMineData().getFullRegion();
//        if (fullRegion == null) {
//            privateMines.getLogger().warning("Region was null!");
//            return;
//        }
        privateMines.getLogger().info("fullRegion: " + fullRegion);
        privateMines.getMineStorage().removeMine(getMineOwner());
    }

    public void reset() {
        MineData mineData = getMineData();
        CuboidRegion miningRegion = mineData.getMiningRegion();
        World world = BukkitAdapter.adapt(privateMines.getMineWorldManager().getMinesWorld());
        EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).build();

        if (miningRegion == null) {
            privateMines.getLogger().info("Mining region was null!");
        }

        privateMines.getLogger().info("miningRegion: " + miningRegion);
        privateMines.getLogger().info("miningRegion min: " + Objects.requireNonNull(miningRegion).getMinimumPoint());
        privateMines.getLogger().info("miningRegion max: " + miningRegion.getMaximumPoint());


        BlockVector3 minimumVector = BlockVector3.at(miningRegion.getMinimumPoint().getBlockX(),
                                                     miningRegion.getMinimumPoint().getBlockY(),
                                                     miningRegion.getMinimumPoint().getBlockZ());
        BlockVector3 maximumVector = BlockVector3.at(miningRegion.getMaximumPoint().getBlockX(),
                                                     miningRegion.getMaximumPoint().getBlockY(),
                                                     miningRegion.getMaximumPoint().getBlockZ());

        com.sk89q.worldedit.regions.CuboidRegion cuboidRegion = new com.sk89q.worldedit.regions.CuboidRegion(minimumVector, maximumVector);

        privateMines.getLogger().info("minimum vector: " + minimumVector);
        privateMines.getLogger().info("maximum vector: " + maximumVector);

        privateMines.getLogger().info("cuboid region: " + cuboidRegion);
        RandomPattern randomPattern = new RandomPattern(); // Create the random pattern
        BlockState stone = BukkitAdapter.adapt(Material.STONE.createBlockData());
        randomPattern.add(stone, 1.0);

        RegionFunction set = new BlockReplace(editSession, randomPattern);
        RegionVisitor regionVisitor = new RegionVisitor(cuboidRegion, set);
        try {
            Operations.completeLegacy(regionVisitor);
        } catch (MaxChangedBlocksException e) {
            e.printStackTrace();
        }
    }
}
