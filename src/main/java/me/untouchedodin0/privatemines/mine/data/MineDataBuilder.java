/**
 * MIT License
 *
 * Copyright (c) 2021 - 2022 Kyle Hicks
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
