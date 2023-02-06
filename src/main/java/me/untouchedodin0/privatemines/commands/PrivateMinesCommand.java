package me.untouchedodin0.privatemines.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Split;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import me.untouchedodin0.privatemines.storage.sql.SQLUtils;
import me.untouchedodin0.privatemines.utils.QueueUtils;
import me.untouchedodin0.privatemines.utils.Utils;
import me.untouchedodin0.privatemines.utils.world.MineWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.redlib.misc.Task;
import redempt.redlib.region.CuboidRegion;

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

    audienceUtils.sendMessage(player,
        String.format("<green>Private Mines is running v%s, latest commit <gray>(%s)", localVersion,
            gitVersion));
  }

  @Subcommand("give")
  @CommandCompletion("@players")
  @CommandPermission("privatemines.give")
  public void give(CommandSender sender, OfflinePlayer target) {
    long before = System.currentTimeMillis();

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
        long after = System.currentTimeMillis();
        long timeTaken = after - before;
        Bukkit.broadcastMessage("time taken " + timeTaken);

        if (sender instanceof Player player) {
          audienceUtils.sendMessage(player, target, MessagesConfig.gavePlayerMine.replace("{name}",
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
        SQLUtils.delete(mine);
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
      Bukkit.broadcastMessage("mine " + mine);
      if (mine != null) {
        SQLUtils.delete(mine);
        mine.upgrade();

        List<Player> players = new ArrayList<>();

        MineData mineData = mine.getMineData();
        Location minFull = mineData.getMinimumFullRegion();
        Location maxFull = mineData.getMaximumFullRegion();
        CuboidRegion cuboidRegion = new CuboidRegion(minFull, maxFull);

        for (Player player : Bukkit.getOnlinePlayers()) {
          if (cuboidRegion.contains(player.getLocation())) {
            if (player.getUniqueId().equals(mineData.getMineOwner())) {
              return;
            }
            players.add(player);
          }
        }

//        SQLUtils.replace(mine);
//        mine.upgrade();

        for (Player toTeleport : players) {
          Bukkit.getServer().dispatchCommand(toTeleport, "spawn");
        }

        Task.syncDelayed(() -> {
          for (Player player : players) {
            mine.teleport(player);
          }
          players.clear();
        }, 20L);
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
        mine.handleReset();

        //        player.sendMessage("Next location: " + privateMines.getMineWorldManager().getNextFreeLocation());

//        CompletableFuture<Void> future = CompletableFuture.supplyAsync(() -> {
//          Bukkit.broadcastMessage("Hi");
//          return null;
//        }).thenRun(() -> {
//          Bukkit.broadcastMessage("finished?");
//        });
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
  @CommandCompletion("@players")
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
        commandSender.sendMessage(
            ChatColor.GREEN + "Successfully expanded " + target.getName() + "'s mine!");
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
  @CommandPermission("privatemines.ban")
  public void ban(Player player, Player target) {
    Mine mine = mineStorage.get(player);
    if (mine != null) {
      mine.ban(target);
      mineStorage.replaceMineNoLog(player, mine);
    }
  }

  @Subcommand("unban")
  @CommandPermission("privatemines.unban")
  public void unban(Player player, Player target) {
    Mine mine = mineStorage.get(player);
    if (mine != null) {
      mine.unban(target);
      mineStorage.replaceMineNoLog(player, mine);
    }
  }

  @Subcommand("tax")
  @CommandPermission("privatemines.tax")
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
  @CommandPermission("privatemines.claim")
  public void claim(Player player) {
    QueueUtils queueUtils = privateMines.getQueueUtils();
    if (queueUtils.isInQueue(player.getUniqueId())) {
      player.sendMessage(ChatColor.RED + "You're already in the queue!");
      return;
    }
    queueUtils.claim(player);
  }

  @Subcommand("setblocks")
  @CommandCompletion("@players")
  @CommandPermission("privatemines.setblocks")
  @Syntax("<target> <materials> (DIRT, STONE)")
  public void setBlocks(CommandSender sender, OfflinePlayer target,
      @Split(",") String[] materials) {
    Map<Material, Double> map = new HashMap<>();

    for (String s : materials) {
      Material material = Material.valueOf(s.toUpperCase());
      map.put(material, 1.0);
    }

    if (target != null) {
      Mine mine = mineStorage.get(Objects.requireNonNull(target.getPlayer()));
      if (mine != null) {
        MineData mineData = mine.getMineData();
        mineData.setMaterials(map);
        mine.setMineData(mineData);
        mineStorage.replaceMineNoLog(target.getPlayer(), mine);
        mine.handleReset();
        Task.asyncDelayed(() -> {
          SQLUtils.updateMaterials(mine);
        });
      }
    }
  }
}
