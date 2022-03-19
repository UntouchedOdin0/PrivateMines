package me.untouchedodin0.privatemines.mine.data;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.World;
import me.untouchedodin0.privatemines.PrivateMines;
import org.bukkit.Material;

import java.util.*;

public class MineData {

    UUID mineOwner;
    UUID coOwner;

    int spawnX;
    int spawnY;
    int spawnZ;

    int miningRegionMinX;
    int miningRegionMinY;
    int miningRegionMinZ;

    int miningRegionMaxX;
    int miningRegionMaxY;
    int miningRegionMaxZ;

    int fullRegionMinX;
    int fullRegionMinY;
    int fullRegionMinZ;

    int fullRegionMaxX;
    int fullRegionMaxY;
    int fullRegionMaxZ;

    String worldName;
    String mineType;

    boolean isOpen;

    Map<Material, Double> materials = new EnumMap<>(Material.class);
    List<UUID> whitelistedPlayers = new ArrayList<>();
    List<UUID> bannedPlayers = new ArrayList<>();
    List<UUID> priorityPlayers = new ArrayList<>();

    public UUID getMineOwner() {
        return mineOwner;
    }

    public void setMineOwner(UUID mineOwner) {
        this.mineOwner = mineOwner;
    }

    public UUID getCoOwner() {
        return coOwner;
    }

    public void setCoOwner(UUID coOwner) {
        this.coOwner = coOwner;
    }

    public int getSpawnX() {
        return spawnX;
    }

    public void setSpawnX(int spawnX) {
        this.spawnX = spawnX;
    }

    public int getSpawnY() {
        return spawnY;
    }

    public void setSpawnY(int spawnY) {
        this.spawnY = spawnY;
    }

    public int getSpawnZ() {
        return spawnZ;
    }

    public void setSpawnZ(int spawnZ) {
        this.spawnZ = spawnZ;
    }

    public int getMiningRegionMinX() {
        return miningRegionMinX;
    }

    public void setMiningRegionMinX(int minX) {
        this.miningRegionMinX = minX;
    }

    public int getMiningRegionMinY() {
        return miningRegionMinY;
    }

    public void setMiningRegionMinY(int minY) {
        this.miningRegionMinY = minY;
    }

    public int getMiningRegionMinZ() {
        return miningRegionMinZ;
    }

    public void setMiningRegionMinZ(int minZ) {
        this.miningRegionMinZ = minZ;
    }

    public int getMiningRegionMaxX() {
        return miningRegionMaxX;
    }

    public void setMiningRegionMaxX(int maxX) {
        this.miningRegionMaxX = maxX;
    }

    public int getMiningRegionMaxY() {
        return miningRegionMaxY;
    }

    public void setMiningRegionMaxY(int miningRegionMaxY) {
        this.miningRegionMaxY = miningRegionMaxY;
    }

    public int getMiningRegionMaxZ() {
        return miningRegionMaxZ;
    }

    public void setMiningRegionMaxZ(int miningRegionMaxZ) {
        this.miningRegionMaxZ = miningRegionMaxZ;
    }

    public void setFullRegionMinX(int fullRegionMinX) {
        this.fullRegionMinX = fullRegionMinX;
    }

    public int getFullRegionMinX() {
        return fullRegionMinX;
    }

    public void setFullRegionMinY(int fullRegionMinY) {
        this.fullRegionMinY = fullRegionMinY;
    }

    public int getFullRegionMinY() {
        return fullRegionMinY;
    }

    public void setFullRegionMinZ(int fullRegionMinZ) {
        this.fullRegionMinZ = fullRegionMinZ;
    }

    public int getFullRegionMinZ() {
        return fullRegionMinZ;
    }

    public void setFullRegionMaxX(int fullRegionMinX) {
        this.fullRegionMinX = fullRegionMinX;
    }

    public int getFullRegionMaxX() {
        return fullRegionMaxX;
    }

    public void setFullRegionMaxY(int fullRegionMinY) {
        this.fullRegionMinY = fullRegionMinY;
    }

    public int getFullRegionMaxY() {
        return fullRegionMaxY;
    }

    public void setFullRegionMaxZ(int fullRegionMinZ) {
        this.fullRegionMinZ = fullRegionMinZ;
    }

    public int getFullRegionMaxZ() {
        return fullRegionMaxZ;
    }

    public CuboidRegion getMiningRegion() {
        BlockVector3 min = BlockVector3.at(getMiningRegionMinX(), getMiningRegionMinY(), getMiningRegionMinZ());
        BlockVector3 max = BlockVector3.at(getMiningRegionMaxX(), getMiningRegionMaxY(), getMiningRegionMaxZ());
        return new CuboidRegion(min, max);
    }

    public CuboidRegion getFullRegion() {
        BlockVector3 min = BlockVector3.at(getFullRegionMinX(), getFullRegionMinY(), getFullRegionMinZ());
        BlockVector3 max = BlockVector3.at(getFullRegionMaxX(), getFullRegionMaxY(), getFullRegionMaxZ());
        return new CuboidRegion(min, max);
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public String getMineType() {
        return mineType;
    }

    public void setMineType(String mineType) {
        this.mineType = mineType;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        this.isOpen = open;
    }

    public Map<Material, Double> getMaterials() {
        return materials;
    }

    public void setMaterials(Map<Material, Double> materials) {
        this.materials = materials;
    }

    public void addWhitelistedPlayer(UUID uuid) {
        if (whitelistedPlayers.contains(uuid)) return;
        whitelistedPlayers.add(uuid);
    }

    public void removeWhitelistedPlayer(UUID uuid) {
        whitelistedPlayers.remove(uuid);
    }

    public List<UUID> getWhitelistedPlayers() {
        return whitelistedPlayers;
    }

    public void addBannedPlayer(UUID uuid) {
        if (bannedPlayers.contains(uuid)) return;
        bannedPlayers.add(uuid);
    }

    public void removeBannedPlayer(UUID uuid) {
        bannedPlayers.remove(uuid);
    }

    public List<UUID> getBannedPlayers() {
        return bannedPlayers;
    }

    public void addPriorityPlayer(UUID uuid) {
        if (priorityPlayers.contains(uuid)) return;
        priorityPlayers.add(uuid);
    }

    public void removePriorityPlayer(UUID uuid) {
        priorityPlayers.remove(uuid);
    }

    public List<UUID> getPriorityPlayers() {
        return priorityPlayers;
    }
}