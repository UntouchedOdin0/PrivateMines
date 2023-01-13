/**
 * MIT License
 *
 * Copyright (c) 2021 - 2023 Kyle Hicks
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.untouchedodin0.privatemines.commands;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import me.untouchedodin0.kotlin.menu.Menu;
import me.untouchedodin0.kotlin.mine.data.MineData;
import me.untouchedodin0.kotlin.mine.pregen.PregenMine;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.kotlin.mine.storage.PregenStorage;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.kotlin.utils.AudienceUtils;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.WorldBorderUtils;
import me.untouchedodin0.privatemines.config.MenuConfig;
import me.untouchedodin0.privatemines.config.MessagesConfig;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.factory.PregenFactory;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.mine.MineTypeManager;
import me.untouchedodin0.privatemines.playershops.Shop;
import me.untouchedodin0.privatemines.playershops.ShopBuilder;
import me.untouchedodin0.privatemines.utils.SQLUtils;
import me.untouchedodin0.privatemines.utils.inventory.PublicMinesMenu;
import me.untouchedodin0.privatemines.utils.world.MineWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.WorldBorder;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.misc.ChatPrompt;
import redempt.redlib.misc.Task;
import redempt.redlib.sql.SQLHelper;

@SuppressWarnings("unused")
public class PrivateMinesCommandOld {

  PrivateMines privateMines = PrivateMines.getPrivateMines();
  MineStorage mineStorage = privateMines.getMineStorage();
  MineTypeManager mineTypeManager = privateMines.getMineTypeManager();
  AudienceUtils audienceUtils = new AudienceUtils();

  @CommandHook("main")
  public void main(CommandSender sender) {
    if (sender instanceof Player player) {
      Menu mainMenu = MenuConfig.getMenus().get("mainMenu");
      mainMenu.open(player);
    }
  }

  @CommandHook("give")
  public void give(CommandSender commandSender, OfflinePlayer target, MineType mineType) {
    MineFactory mineFactory = new MineFactory();
    MineWorldManager mineWorldManager = privateMines.getMineWorldManager();
    Location location = mineWorldManager.getNextFreeLocation();
    mineWorldManager.setCurrentLocation(location);
    MineType defaultMineType = mineTypeManager.getDefaultMineType();

    if (target.getPlayer() != null) {
      if (mineStorage.hasMine(target.getUniqueId())) {
        if (commandSender instanceof Player player) {
          audienceUtils.sendMessage(player, MessagesConfig.playerAlreadyOwnsAMine);
        }
      } else {
        mineFactory.create(target.getPlayer(), location,
            Objects.requireNonNullElse(mineType, defaultMineType), true);
        if (commandSender instanceof Player player) {
          audienceUtils.sendMessage(player, target, MessagesConfig.gavePlayerMine);
        }
      }
    }
  }

  @CommandHook("delete")
  public void delete(CommandSender commandSender, OfflinePlayer target) {
    if (!mineStorage.hasMine(target.getUniqueId())) {
      if (commandSender instanceof Player player) {
        audienceUtils.sendMessage(player, MessagesConfig.playerDoesntOwnMine);
      }
    } else {
      Mine mine = mineStorage.get(target.getUniqueId());
      if (mine != null) {
        mine.delete(true);
        if (commandSender instanceof Player player) {
          audienceUtils.sendMessage(player, target, MessagesConfig.deletedPlayersMine);
        }
      }
    }
  }

  @CommandHook("reset")
  public void reset(Player player, OfflinePlayer target) {
    if (target != null) {
      Player targetPlayer = target.getPlayer();
      if (!mineStorage.hasMine(Objects.requireNonNull(targetPlayer))) {
        player.sendMessage(ChatColor.RED + target.getName() + " Doesn't own a mine!");
      } else {
        Mine mine = mineStorage.get(Objects.requireNonNull(targetPlayer));
        if (mine != null) {
          mine.reset();
        }
        audienceUtils.sendMessage(player, target, MessagesConfig.resetTargetMine);
      }
    } else {
      Mine mine = mineStorage.get(player);
      if (mine != null) {
        mine.reset();
      }
      audienceUtils.sendMessage(player, MessagesConfig.mineReset);
    }
  }

  @CommandHook("upgrade")
  public void upgrade(CommandSender commandSender, Player player) {
    if (!mineStorage.hasMine(player)) {
      if (commandSender instanceof Player player1) {
        audienceUtils.sendMessage(player1, MessagesConfig.playerDoesntOwnMine);
      }
    } else {
      Mine mine = mineStorage.get(player);
      if (mine != null) {
        MineData mineData = mine.getMineData();
        mine.upgrade();
        audienceUtils.sendMessage(player, MessagesConfig.mineUpgraded);
      }
    }
  }

  @CommandHook("expand")
  public void expand(CommandSender commandSender, Player target, int amount) {
    if (!mineStorage.hasMine(target)) {
      if (commandSender instanceof Player player) {
        audienceUtils.sendMessage(player, MessagesConfig.playerDoesntOwnMine);
      }
    } else {
      Mine mine = mineStorage.get(target);
      if (mine != null) {
        if (mine.canExpand(amount)) {
          for (int i = 0; i < amount; i++) {
            mine.expand();
          }
        }
        MineData mineData = mine.getMineData();
        mine.saveMineData(target, mineData);
        if (commandSender instanceof Player player) {
          audienceUtils.sendMessage(player, target, MessagesConfig.playerMineExpanded, amount);
          audienceUtils.sendMessage(target, MessagesConfig.ownMineExpanded, amount);
        }
      }
    }
  }

  @CommandHook("teleport")
  public void teleport(Player player) {
    if (!mineStorage.hasMine(player.getUniqueId())) {
      audienceUtils.sendMessage(player, MessagesConfig.dontOwnMine);
    } else {
      Mine mine = mineStorage.get(player.getUniqueId());
      audienceUtils.sendMessage(player, MessagesConfig.teleportedToOwnMine);
      if (mine != null) {
        mine.teleport(player);
      }
    }
  }

  @CommandHook("visit")
  public void visit(Player player, OfflinePlayer target) {
    if (!mineStorage.hasMine(target.getUniqueId())) {
      audienceUtils.sendMessage(player, MessagesConfig.playerDoesntOwnMine);
    } else {
      Mine mine = mineStorage.get(target.getUniqueId());
      if (mine != null) {
        if (mine.getMineData().isOpen()) {
          mine.teleport(player);
          audienceUtils.sendMessage(player, target, MessagesConfig.visitingMine);
        } else {
          player.sendMessage(ChatColor.RED + "Mine closed!");
        }
      }
    }
  }

  @CommandHook("setblocks")
  public void setBlocks(CommandSender commandSender, Player target, Material[] materials) {
    Mine mine = mineStorage.get(target);
    MineData mineData;
    Map<Material, Double> map = new HashMap<>();

    if (mine != null) {
      for (Material material : materials) {
        if (material.isBlock()) {
          map.put(material, 1.0);
        } else {
          commandSender.sendMessage(
              ChatColor.RED + "Could not add " + material.name() + " as it wasn't a solid block!");
        }
      }

      mineData = mine.getMineData();
      mineData.setMaterials(map);
      mine.setMineData(mineData);
      mine.saveMineData(target, mineData);
      mineStorage.replaceMine(target.getUniqueId(), mine);
      mine.reset();
    }
  }

  @CommandHook("tax")
  public void tax(Player player, double tax) {
    Mine mine = mineStorage.get(player);
    MineData mineData;
    if (mine != null) {
      mineData = mine.getMineData();
      mineData.setTax(tax);
      mine.setMineData(mineData);
      mine.saveMineData(player, mineData);
      audienceUtils.sendMessage(player,
          MessagesConfig.setTax.replace("{tax}", String.valueOf(tax)));
    }
  }

  @CommandHook("ban")
  public void ban(Player player, Player target) {
    Mine mine = mineStorage.get(player);
    if (mine != null) {
      MineData mineData = mine.getMineData();
      UUID uuid = target.getUniqueId();

      if (mineData.getBannedPlayers().contains(uuid)) {
        audienceUtils.sendMessage(player, MessagesConfig.targetAlreadyBanned);
      } else {
        mineData.getBannedPlayers().add(uuid);
        mine.setMineData(mineData);
        mine.saveMineData(player, mineData);
        audienceUtils.sendMessage(player, target, MessagesConfig.successfullyBannedPlayer);
      }
    }
  }

  @CommandHook("unban")
  public void unban(Player player, Player target) {
    Mine mine = mineStorage.get(player);
    if (mine != null) {
      MineData mineData = mine.getMineData();
      UUID uuid = target.getUniqueId();

      if (!mineData.getBannedPlayers().contains(uuid)) {
        audienceUtils.sendMessage(player, target, MessagesConfig.targetIsNotBanned);
      } else {
        mineData.getBannedPlayers().remove(uuid);
        mine.setMineData(mineData);
        mine.saveMineData(player, mineData);
        audienceUtils.sendMessage(player, target, MessagesConfig.unbannedPlayer);
      }
    }
  }

  @CommandHook("open")
  public void open(Player player) {
    Mine mine = mineStorage.get(player);

    if (mine != null) {
      MineData mineData = mine.getMineData();
      mineData.setOpen(true);
      mine.saveMineData(player, mineData);
      player.sendMessage(ChatColor.GREEN + "Your mine's now open!");
    }
  }

  @CommandHook("close")
  public void close(Player player) {
    Mine mine = mineStorage.get(player);

    if (mine != null) {
      MineData mineData = mine.getMineData();
      mineData.setOpen(false);
      mine.saveMineData(player, mineData);
      player.sendMessage(ChatColor.RED + "Your mine's now closed!");
    }
  }

  @CommandHook("addfriend")
  public void addFriend(Player player, Player target) {
    Mine mine = mineStorage.get(player);
    if (mine != null) {
      MineData mineData = mine.getMineData();
      List<UUID> friends = mineData.getFriends();
      UUID targetUUID = target.getUniqueId();

      if (friends.contains(targetUUID)) {
        player.sendMessage(ChatColor.RED + "You have already added that user as a friend!");
      } else {
        friends.add(targetUUID);
        player.sendMessage(
            String.format(ChatColor.GREEN + "You have added %s as a friend to your mine!",
                target.getName()));
        mineData.setFriends(friends);
        mine.setMineData(mineData);
        mine.saveMineData(player, mineData);
      }
    }
  }

  @CommandHook("removefriend")
  public void removeFriend(Player player, Player target) {
    Mine mine = mineStorage.get(player);
    if (mine != null) {
      MineData mineData = mine.getMineData();
      List<UUID> friends = mineData.getFriends();
      UUID targetUUID = target.getUniqueId();

      if (!friends.contains(targetUUID)) {
        player.sendMessage(ChatColor.RED + "You have not added that user as a friend!");
      } else {
        friends.remove(targetUUID);
        player.sendMessage(
            String.format(ChatColor.GREEN + "You have remove %s as a friend from your mine!",
                target.getName()));
        mineData.setFriends(friends);
        mine.setMineData(mineData);
        mine.saveMineData(player, mineData);
      }
    }
  }

  @CommandHook("debug")
  public void debug(Player player) {
    PublicMinesMenu menu = new PublicMinesMenu();
    menu.open(player);
    player.sendMessage("debug.");
  }

  @CommandHook("pregen")
  public void pregen(Player player, int amount) {
    PregenFactory pregenFactory = new PregenFactory();
    pregenFactory.generate(player, amount);
  }

  @CommandHook("claim")
  public void claim(Player player) {
    AudienceUtils audienceUtils = new AudienceUtils();

    if (mineStorage.hasMine(player)) {
      player.sendMessage(ChatColor.RED + "You already own a mine!");
      return;
    }
    PregenStorage pregenStorage = privateMines.getPregenStorage();
    boolean isAllRedeemed = pregenStorage.isAllRedeemed();

    if (isAllRedeemed) {
      player.sendMessage(ChatColor.RED + "All the mines have been redeemed, please contact an");
      player.sendMessage(ChatColor.RED + "admin and ask them to redeem some more mines!");
    } else {
      PregenMine pregenMine = pregenStorage.getAndRemove();

      if (pregenMine != null) {
        Location location = pregenMine.getLocation();
        Location spawnLocation = pregenMine.getSpawnLocation();
        Location lowerRails = pregenMine.getLowerRails();
        Location upperRails = pregenMine.getUpperRails();
        Location fullMin = pregenMine.getFullMin();
        Location fullMax = pregenMine.getFullMax();
        File file = pregenMine.getFile();

        UUID uuid = player.getUniqueId();

        if (spawnLocation != null) {
          spawnLocation.getBlock().setType(Material.AIR);
        }

        MineType mineType = mineTypeManager.getDefaultMineType();

        Map<Material, Double> prices = new HashMap<>();
        Map<Material, Double> materials = mineType.getMaterials();
        if (materials != null) {
          prices.putAll(materials);
        }
        Shop shop = new ShopBuilder().setOwner(uuid).setPrices(prices).build();

        Mine mine = new Mine(privateMines);
        assert upperRails != null;
        assert lowerRails != null;
        assert fullMin != null;
        assert fullMax != null;
        assert location != null;
        assert spawnLocation != null;

        MineData mineData = new MineData(uuid, upperRails, lowerRails, fullMin, fullMax, location,
            spawnLocation, mineType, shop);

        mine.setMineData(mineData);
        mine.saveMineData(player, mineData);
        mineStorage.addMine(player.getUniqueId(), mine);
        mine.reset();
        mine.createWorldGuardRegions();

        try {
          if (file != null) {
            Files.delete(file.toPath());
          }
        } catch (IOException e) {
          throw new RuntimeException(e);
        }

        Task.syncDelayed(() -> pregenMine.teleport(player), 5L);
        audienceUtils.sendMessage(player, MessagesConfig.teleportedToOwnMine);
      }
    }
  }

  @CommandHook("convert")
  public void convert(Player player) {
    player.sendMessage("" + ChatColor.RED + ChatColor.BOLD + "NOT WORKING YET!");
    ChatPrompt.prompt(player, ChatColor.YELLOW
        + "Are you sure you want to convert the mines to SQL? Type Yes to carry on the process "
        + "or No to cancel the process.", str -> {
      if (str.equalsIgnoreCase("Yes")) {
        player.sendMessage(ChatColor.GREEN + "Starting conversion...");
        SQLHelper sqlHelper = privateMines.getSqlHelper();
        Connection connection = sqlHelper.getConnection();
        String query = "INSERT INTO privatemines values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // 13

        try {
          PreparedStatement preparedStatement = connection.prepareStatement(query);
          preparedStatement.setString(1, "1");
          preparedStatement.setString(2, "2");
          preparedStatement.setString(3, "3");
          preparedStatement.setString(4, "4");
          preparedStatement.setString(5, "5");
          preparedStatement.setString(6, "6");
          preparedStatement.setString(7, "7");
          preparedStatement.setString(8, "8");
          preparedStatement.setString(9, "9");
          preparedStatement.setString(10, "10");
          preparedStatement.setString(11, "11");
          preparedStatement.setString(12, "12");
          preparedStatement.setString(13, "13");

          int res = preparedStatement.executeUpdate();

//                    connection.commit();
//                    connection.close();
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }

        player.sendMessage("connection " + connection);

        try {
          connection.commit();
          connection.close();
        } catch (SQLException e) {
          throw new RuntimeException(e);
        }

//                sqlHelper.executeUpdate("UPDATE privatemines SET mineOwner=? mineType=? mineLocation=? corner1=? corner2=? fullRegionMin=? fullRegionMax=? spawn=? tax=? isOpen=? maxPlayers=? maxMineSize=? materials=?", "owner", "type", "minelocation", "corner1", "corner2", "fullmin", "fullmax", "spawn", 5.0, false, 1, 1, "materials");
//                sqlHelper.commit();
//
//                player.sendMessage("" + sqlHelper);
//                sqlHelper.execute("INSERT INTO privatemines (mineOwner, mineType, mineLocation, corner1, corner2, fullRegionMin, fullRegionMax, spawn, tax, isOpen, maxPlayers, maxMineSize, materials) VALUES(mineOwner, mineType, mineLocation, corner1, corner2, fullRegionMin, fullRegionMax, spawn, tax, isOpen, maxPlayers, maxMineSize, materials);");

//                privateMines.convertToSQL(player);
      } else {
        player.sendMessage(ChatColor.RED + "Cancelled the process.");
      }
    });
  }

  @CommandHook("reload")
  public void reload(Player player) {
    privateMines.getConfigManager().reload();
    privateMines.getConfigManager().load();
  }

  @CommandHook("setborder")
  public void setBorder(Player player, Player target, int size) {
    WorldBorderUtils worldBorderUtils = privateMines.getWorldBorderUtils();
    Server server = Bukkit.getServer();
    Location location = player.getLocation();

    player.sendMessage("worldBorderUtils: " + worldBorderUtils);
    worldBorderUtils.sendWorldBorder(server, player, location, size);
  }

  @CommandHook("clearborder")
  public void clearborder(Player player, Player target) {
    WorldBorderUtils worldBorderUtils = privateMines.getWorldBorderUtils();
    WorldBorder worldBorder = worldBorderUtils.getWorldBorder(player);
    worldBorderUtils.clearBorder(player);
  }

  @CommandHook("testsql")
  public void testSQL(Player player) {
    Mine mine = mineStorage.get(player);
    if (mine != null) {
      SQLUtils.insert(mine);
    }

//        SQLHelper sqlHelper = privateMines.getSqlHelper();
//        Connection connection = sqlHelper.getConnection();
//        String updateString = "INSERT INTO privatemines (mineOwner, mineType, mineLocation, corner1, corner2, fullRegionMin, fullRegionMax, spawn, tax, isOpen, maxPlayers, maxMineSize, materials)" +
//                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//                //"update privatemines " + "set mineOwner = ? where COF_NAME = ?";
//        try {
//            PreparedStatement updateSales = connection.prepareStatement(updateString);
//            updateSales.setString(1, "mine owner");
//            updateSales.setString(2, "mine type");
//            updateSales.setString(3, "mine location");
//            updateSales.setString(4, "corner 1");
//            updateSales.setString(5, "corner 2");
//            updateSales.setString(6, "full region min");
//            updateSales.setString(7, "full region max");
//            updateSales.setString(8, "spawn");
//            updateSales.setDouble(9, 5.0);
//            updateSales.setBoolean(10, false);
//            updateSales.setInt(11, 1);
//            updateSales.setInt(12, 2);
//            updateSales.setString(13, "materials");
//
//            player.sendMessage("" + sqlHelper);
//            player.sendMessage("update sales " + updateSales);
//
//            updateSales.executeUpdate();
////            updateSales.close();
//
//            connection.commit();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
  }
}