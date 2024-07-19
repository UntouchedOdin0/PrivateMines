package me.untouchedodin0.privatemines.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.google.common.util.concurrent.AtomicDouble;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.ScrollingGui;
import me.untouchedodin0.kotlin.mine.data.MineData;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.kotlin.utils.AudienceUtils;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.config.MessagesConfig;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.playershops.Shop;
import me.untouchedodin0.privatemines.playershops.ShopUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import redempt.redlib.sql.SQLHelper;

@CommandAlias("playershop|playershops|pshop")
public class PlayerShopCommand extends BaseCommand {

  PrivateMines privateMines = PrivateMines.getPrivateMines();
  MineStorage mineStorage = privateMines.getMineStorage();
  AudienceUtils audienceUtils = new AudienceUtils();

  @Default
  @CommandPermission("privatemines.playershop")
  public void playerShop(Player player) {
    if (!mineStorage.hasMine(player)) {
      audienceUtils.sendMessage(player, MessagesConfig.dontOwnMine);
    } else {
      Mine mine = mineStorage.get(player);
      AtomicDouble atomicDouble = new AtomicDouble(1);

      if (mine != null) {
        SQLHelper sqlHelper = privateMines.getSqlHelper();
        sqlHelper.execute("UPDATE shops SET price=? WHERE item=? AND owner=?;",
            atomicDouble.getAndAdd(1.0), "COBBLESTONE", player.getUniqueId().toString());

//        SQLUtils.updatePrice(player.getUniqueId(), Material.COBBLESTONE, 1.0);

        MineData mineData = mine.getMineData();
        Shop shop = mineData.getShop();
        player.sendMessage("owner " + shop.getOwner());
        player.sendMessage("prices " + shop.getPrices());

        ScrollingGui scrollingGui = Gui.scrolling()
            .title(Component.text("Title"))
            .rows(6)
            .pageSize(45)
            .create();

        scrollingGui.open(player);
      }
    }
  }

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

