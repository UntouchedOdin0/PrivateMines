package me.untouchedodin0.privatemines.playershops;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import me.untouchedodin0.privatemines.PrivateMines;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import redempt.redlib.sql.SQLHelper;
import redempt.redlib.sql.SQLHelper.Results;

public class PlayerShopMenuUtils {

  PrivateMines privateMines = PrivateMines.getPrivateMines();
  private Map<Material, Integer> shopItems;
  private Map<Material, Integer> quantitySold;

  public void generateMenu(Player player) {
    // Initialize the GUI
    PaginatedGui paginatedGui = Gui.paginated().title(Component.text("test")).rows(5).pageSize(36)
        .create();
    paginatedGui.setDefaultClickAction(event -> event.setCancelled(true));

    shopItems = getShopItems(player.getUniqueId());
    quantitySold = new HashMap<>();

    // Previous item
    paginatedGui.setItem(5, 3, ItemBuilder.from(Material.PAPER).name(Component.text("Previous"))
        .asGuiItem(event -> paginatedGui.previous()));

    // Sell All Button
    paginatedGui.setItem(5, 5, ItemBuilder.from(Material.CHEST).name(Component.text("Sell All"))
        .lore(List.of(Component.text("Click to sell all your"),
            Component.text("items in your shop!"))).asGuiItem(event -> {
          player.sendMessage(ChatColor.GREEN + "Selling all items!");

          List<GuiItem> items = paginatedGui.getPageItems().stream().filter(item -> {
            String itemName = Objects.requireNonNull(ChatColor.stripColor(
                Objects.requireNonNull(item.getItemStack().getItemMeta()).getDisplayName()));
            return !(itemName.equals("Previous") || itemName.equals("Sell All") || itemName.equals(
                "Next"));
          }).toList();

          for (GuiItem guiItem : items) {
            Material material = guiItem.getItemStack().getType();
            int quantity = shopItems.get(material);

            ShopUtils.removeItem(player.getUniqueId(), material, quantity);
            quantitySold.put(material, quantity); // Update quantitySold map
            shopItems.remove(material);
          }

          // Send a message with sold quantities
          if (!quantitySold.isEmpty()) {
            StringBuilder soldMessage = new StringBuilder(ChatColor.GREEN + "Sold items: ");
            quantitySold.forEach(
                (material, quantity) -> soldMessage.append(material.name()).append(" x")
                    .append(quantity).append(", "));
            // Remove trailing comma and space
            if (soldMessage.length() > 2) {
              soldMessage.setLength(soldMessage.length() - 2);
            }
            player.sendMessage(soldMessage.toString());
          }

          refreshGui(player, paginatedGui);
        }));

    // Next item
    paginatedGui.setItem(5, 7, ItemBuilder.from(Material.PAPER).name(Component.text("Next"))
        .asGuiItem(event -> paginatedGui.next()));

    shopItems.forEach((material, quantity) -> {
      GuiItem guiItem = ItemBuilder.from(material).name(Component.text("Item: " + material.name()))
          .lore(List.of(Component.text("Quantity: " + shopItems.get(material)), Component.text(" "),
              Component.text("Click to sell all!"))).asGuiItem(event -> {
            quantitySold.put(material, quantity); // Update quantitySold map

            // Remove the item from shopItems
            shopItems.remove(material);

            // Remove item from database
            ShopUtils.removeItem(player.getUniqueId(), material, quantity);

            // Send a message about the sold item
            player.sendMessage(ChatColor.GREEN + "Sold item: " + material.name() + " x" + quantity);

            // Refresh the menu
            refreshGui(player, paginatedGui);
          });
      paginatedGui.addItem(guiItem);
    });

    paginatedGui.open(player);
  }

  private void refreshGui(Player player, PaginatedGui paginatedGui) {
    paginatedGui.clearPageItems(true);
    // Repopulate the GUI with the remaining items from shopItems
    shopItems.forEach((material, quantity) -> {
      GuiItem guiItem = ItemBuilder.from(material).name(Component.text("Item: " + material.name()))
          .lore(List.of(Component.text("Quantity: " + quantity), Component.text(" "),
              Component.text("Click to sell all!"))).asGuiItem(event -> {
            quantitySold.put(material, quantity); // Update quantitySold map

            // Remove the item from shopItems
            shopItems.remove(material);

            // Remove item from database
            ShopUtils.removeItem(player.getUniqueId(), material, quantity);

            // Send a message about the sold item
            player.sendMessage(ChatColor.GREEN + "Sold item: " + material.name() + " x" + quantity);

            // Refresh the menu
            refreshGui(player, paginatedGui);
          });
      paginatedGui.addItem(guiItem);
    });
  }

  public Map<Material, Integer> getShopItems(UUID uuid) {
    SQLHelper sqlHelper = privateMines.getSqlHelper();
    String ownerUUID = uuid.toString();
    Map<Material, Integer> shopItems = new HashMap<>();

    Results results = sqlHelper.queryResults("SELECT * FROM shops WHERE owner=?;", ownerUUID);

    if (results == null) {
      Bukkit.broadcastMessage("No results found.");
      return null;
    }

    results.forEach(result -> {
      String owner = result.getString(1);
      String seller = result.getString(2);
      String item = result.getString(3);
      int quantity = result.get(4);

      Material material = Material.getMaterial(item);
      if (material != null) {
        shopItems.put(material, quantity);
      } else {
        Bukkit.broadcastMessage("Invalid material: " + item);
      }
    });
    return shopItems;
  }
}
