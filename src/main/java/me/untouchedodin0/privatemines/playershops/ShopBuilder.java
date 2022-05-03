package me.untouchedodin0.privatemines.playershops;

import com.sk89q.worldedit.regions.Region;
import org.bukkit.Material;

import java.util.Map;
import java.util.UUID;

public class ShopBuilder {

    UUID owner;
    Region region;
    Map<Material, Double> prices;

    public ShopBuilder setOwner(UUID owner) {
        this.owner = owner;
        return this;
    }

    public ShopBuilder setRegion(Region region) {
        this.region = region;
        return this;
    }

    public ShopBuilder setPrices(Map<Material, Double> prices) {
        this.prices = prices;
        return this;
    }

    public Shop build() {
        Shop shop = new Shop();
        shop.setOwner(owner);
        shop.setRegion(region);
        shop.setPrices(prices);
        return shop;
    }
}
