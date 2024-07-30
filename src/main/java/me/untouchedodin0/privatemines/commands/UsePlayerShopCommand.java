package me.untouchedodin0.privatemines.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

@CommandAlias("useplayershop|usepshop")
public class UsePlayerShopCommand extends BaseCommand {

  @Default
  @CommandPermission("privatemines.playershop.use")
  public void usePlayerShop(Player player, OfflinePlayer target) {

  }
}