package me.untouchedodin0.privatemines.mine.data;

import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.playershops.Shop;
import org.bukkit.Location;
import org.bukkit.Material;
import redempt.redlib.region.CuboidRegion;

import java.util.*;

public class MineData {

    UUID mineOwner;
    UUID coOwner;
    Location mineLocation;
    Location spawnLocation;
    Location npcLocation;
    Location minimumMining;
    Location maximumMining;
    CuboidRegion miningRegion;
    Location minimumFullRegion;
    Location maximumFullRegion;
    MineType mineType;

    Shop shop;
    boolean isOpen;
    double tax = 5;

    Map<Material, Double> materials = new EnumMap<>(Material.class);
    List<UUID> whitelistedPlayers = new ArrayList<>();
    List<UUID> bannedPlayers = new ArrayList<>();
    List<UUID> priorityPlayers = new ArrayList<>();

    public UUID getMineOwner() {
        return mineOwner;
    }

    public Location getMineLocation() {
        return mineLocation;
    }


    public Location getSpawnLocation() {
        return spawnLocation;
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

    public Location getMaximumFullRegion() {
        return maximumFullRegion;
    }

    public MineType getMineType() {
        return mineType;
    }

    public void setShop(Shop shop) {
        this.shop = shop;
    }

    public Shop getShop() {
        return shop;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        this.isOpen = open;
    }

    public double getTax() {
        return tax;
    }

    public void setTax(double tax) {
        this.tax = tax;
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
        if (!bannedPlayers.contains(uuid)) return;
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