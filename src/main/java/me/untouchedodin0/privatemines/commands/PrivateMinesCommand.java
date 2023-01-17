package me.untouchedodin0.privatemines.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import java.nio.Buffer;
import me.untouchedodin0.kotlin.menu.Menu;
import me.untouchedodin0.kotlin.mine.data.MineData;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.kotlin.utils.AudienceUtils;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.config.MenuConfig;
import me.untouchedodin0.privatemines.config.MessagesConfig;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.mine.MineTypeManager;
import me.untouchedodin0.privatemines.utils.world.MineWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("privatemine|privatemines|pmine")
public class PrivateMinesCommand extends BaseCommand {

  PrivateMines privateMines = PrivateMines.getPrivateMines();
  MineStorage mineStorage = privateMines.getMineStorage();
  MineTypeManager mineTypeManager = privateMines.getMineTypeManager();
  AudienceUtils audienceUtils = new AudienceUtils();

  @Default
  public void defaultCommand(Player player) {
    Menu mainMenu = MenuConfig.getMenus().get("mainMenu");
    mainMenu.open(player);
//    commandHelp.showHelp();
  }

  @Subcommand("version")
  @CommandCompletion("@player @addon")
  @CommandPermission("privatemines.version")
  public void version(Player player, String addon) {
    if (addon.isEmpty()) {
      player.sendMessage(String.format("PrivateMines is running addons v%s", "0"));
    } else {
      player.sendMessage("ignoreme " + addon);
    }
  }

  @Subcommand("give")
  @CommandCompletion("@players")
  @CommandPermission("privatemines.give")
  public void give(CommandSender sender, Player target) {
    sender.sendMessage(ChatColor.GREEN + "Giving " + target.getName() + " a mine!");

    MineFactory mineFactory = new MineFactory();
    MineWorldManager mineWorldManager = privateMines.getMineWorldManager();
    Location location = mineWorldManager.getNextFreeLocation();
    mineWorldManager.setCurrentLocation(location);
    MineType defaultMineType = mineTypeManager.getDefaultMineType();

    if (target.getPlayer() != null) {
      if (mineStorage.hasMine(target.getUniqueId())) {
        if (sender instanceof Player player) {
          audienceUtils.sendMessage(player, MessagesConfig.playerAlreadyOwnsAMine);
        }
      } else {
        mineFactory.create(target, location, defaultMineType, true);
        if (sender instanceof Player player) {
          audienceUtils.sendMessage(player, target,
              MessagesConfig.gavePlayerMine.replace("{name}", target.getName()));
        }
      }
    }
  }

  @Subcommand("delete")
  @CommandCompletion("@players")
  @CommandPermission("privatemines.delete")
  public void delete(CommandSender sender, Player target) {
    if (!mineStorage.hasMine(target.getUniqueId())) {
      if (sender instanceof Player player) {
        audienceUtils.sendMessage(player, MessagesConfig.playerDoesntOwnMine);
      }
    } else {
      Mine mine = mineStorage.get(target.getUniqueId());
      if (mine != null) {
        mine.delete(true);
        if (sender instanceof Player player) {
          audienceUtils.sendMessage(player, target, MessagesConfig.deletedPlayersMine);
        }
      }
    }
  }

  @Subcommand("upgrade")
  @CommandCompletion("@players")
  @CommandPermission("privatemines.upgrade")
  public void upgrade(CommandSender sender, Player target, String mineType) {
    if (!mineStorage.hasMine(target.getUniqueId())) {
      if (sender instanceof Player player) {
        audienceUtils.sendMessage(player, MessagesConfig.playerDoesntOwnMine);
      }
    } else {
      Mine mine = mineStorage.get(target);
      if (mine != null) {
        mine.upgrade();
      }
    }
  }

  @Subcommand("reset")
  @CommandPermission("privatemines.reset")
  public void reset(Player player) {
    if (!mineStorage.hasMine(player)) {
      player.sendMessage(ChatColor.RED + "You don't own a mine!");
    } else {
      Mine mine = mineStorage.get(player);
      if (mine != null) {
        MineData mineData = mine.getMineData();
        MineType mineType = mineData.getMineType();
        boolean useOraxen = mineType.getUseOraxen();
        boolean useItemsAdder = mineType.getUseItemsAdder();

        if (!useOraxen && !useItemsAdder) {
          mine.reset();
        } else if (mineType.getOraxen() != null && useOraxen) {
          mine.resetOraxen();
        } else if (mineType.getItemsAdder() != null && useItemsAdder) {
          mine.resetItemsAdder();
        }
      }
    }
  }

  @Subcommand("teleport")
  @CommandPermission("privatemines.teleport")
  public void teleport(Player player) {
    if (!mineStorage.hasMine(player)) {
      player.sendMessage(ChatColor.RED + "You don't own a mine!");
    } else {
      Mine mine = mineStorage.get(player);
      if (mine != null) {
        mine.teleport(player);
      }
    }
  }

  @Subcommand("expand")
  @CommandPermission("privatemines.expand")
  public void expand(CommandSender commandSender, Player target, int amount) {
    if (!mineStorage.hasMine(target)) return;
    Mine mine = mineStorage.get(target);
    if (mine != null) {
      if (mine.canExpand(amount)) {
        for (int i = 0; i < amount; i++) {
          mine.expand();
        }
      }
    }
  }
}
