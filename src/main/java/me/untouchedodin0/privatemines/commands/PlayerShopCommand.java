package me.untouchedodin0.privatemines.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.google.common.util.concurrent.AtomicDouble;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.PaginatedGui;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.untouchedodin0.kotlin.mine.data.MineData;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.kotlin.utils.AudienceUtils;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.config.MessagesConfig;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.playershops.Shop;
import me.untouchedodin0.privatemines.playershops.ShopUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import redempt.redlib.misc.Task;
import redempt.redlib.sql.SQLHelper;
import redempt.redlib.sql.SQLHelper.Results;

@CommandAlias("playershop|playershops|pshop")
public class PlayerShopCommand extends BaseCommand {

  PrivateMines privateMines = PrivateMines.getPrivateMines();
  MineStorage mineStorage = privateMines.getMineStorage();
  AudienceUtils audienceUtils = new AudienceUtils();

  @Default
  @CommandPermission("privatemines.playershop")
  public synchronized void playerShop(Player player) {
    Map<Material, Integer> shopItems = new HashMap<>();

    if (!mineStorage.hasMine(player)) {
      audienceUtils.sendMessage(player, MessagesConfig.dontOwnMine);
      return;
    }

    Mine mine = mineStorage.get(player);
    if (mine == null) {
      audienceUtils.sendMessage(player, MessagesConfig.dontOwnMine);
      return;
    }

    MineData mineData = mine.getMineData();
    Shop shop = mineData.getShop();

    // Initialize the GUI
    PaginatedGui paginatedGui = Gui
        .paginated()
        .title(Component.text("test"))
        .rows(5)
        .pageSize(36)
        .create();

    paginatedGui.setDefaultClickAction(event -> event.setCancelled(true));

    // Previous item
    paginatedGui.setItem(5, 3, ItemBuilder.from(Material.PAPER).setName("Previous")
        .asGuiItem(event -> paginatedGui.previous()));
    // Next item
    paginatedGui.setItem(5, 7, ItemBuilder.from(Material.PAPER).setName("Next")
        .asGuiItem(event -> paginatedGui.next()));

    // Asynchronous task to fetch shop data
    Task.asyncDelayed(() -> {
      SQLHelper sqlHelper = privateMines.getSqlHelper();
      String ownerUUID = player.getUniqueId().toString();
      Results results = sqlHelper.queryResults("SELECT * FROM shops WHERE owner=?;", ownerUUID);

      if (results == null) {
        Bukkit.broadcastMessage("No results found.");
        return;
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

      // Use a synchronous task to update the shopItems map
      Bukkit.getScheduler().runTask(PrivateMines.getPrivateMines(), () -> {
//        for (Material material : Material.values()) {
//          if (material.isBlock()) {
//            GuiItem guiItem = ItemBuilder.from(material)
//                .name(Component.text("test"))
//                .lore(Component.text("Quantity: " + shopItems.get(material)))
//                .asGuiItem();
//            paginatedGui.addItem(guiItem);
//          }
//        }
        shopItems.forEach((material, quantity) -> {
          GuiItem guiItem = ItemBuilder.from(material)
              .name(Component.text("Item: " + material.name()))
              .lore(Component.text("Quantity: " + quantity))
              .asGuiItem();
          paginatedGui.addItem(guiItem);
        });

        // Open the GUI for the player
        paginatedGui.open(player);
      });
    });
  }


//  @Default
//  @CommandPermission("privatemines.playershop")
//  public synchronized void playerShop(Player player) {
//    Map<Material, Integer> shopItems = new HashMap<>();
//
//    if (!mineStorage.hasMine(player)) {
//      audienceUtils.sendMessage(player, MessagesConfig.dontOwnMine);
//    } else {
//      Mine mine = mineStorage.get(player);
//      AtomicDouble atomicDouble = new AtomicDouble(1);
//
//      if (mine != null) {
//        SQLHelper sqlHelper = privateMines.getSqlHelper();
//        sqlHelper.execute("UPDATE shops SET price=? WHERE item=? AND owner=?;",
//            atomicDouble.getAndAdd(1.0), "COBBLESTONE", player.getUniqueId().toString());
//
//        MineData mineData = mine.getMineData();
//        Shop shop = mineData.getShop();
//
//        player.sendMessage("owner " + shop.getOwner());
//        player.sendMessage("prices " + shop.getPrices());
//
//        PaginatedGui paginatedGui = Gui
//            .paginated()
//            .title(Component.text("test"))
//            .rows(5)
//            .pageSize(36)
//            .create();
//
//        paginatedGui.setDefaultClickAction(event -> event.setCancelled(true));
//
//        // Previous item
//        paginatedGui.setItem(5, 3, ItemBuilder.from(Material.PAPER).setName("Previous")
//            .asGuiItem(event -> paginatedGui.previous()));
//        // Next item
//        paginatedGui.setItem(5, 7, ItemBuilder.from(Material.PAPER).setName("Next")
//            .asGuiItem(event -> paginatedGui.next()));
//
//        // Asynchronous task to fetch shop data
//        Task task = Task.asyncDelayed(() -> {
//          Results results = sqlHelper.queryResults("SELECT * FROM shops WHERE owner=?;",
//              player.getUniqueId().toString());
//          Bukkit.broadcastMessage("results = " + results);
//
//          results.forEach(result -> {
//            String owner = result.getString(1);
//            String seller = result.getString(2);
//            String item = result.getString(3);
//            int quantity = result.get(4);
//
//            Bukkit.broadcastMessage("owner " + owner);
//            Bukkit.broadcastMessage("seller " + seller);
//            Bukkit.broadcastMessage("item " + item);
//            Bukkit.broadcastMessage("quantity " + quantity);
//
//            Material material = Material.getMaterial(item);
//            if (material != null) {
//              shopItems.put(material, quantity);
//
//
//              // Use a synchronous task to update the shopItems map
//              Bukkit.getScheduler().runTask(PrivateMines.getPrivateMines(), () -> {
//                shopItems.forEach(((material1, qty) -> {
//                  GuiItem guiItem = ItemBuilder.from(material1)
//                      .name(Component.text("test"))
//                      .lore(Component.text(material1.name())
//                          .append(Component.text(":").appendSpace().append(Component.text(qty))))
//                      .asGuiItem();
//                  paginatedGui.addItem(guiItem);
//
//                  Bukkit.broadcastMessage("gui item " + guiItem);
//                }));
////                // Add items to the GUI once the shopItems map is updated
////                shopItems.forEach((mat, qty) -> {
////                  Bukkit.broadcastMessage("mat? " + mat);
////                  Bukkit.broadcastMessage("int? " + qty);
////                  GuiItem guiItem = ItemBuilder.from(mat)
////                      .name(Component.text(UUID.randomUUID().toString()))
////                      .lore(Component.text(mat.name()).append(Component.text(":")).appendSpace()
////                          .append(Component.text(qty)))
////                      .asGuiItem();
//////                  scrollingGui.addItem(guiItem);
////                  Bukkit.broadcastMessage("gui item " + guiItem);
////                  paginatedGui.addItem(guiItem);
////                });
//
//                // Open the GUI for the player
//              });
//            } else {
//              Bukkit.broadcastMessage("Invalid material: " + item);
//            }
//          });
//        });
//
//        paginatedGui.open(player);
//      }
//    }
//  }

//  public void playerShop(Player player) {
//    if (!mineStorage.hasMine(player)) {
//      audienceUtils.sendMessage(player, MessagesConfig.dontOwnMine);
//    } else {
//      Mine mine = mineStorage.get(player);
//      AtomicDouble atomicDouble = new AtomicDouble(1);
//      Map<Material, Integer> shopItems = new HashMap<>();
//
//      if (mine != null) {
//        SQLHelper sqlHelper = privateMines.getSqlHelper();
//        sqlHelper.execute("UPDATE shops SET price=? WHERE item=? AND owner=?;",
//            atomicDouble.getAndAdd(1.0), "COBBLESTONE", player.getUniqueId().toString());
//
////        SQLUtils.updatePrice(player.getUniqueId(), Material.COBBLESTONE, 1.0);
//
//        MineData mineData = mine.getMineData();
//        Shop shop = mineData.getShop();
//
//        player.sendMessage("owner " + shop.getOwner());
//        player.sendMessage("prices " + shop.getPrices());
//
//        ScrollingGui scrollingGui = Gui.scrolling()
//            .title(Component.text("Title"))
//            .rows(6)
//            .disableItemTake()
//            .create();
//
//        Task.asyncDelayed(() -> {
//          Results results = sqlHelper.queryResults("SELECT * FROM shops WHERE owner=?;",
//              player.getUniqueId().toString());
//          Bukkit.broadcastMessage("results = " + results);
//
//          results.forEach(result -> {
//            String owner = result.getString(1);
//            String seller = result.getString(2);
//            String item = result.getString(3);
//            int quantity = result.get(4);
//
//            Bukkit.broadcastMessage("owner " + owner);
//            Bukkit.broadcastMessage("seller " + seller);
//            Bukkit.broadcastMessage("item " + item);
//            Bukkit.broadcastMessage("quantity " + quantity);
//
//            Task.syncDelayed(() -> {
//              shopItems.put(Material.getMaterial(item), quantity);
//            });
//          });
//        });
//
//        shopItems.forEach((material, integer) -> {
//          Bukkit.broadcastMessage("mat? " + material);
//          Bukkit.broadcastMessage("int? " + integer);
//        });
//
//
//        for (Entry<Material, Integer> entry : shopItems.entrySet()) {
//          Bukkit.broadcastMessage("entry " + entry);
//          GuiItem guiItem = ItemBuilder.from(
//                  Objects.requireNonNullElse(entry.getKey(), Material.STONE))
//              .name(Component.text(UUID.randomUUID().toString()))
//              .lore(Component.text(Material.STONE.name()).append(Component.text(":")).appendSpace()
//                  .append(Component.text(1))).asGuiItem();
//          scrollingGui.addItem(guiItem);
//
//          Bukkit.broadcastMessage("gui item " + guiItem);
//          Bukkit.broadcastMessage("scrolling gui " + scrollingGui);
//        }
////        for (Material material : shop.getPrices().keySet()) {
////          GuiItem guiItem = ItemBuilder.from(Objects.requireNonNullElse(material, Material.STONE))
////              .name(Component.text(UUID.randomUUID().toString()))
////              .asGuiItem();
////          scrollingGui.addItem(guiItem);
////        }
//
//        GuiItem guiItem = ItemBuilder.from(Material.STONE)
//            .name(Component.text("Test"))
//            .asGuiItem();
//
////        scrollingGui.addItem(guiItem);
////        scrollingGui.addItem(guiItem);
//
//        scrollingGui.open(player);
//      }
//    }
//  }

  @Subcommand("debug")
  public void pricesDebug(Player player, double price, int quantity) {
    if (!mineStorage.hasMine(player)) {
      audienceUtils.sendMessage(player, MessagesConfig.dontOwnMine);
    } else {
      Mine mine = mineStorage.get(player);

      if (mine != null) {
        SQLHelper sqlHelper = privateMines.getSqlHelper();
        int quantityBefore = sqlHelper.querySingleResult(
            "SELECT quantity FROM shops WHERE item=? AND owner=?;", "COBBLESTONE",
            player.getUniqueId().toString());
        sqlHelper.execute("UPDATE shops SET price=? WHERE item=? AND owner=?;",
            price, "COBBLESTONE", player.getUniqueId().toString());
        sqlHelper.execute("UPDATE shops SET quantity=? WHERE item=? AND owner=?;",
            quantity, "COBBLESTONE", player.getUniqueId().toString());

        String[] messages = {
            "Quantity before: " + quantityBefore,
            "Set the prices of", "cobblestone to " + price,
            "Set the quantity of", " cobblestone to " + quantity};
        player.sendMessage(messages);
      }
    }
  }

  @Subcommand("add")
  public void pricesDebugAdd(Player player, int quantity, double price) {
    if (!mineStorage.hasMine(player)) {
      audienceUtils.sendMessage(player, MessagesConfig.dontOwnMine);
    } else {
      ShopUtils.addItem(player.getUniqueId(), Material.COBBLESTONE, quantity, price);
    }
  }

  @Subcommand("sethandprice")
  public void setHandPrice(Player player, double price) {
    if (!mineStorage.hasMine(player)) {
      audienceUtils.sendMessage(player, MessagesConfig.dontOwnMine);
    } else {
      ShopUtils.updatePrice(player.getUniqueId(),
          player.getInventory().getItemInMainHand().getType(), price);
    }
  }
}

