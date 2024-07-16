package me.untouchedodin0.privatemines.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import me.untouchedodin0.privatemines.storage.sql.SQLUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@CommandAlias("playershop|playershops|pshop")
public class PlayerShopCommand extends BaseCommand {

  @Default
  @CommandPermission("privatemines.playershop")
  public void playerShop(Player player) {

    SQLUtils.updatePrice(player.getUniqueId(), Material.COBBLESTONE, 1.0);
  }
}
