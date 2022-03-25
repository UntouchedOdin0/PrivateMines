package me.untouchedodin0.privatemines.mine.data;

import com.sk89q.worldedit.regions.Region;
import me.untouchedodin0.privatemines.utils.regions.CuboidRegion;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.*;

public class MineData {

    UUID mineOwner;
    UUID coOwner;
    Location mineLocation;
    Location spawnLocation;
    Location npcLocation;
    Location minimumMining;
    Location maximumMining;
    Location minimumFullRegion;
    Location maximumFullRegion;

    CuboidRegion miningRegion;
    Region fullRegion;

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

    public Location getMineLocation() {
        return mineLocation;
    }

    public void setMineLocation(Location mineLocation) {
        this.mineLocation = mineLocation;
    }

    public Location getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public Location getMinimumMining() {
        return minimumMining;
    }

    public void setMinimumMining(Location minimumMining) {
        this.minimumMining = minimumMining;
    }

    public Location getMaximumMining() {
        return maximumMining;
    }

    public void setMaximumMining(Location maximumMining) {
        this.maximumMining = maximumMining;
    }

    public Location getMinimumFullRegion() {
        return minimumFullRegion;
    }

    public void setMinimumFullRegion(Location minimumFullRegion) {
        this.minimumFullRegion = minimumFullRegion;
    }

    public Location getMaximumFullRegion() {
        return maximumFullRegion;
    }

    public void setMaximumFullRegion(Location maximumFullRegion) {
        this.maximumFullRegion = maximumFullRegion;
    }

    public Region getFullRegion() {
        return fullRegion;
    }

    public void setFullRegion(Region region) {
        this.fullRegion = region;
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