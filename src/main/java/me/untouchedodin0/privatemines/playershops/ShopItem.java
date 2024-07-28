package me.untouchedodin0.privatemines.playershops;

import java.util.UUID;
import org.bukkit.Material;

public class ShopItem {

  private Material item;
  private long quantity;
  private double price;
  private double tax;

  public ShopItem(Material material, long quantity, double price, double tax) {
    this.item = material;
    this.quantity = quantity;
    this.price = price;
    this.tax = tax;
  }

  public Material getItem() {
    return item;
  }

  public long getQuantity() {
    return quantity;
  }

  public double getPrice() {
    return price;
  }

  public double getTax() {
    return tax;
  }
}
