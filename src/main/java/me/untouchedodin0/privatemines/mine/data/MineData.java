package me.untouchedodin0.privatemines.mine.data;

import me.untouchedodin0.privatemines.utils.regions.CuboidRegion;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.*;

public class MineData {

    UUID mineOwner;
    UUID coOwner;
    Location spawnLocation;
    Location npcLocation;

    CuboidRegion miningRegion;
    CuboidRegion fullRegion;

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

    public CuboidRegion getMiningRegion() {
        return miningRegion;
    }

    public void setMiningRegion(CuboidRegion cuboidRegion) {
        this.miningRegion = cuboidRegion;
    }

    public CuboidRegion getFullRegion() {
        return fullRegion;
    }

    public void setFullRegion(CuboidRegion region) {
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