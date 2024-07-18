package me.untouchedodin0.privatemines.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import dev.triumphteam.gui.components.ScrollType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.ScrollingGui;
import me.untouchedodin0.kotlin.mine.data.MineData;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.kotlin.utils.AudienceUtils;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.config.MessagesConfig;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.playershops.Shop;
import me.untouchedodin0.privatemines.storage.sql.SQLUtils;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

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
      if (mine != null) {
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
}
