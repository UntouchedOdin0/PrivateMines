package me.untouchedodin0.privatemines.mine.data;

import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.playershops.Shop;
import org.bukkit.Location;
import redempt.redlib.region.CuboidRegion;

import java.util.UUID;

public class MineDataBuilder {

    UUID mineOwner;
    Location mineLocation;
    Location spawnLocation;
    Location npcLocation;
    Location minimumMining;
    Location maximumMining;
    Location minimumFullRegion;
    Location maximumFullRegion;

    CuboidRegion miningRegion;
    MineType mineType;

    Shop shop = new Shop();

    boolean isOpen;
    double tax = 5;

    public MineDataBuilder setOwner(UUID uuid) {
        this.mineOwner = uuid;
        return this;
    }

    public MineDataBuilder setMineLocation(Location location) {
        this.mineLocation = location;
        return this;
    }

    public MineDataBuilder setSpawnLocation(Location location) {
        this.spawnLocation = location;
        return this;
    }

    public MineDataBuilder setNpcLocation(Location location) {
        this.npcLocation = location;
        return this;
    }

    public MineDataBuilder setMinimumMining(Location location) {
        this.minimumMining = location;
        return this;
    }

    public MineDataBuilder setMaximumMining(Location location) {
        this.maximumMining = location;
        return this;
    }

    public MineDataBuilder setMinimumFullRegion(Location location) {
        this.minimumFullRegion = location;
        return this;
    }

    public MineDataBuilder setMaximumFullRegion(Location location) {
        this.maximumFullRegion = location;
        return this;
    }

    public MineDataBuilder setMineType(MineType mineType) {
        this.mineType = mineType;
        return this;
    }

    public MineDataBuilder setOpen(boolean isOpen) {
        this.isOpen = isOpen;
        return this;
    }

    public MineDataBuilder setTax(double tax) {
        this.tax = tax;
        return this;
    }

    public MineDataBuilder setShop(Shop shop) {
        this.shop = shop;
        return this;
    }

    public MineData build() {
        // Create the mine data object and return it
        MineData mineData = new MineData();
        mineData.mineOwner = mineOwner;
        mineData.mineLocation = mineLocation;
        mineData.spawnLocation = spawnLocation;
        mineData.npcLocation = npcLocation;
        mineData.minimumMining = minimumMining;
        mineData.maximumMining = maximumMining;
        mineData.minimumFullRegion = minimumFullRegion;
        mineData.maximumFullRegion = maximumFullRegion;
        mineData.miningRegion = miningRegion;
        mineData.mineType = mineType;
        mineData.isOpen = isOpen;
        mineData.tax = tax;
        mineData.shop = shop;
        return mineData;
    }
}
