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
import me.untouchedodin0.privatemines.mine.quarries.Quarry;
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
    Quarry quarry;
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

    public void setMinimumFullRegion(Location minimumFullRegion) {
        this.minimumFullRegion = minimumFullRegion;
    }

    public Location getMaximumFullRegion() {
        return maximumFullRegion;
    }

    public void setMaximumFullRegion(Location maximumFullRegion) {
        this.maximumFullRegion = maximumFullRegion;
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