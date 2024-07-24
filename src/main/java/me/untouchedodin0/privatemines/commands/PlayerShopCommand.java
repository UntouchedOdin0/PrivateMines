package me.untouchedodin0.privatemines.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import java.util.HashMap;
import java.util.Map;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.kotlin.utils.AudienceUtils;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.config.MessagesConfig;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.playershops.PlayerShopMenuUtils;
import me.untouchedodin0.privatemines.playershops.ShopUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@CommandAlias("playershop|playershops|pshop")
public class PlayerShopCommand extends BaseCommand {

  PrivateMines privateMines = PrivateMines.getPrivateMines();
  MineStorage mineStorage = privateMines.getMineStorage();
  AudienceUtils audienceUtils = new AudienceUtils();

  @Default
  @CommandPermission("privatemines.playershop")
  public synchronized void playerShop(Player player) {
    Map<Material, Integer> shopItems = new HashMap<>();
    PlayerShopMenuUtils playerShopMenuUtils = new PlayerShopMenuUtils();

    if (!mineStorage.hasMine(player)) {
      audienceUtils.sendMessage(player, MessagesConfig.dontOwnMine);
      return;
    }

    Mine mine = mineStorage.get(player);
    if (mine == null) {
      audienceUtils.sendMessage(player, MessagesConfig.dontOwnMine);
      return;
    }

    playerShopMenuUtils.generateMenu(player);
  }

  @Subcommand("debug")
  public void pricesDebug(Player player, double price, int quantity) {
    if (!mineStorage.hasMine(player)) {
      audienceUtils.sendMessage(player, MessagesConfig.dontOwnMine);
    } else {
      Mine mine = mineStorage.get(player);
    }
  }

  @Subcommand("add")
  @Syntax("<quantity> <price>")
  public void pricesDebugAdd(Player player, long quantity, double price) {
    if (!mineStorage.hasMine(player)) {
      audienceUtils.sendMessage(player, MessagesConfig.dontOwnMine);
    } else {
      Material material = player.getInventory().getItemInMainHand().getType();
      Bukkit.broadcastMessage("" + material);
      ShopUtils.addItem(player.getUniqueId(), material, quantity, price);
    }
  }

  @Subcommand("remove")
  public void pricesDebugRemove(Player player, long quantity) {
    if (!mineStorage.hasMine(player)) {
      audienceUtils.sendMessage(player, MessagesConfig.dontOwnMine);
    } else {
      Material material = player.getInventory().getItemInMainHand().getType();
      ShopUtils.removeItem(player.getUniqueId(), material, quantity);
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

