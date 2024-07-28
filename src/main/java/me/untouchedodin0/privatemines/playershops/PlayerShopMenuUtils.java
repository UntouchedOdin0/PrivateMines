package me.untouchedodin0.privatemines.playershops;

import static me.untouchedodin0.privatemines.utils.Utils.format;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import me.untouchedodin0.privatemines.PrivateMines;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PlayerShopMenuUtils {

  PrivateMines privateMines = PrivateMines.getPrivateMines();
  // private Map<Material, Long> shopItems;
  private List<ShopItem> shopItems;
  private Map<Material, Long> quantitySold;

  public void generateMenu(Player player) {
    ShopUtils shopUtils = new ShopUtils();

    // Initialize the GUI
    PaginatedGui paginatedGui = Gui.paginated().title(Component.text("test")).rows(5).pageSize(36)
        .create();
    paginatedGui.setDefaultClickAction(event -> event.setCancelled(true));

    shopItems = shopUtils.getShopItems(player.getUniqueId());
    quantitySold = new HashMap<>();

    // Previous item
    paginatedGui.setItem(5, 3, ItemBuilder.from(Material.PAPER).name(Component.text("Previous"))
        .asGuiItem(event -> paginatedGui.previous()));

    // Sell All Button
    paginatedGui.setItem(5, 5, ItemBuilder.from(Material.CHEST).name(Component.text("Sell All"))
        .lore(List.of(Component.text("Click to sell all your"),
            Component.text("items in your shop!"))).asGuiItem(event -> {
          player.sendMessage(ChatColor.GREEN + "Selling all items!");

          shopItems.forEach(shopItem -> {
            quantitySold.put(shopItem.getItem(), shopItem.getQuantity());
            ShopUtils.removeItem(player.getUniqueId(), shopItem.getItem(), shopItem.getQuantity());
          });
          shopItems.clear();
          quantitySold.forEach((material, aLong) -> {
            player.sendMessage("Sold " + aLong + "x " + material.name());
          });

          List<GuiItem> items = paginatedGui.getPageItems().stream().filter(item -> {
            String itemName = Objects.requireNonNull(ChatColor.stripColor(
                Objects.requireNonNull(item.getItemStack().getItemMeta()).getDisplayName()));
            return !(itemName.equals("Previous") || itemName.equals("Sell All") || itemName.equals(
                "Next"));
          }).toList();

          // Send a message with sold quantities
//          if (!quantitySold.isEmpty()) {
//            StringBuilder soldMessage = new StringBuilder(ChatColor.GREEN + "Sold items: ");
//            quantitySold.forEach(
//                (material, quantity) -> soldMessage.append(material.name()).append(" x")
//                    .append(quantity).append(", "));
//            // Remove trailing comma and space
//            if (soldMessage.length() > 2) {
//              soldMessage.setLength(soldMessage.length() - 2);
//            }
//
//
//            player.sendMessage(soldMessage.toString());
//          }

          refreshGui(player, paginatedGui);
        }));

    // Next item
    paginatedGui.setItem(5, 7, ItemBuilder.from(Material.PAPER).name(Component.text("Next"))
        .asGuiItem(event -> paginatedGui.next()));

    shopItems.forEach(shopItem -> {
      if (shopItem.getQuantity() > 0) {
        double price = shopItem.getPrice();
        Material material = shopItem.getItem();
        long quantity = shopItem.getQuantity();

        GuiItem guiItem = ItemBuilder.from(material).name(Component.text(format(material))).lore(
                List.of(
                    Component.text("Quantity: " + shopItem.getQuantity()).color(NamedTextColor.GRAY),
                    Component.text("Price: " + price), Component.text(" "),
                    Component.text("Click to sell all!").color(NamedTextColor.GREEN)))
            .asGuiItem(event -> {
              quantitySold.put(material, quantity); // Update quantitySold map

              // Remove the item from shopItems
              shopItems.remove(material);

              // Remove item from database
//              ShopUtils.removeItem(player.getUniqueId(), material, quantity);

              // Send a message about the sold item
              player.sendMessage(
                  ChatColor.GREEN + "Sold item: " + material.name() + " x" + quantity);

              // Refresh the menu
              refreshGui(player, paginatedGui);
            });
        paginatedGui.addItem(guiItem);
      }
    });

    paginatedGui.open(player);
  }

  private void refreshGui(Player player, PaginatedGui paginatedGui) {
    paginatedGui.clearPageItems(true);
    // Repopulate the GUI with the remaining items from shopItems

    shopItems.forEach(shopItem -> {
      Material material = shopItem.getItem();
      long quantity = shopItem.getQuantity();

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
}
//    shopItems.forEach((material, quantity) -> {
//      GuiItem guiItem = ItemBuilder.from(material).name(Component.text("Item: " + material.name()))
//          .lore(List.of(Component.text("Quantity: " + quantity), Component.text(" "),
//              Component.text("Click to sell all!"))).asGuiItem(event -> {
//            quantitySold.put(material, quantity); // Update quantitySold map
//
//            // Remove the item from shopItems
//            shopItems.remove(material);
//
//            // Remove item from database
//            ShopUtils.removeItem(player.getUniqueId(), material, quantity);
//
//            // Send a message about the sold item
//            player.sendMessage(ChatColor.GREEN + "Sold item: " + material.name() + " x" + quantity);
//
//            // Refresh the menu
//            refreshGui(player, paginatedGui);
//          });
//      paginatedGui.addItem(guiItem);

