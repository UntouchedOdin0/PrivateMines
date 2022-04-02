package me.untouchedodin0.privatemines.playershops;

import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Shop {

    //todo Create a shop
    // A shop needs to hold the following things
    // Owner - UUID
    // Prices - Map of Materials and Doubles
    // WorldGuardRegion

    UUID owner;
    Map<Material, Double> prices = new HashMap<>();
    ProtectedCuboidRegion region;

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
}
