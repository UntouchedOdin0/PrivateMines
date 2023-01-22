package me.untouchedodin0.privatemines.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.comphenix.protocol.PacketType.Play;
import java.util.Objects;
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
import me.untouchedodin0.privatemines.utils.QueueUtils;
import me.untouchedodin0.privatemines.utils.Utils;
import me.untouchedodin0.privatemines.utils.world.MineWorldManager;
import net.royawesome.jlibnoise.module.combiner.Min;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
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
  }

  @Subcommand("version")
  @CommandPermission("privatemines.version")
  public void version(Player player) {
    String localVersion = privateMines.getDescription().getVersion();
    String gitVersion = Utils.getGit();

    audienceUtils.sendMessage(player, String.format("<green>Private Mines is running v%s, latest commit <gray>(%s)", localVersion, gitVersion));
  }

  @Subcommand("give")
  @CommandCompletion("@players")
  @CommandPermission("privatemines.give")
  public void give(CommandSender sender, OfflinePlayer target) {

    MineFactory mineFactory = new MineFactory();
    MineWorldManager mineWorldManager = privateMines.getMineWorldManager();
    Location location = mineWorldManager.getNextFreeLocation();
    mineWorldManager.setCurrentLocation(location);
    MineType defaultMineType = mineTypeManager.getDefaultMineType();

    if (target.getPlayer() != null) {
      if (mineStorage.hasMine(target.getUniqueId())) {
        if (sender instanceof Player player) {
          audienceUtils.sendMessage(player, MessagesConfig.playerAlreadyOwnsAMine);
        } else {
          sender.sendMessage(ChatColor.RED + "Player has a mine!");
        }
      } else {
        sender.sendMessage(ChatColor.GREEN + "Giving " + target.getName() + " a mine!");
        mineFactory.create(target.getPlayer(), location, defaultMineType, true);
        if (sender instanceof Player player) {
          audienceUtils.sendMessage(player, target,
              MessagesConfig.gavePlayerMine.replace("{name}",
                  Objects.requireNonNull(target.getName())));
        }
      }
    }
  }

  @Subcommand("delete")
  @CommandCompletion("@players")
  @CommandPermission("privatemines.delete")
  public void delete(CommandSender sender, OfflinePlayer target) {
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
  public void upgrade(CommandSender sender, OfflinePlayer target) {
    if (!mineStorage.hasMine(target.getUniqueId())) {
      if (sender instanceof Player player) {
        audienceUtils.sendMessage(player, MessagesConfig.playerDoesntOwnMine);
      }
    } else {
      Mine mine = mineStorage.get(target.getUniqueId());
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
  public void expand(CommandSender commandSender, OfflinePlayer target, int amount) {
    if (!mineStorage.hasMine(Objects.requireNonNull(target.getPlayer()))) {
      return;
    }
    Mine mine = mineStorage.get(target.getPlayer());
    if (mine != null) {
      if (mine.canExpand(amount)) {
        for (int i = 0; i < amount; i++) {
          mine.expand();
        }
      }
    }
  }

  @Subcommand("open")
  @CommandPermission("privatemines.open")
  public void open(Player player) {
    Mine mine = mineStorage.get(player);
    MineData mineData;
    if (mine != null) {
      mineData = mine.getMineData();
      mineData.setOpen(true);
      mine.setMineData(mineData);
      mineStorage.replaceMineNoLog(player, mine);
    }
  }

  @Subcommand("close")
  @CommandPermission("privatemines.close")
  public void close(Player player) {
    Mine mine = mineStorage.get(player);
    MineData mineData;
    if (mine != null) {
      mineData = mine.getMineData();
      mineData.setOpen(false);
      mine.setMineData(mineData);
      mineStorage.replaceMineNoLog(player, mine);
    }
  }

  @Subcommand("ban")
  public void ban(Player player, Player target) {
    Mine mine = mineStorage.get(player);
    if (mine != null) {
      mine.ban(target);
      mineStorage.replaceMineNoLog(player, mine);
    }
  }

  @Subcommand("unban")
  public void unban(Player player, Player target) {
    Mine mine = mineStorage.get(player);
    if (mine != null) {
      mine.unban(target);
      mineStorage.replaceMineNoLog(player, mine);
    }
  }

  @Subcommand("tax")
  public void tax(Player player, double tax) {
    Mine mine = mineStorage.get(player);
    if (mine != null) {
      MineData mineData = mine.getMineData();
      mineData.setTax(tax);
      mine.setMineData(mineData);
      mineStorage.replaceMineNoLog(player, mine);
    }
  }

  @Subcommand("claim")
  public void claim(Player player) {
    QueueUtils queueUtils = privateMines.getQueueUtils();
    if (queueUtils.isInQueue(player.getUniqueId())) {
      player.sendMessage(ChatColor.RED + "You're already in the queue!");
      return;
    }
    queueUtils.claim(player);
  }
}
