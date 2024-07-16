/**
 * MIT License
 *
 * Copyright (c) 2021 - 2023 Kyle Hicks
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

package me.untouchedodin0.privatemines.playershops;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class Shop {

    //todo Create a shop
    // A shop needs to hold the following things
    // Owner - UUID
    // Prices - Map of Materials and Doubles
    // WorldGuardRegion

    UUID owner;
    Map<Material, Double> prices = new HashMap<>();
    ProtectedRegion region;

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    public Map<Material, Double> getPrices() {
        return prices;
    }

    public void setPrice(Material material, Double price) {
        Player player = Bukkit.getPlayer(getOwner());
        if (prices.containsKey(material)) {
            prices.put(material, price);
            if (player != null) {
                player.sendMessage(ChatColor.GREEN + "Updated prices of " + material + " to $" + price);
            }
        }
    }

    public void setRegion(ProtectedRegion region) {
        this.region = region;
    }

    public void setPrices(Map<Material, Double> prices) {
        this.prices = prices;
    }
}
