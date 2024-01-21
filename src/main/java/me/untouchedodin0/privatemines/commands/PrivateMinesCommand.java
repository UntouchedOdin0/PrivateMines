/**
 * MIT License
 * <p>
 * Copyright (c) 2021 - 2023 Kyle Hicks
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package me.untouchedodin0.privatemines.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.comphenix.protocol.PacketType.Play.Server;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.comphenix.protocol.wrappers.WrappedBlockData;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import me.untouchedodin0.kotlin.mine.data.MineData;
import me.untouchedodin0.kotlin.mine.pregen.PregenMine;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.kotlin.mine.storage.PregenStorage;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.kotlin.utils.AudienceUtils;
import me.untouchedodin0.kotlin.utils.FlagUtils;
import me.untouchedodin0.kotlin.utils.Range;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.config.Config;
import me.untouchedodin0.privatemines.config.MessagesConfig;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.factory.PregenFactory;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.mine.MineTypeManager;
import me.untouchedodin0.privatemines.storage.sql.SQLUtils;
import me.untouchedodin0.privatemines.utils.CooldownManager;
import me.untouchedodin0.privatemines.utils.Utils;
import me.untouchedodin0.privatemines.utils.world.MineWorldManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import redempt.redlib.misc.LocationUtils;
import redempt.redlib.misc.Task;
import redempt.redlib.region.CuboidRegion;
import redempt.redlib.sql.SQLHelper;

@CommandAlias("privatemine|privatemines|pmine")
public class PrivateMinesCommand extends BaseCommand {

  PrivateMines privateMines = PrivateMines.getPrivateMines();
  MineStorage mineStorage = privateMines.getMineStorage();
  MineTypeManager mineTypeManager = privateMines.getMineTypeManager();
  AudienceUtils audienceUtils = new AudienceUtils();
  private final CooldownManager cooldownManager = new CooldownManager();

  @Default
  public void defaultCommand(CommandHelp commandHelp) {
    commandHelp.showHelp();
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
  @Syntax("<target>")
  public void give(CommandSender sender, OfflinePlayer target) {
    MineFactory mineFactory = new MineFactory();
    MineWorldManager mineWorldManager = privateMines.getMineWorldManager();
    Location location = mineWorldManager.getNextFreeLocation();

    MineType defaultMineType = mineTypeManager.getDefaultMineType();

    if (target.getPlayer() != null) {
      if (mineStorage.hasMine(target.getUniqueId())) {
        if (sender instanceof Player player) {
          audienceUtils.sendMessage(player, MessagesConfig.playerAlreadyOwnsAMine);
        } else {
          audienceUtils.sendMessage(sender, MessagesConfig.playerAlreadyOwnsAMine);
        }
      } else {
        mineFactory.create(target.getPlayer(), location, defaultMineType);

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
  @Syntax("<target>")
  public void delete(CommandSender sender, OfflinePlayer target) {
    if (!mineStorage.hasMine(target.getUniqueId())) {
      if (sender instanceof Player player) {
        audienceUtils.sendMessage(player, MessagesConfig.playerDoesntOwnMine);
      }
    } else {
      Mine mine = mineStorage.get(target.getUniqueId());
      if (mine != null) {
        mine.stopTasks();
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
  public void upgrade(CommandSender sender, @Default("1") int times) {
    if (!(sender instanceof Player player)) {
      sender.sendMessage(ChatColor.RED + "Only players can use this command!");
    } else {
      if (!mineStorage.hasMine(player)) {
        audienceUtils.sendMessage(player, MessagesConfig.dontOwnMine);
      } else {
        Mine mine = mineStorage.get(player);
        if (mine != null) {

          for (int i = 0; i < times; i++) {
            MineData mineData = mine.getMineData();
            MineType currentType = mineData.getMineType();
            MineType nextType = mineTypeManager.getNextMineType(currentType);
            double cost = nextType.getUpgradeCost();
            double bal = PrivateMines.getEconomy().getBalance(player);

            if (currentType == nextType) {
              audienceUtils.sendMessage(player, MessagesConfig.mineMaxUpgrade);
              return;
            }

            if (bal >= cost) {
              // player has enough money, upgrade the mine
              PrivateMines.getEconomy().withdrawPlayer(player, cost);
              mine.upgrade();
              mine.handleReset();
              audienceUtils.sendMessage(player, MessagesConfig.mineUpgraded);
            } else {
              player.sendMessage(ChatColor.RED + String.format(
                  "You need %.2f to upgrade the mine. You currently have %.2f.", cost, bal));
            }
          }
        }
      }
    }
  }

  @Subcommand("forceupgrade")
  @CommandCompletion("@players")
  @CommandPermission("privatemines.forceupgrade")
  @Syntax("<target>")
  public void forceUpgrade(CommandSender sender, OfflinePlayer target) {
    if (!mineStorage.hasMine(target.getUniqueId())) {
      if (sender instanceof Player player) {
        audienceUtils.sendMessage(player, MessagesConfig.playerDoesntOwnMine);
      }
    } else {
      SQLHelper sqlHelper = privateMines.getSqlHelper();

      Mine mine = mineStorage.get(target.getUniqueId());
      if (mine != null) {
        mine.upgrade();
        mine.reset();
        audienceUtils.sendMessage(Objects.requireNonNull(target.getPlayer()),
            MessagesConfig.mineUpgraded);

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

        for (Player toTeleport : players) {
          Bukkit.getServer().dispatchCommand(toTeleport, "spawn");
        }

        Task.syncDelayed(() -> {
          for (Player player : players) {
            mine.teleport(player);
          }
          players.clear();
        }, 20L);

        Task.asyncDelayed(() -> {
          sqlHelper.executeUpdate(
              "INSERT INTO privatemines (owner, mineType, mineLocation, corner1, corner2, fullRegionMin, fullRegionMax, spawn, tax, isOpen, maxPlayers, maxMineSize, materials) "
                  + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
              String.valueOf(mineData.getMineOwner()), mineData.getMineType(),
              LocationUtils.toString(mineData.getMineLocation()),
              LocationUtils.toString(mineData.getMinimumMining()),
              LocationUtils.toString(mineData.getMaximumMining()),
              LocationUtils.toString(mineData.getMinimumFullRegion()),
              LocationUtils.toString(mineData.getMaximumFullRegion()),
              LocationUtils.toString(mineData.getSpawnLocation()), mineData.getTax(),
              mineData.isOpen(), mineData.getMaxPlayers(), mineData.getMaxMineSize(),
              mineData.getMaterials());
        });
      }
    }
  }

  @Subcommand("forcereset")
  @CommandCompletion("@players")
  @CommandPermission("privatemines.forcereset")
  @Syntax("<target>")
  public void forceReset(CommandSender sender, OfflinePlayer target) {
    if (!mineStorage.hasMine(target.getUniqueId())) {
      if (sender instanceof Player player) {
        audienceUtils.sendMessage(player, MessagesConfig.playerDoesntOwnMine);
      }
    } else {
      Mine mine = mineStorage.get(target.getUniqueId());
      if (mine != null) {
        mine.handleReset();
      }
    }
  }

  @Subcommand("reset")
  @CommandPermission("privatemines.reset")
  public void reset(Player player) {
    if (!mineStorage.hasMine(player)) {
      audienceUtils.sendMessage(player, MessagesConfig.dontOwnMine);
    } else {
      int timeLeft = cooldownManager.getCooldown(player.getUniqueId());
      Mine mine = mineStorage.get(player);

      if (!Config.enableResetCooldown) {
        if (mine != null) {
          mine.handleReset();
        }
      } else {
        if (timeLeft == 0) {
          if (mine != null) {
            mine.handleReset();
          }
          cooldownManager.setCooldown(player.getUniqueId(), Config.resetCooldown);
          new BukkitRunnable() {
            @Override
            public void run() {
              int timeLeft = cooldownManager.getCooldown(player.getUniqueId());
              cooldownManager.setCooldown(player.getUniqueId(), --timeLeft);
              if (timeLeft == 0) {
                this.cancel();
              }
            }
          }.runTaskTimerAsynchronously(privateMines, 20, 20);
        } else {
          //Hasn't expired yet, shows how many seconds left until it does
          player.sendMessage(
              String.format(ChatColor.RED + "Please wait %d seconds to reset your mine again!",
                  timeLeft));
        }
      }
    }
  }

  @Subcommand("teleport")
  @CommandPermission("privatemines.teleport")
  public void teleport(Player player) {
    if (!mineStorage.hasMine(player)) {
      audienceUtils.sendMessage(player, MessagesConfig.dontOwnMine);
    } else {
      Mine mine = mineStorage.get(player);
      if (mine != null) {
        mine.teleport(player);
      }
    }
  }

  @Subcommand("go")
  @CommandCompletion("@players")
  @CommandPermission("privatemines.go")
  @Syntax("<target>")
  @CommandAlias("visit")
  public void go(Player player, OfflinePlayer target) {
    if (target.getPlayer() != null) {
      Player targetPlayer = target.getPlayer();
      Mine mine = mineStorage.get(targetPlayer);
      if (mine != null) {
        MineData mineData = mine.getMineData();
        if (mineData != null) {
          List<UUID> banned = mineData.getBannedPlayers();

          if (banned.contains(player.getUniqueId())) {
            audienceUtils.sendMessage(player, MessagesConfig.bannedFromMine);
          } else {
            if (mineData.isOpen()) {
              mine.teleport(player);
              mine.startTasks();
            } else {
              audienceUtils.sendMessage(player, MessagesConfig.targetMineClosed);
            }
          }
        }
      }
    }
  }

  @Subcommand("expand")
  @CommandCompletion("@players")
  @CommandPermission("privatemines.expand")
  @Syntax("<target> <amount>")
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
      SQLUtils.update(mine);
      audienceUtils.sendMessage(player, MessagesConfig.mineOpened);
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
      SQLUtils.update(mine);
      audienceUtils.sendMessage(player, MessagesConfig.mineClosed);
    }
  }

  @Subcommand("ban")
  @CommandPermission("privatemines.ban")
  @Syntax("<target>")
  public void ban(Player player, Player target) {
    Mine mine = mineStorage.get(player);
    if (mine != null) {
      mine.ban(target);
      mineStorage.replaceMineNoLog(player, mine);
      SQLUtils.update(mine);
      audienceUtils.sendMessage(player, target, MessagesConfig.successfullyBannedPlayer);
    }
  }

  @Subcommand("unban")
  @CommandPermission("privatemines.unban")
  @Syntax("<target>")
  public void unban(Player player, Player target) {
    Mine mine = mineStorage.get(player);
    if (mine != null) {
      mine.unban(target);
      mineStorage.replaceMineNoLog(player, mine);
      SQLUtils.update(mine);
      audienceUtils.sendMessage(player, target, MessagesConfig.unbannedPlayer);
    }
  }

  @Subcommand("tax")
  @CommandPermission("privatemines.tax")
  @Syntax("<amount>")
  public void tax(Player player, double tax) {
    Range range = new Range(0, 100);
    BukkitAudiences audiences = privateMines.getAdventure();
    Audience audience = audiences.player(player);

    if (!range.contains(tax)) {
      audienceUtils.sendMessage(player, MessagesConfig.taxLimit);
    } else {
      Mine mine = mineStorage.get(player);
      if (mine != null) {
        MineData mineData = mine.getMineData();
        mineData.setTax(tax);
        mine.setMineData(mineData);
        mineStorage.replaceMineNoLog(player, mine);
        SQLUtils.update(mine);
        Component message = Component.text()
            .append(Component.text("Successfully updated tax to ", NamedTextColor.GREEN))
            .append(Component.text(String.format("%.2f%%", tax), NamedTextColor.GOLD)).build();
        audienceUtils.sendMessage(player, MessagesConfig.setTax, tax);
      }
    }
  }

  @Subcommand("setblocks")
  @CommandCompletion("@players")
  @CommandPermission("privatemines.setblocks")
  @Syntax("<target> <materials> STONE, DIRT")
  public void setBlocks(CommandSender sender, OfflinePlayer target, String materials) {
    Map<Material, Double> map = new HashMap<>();

    // Split the materials string into an array
    String[] materialArray = materials.split(",");

    for (String string : materialArray) {
      // Valid format, proceed with getting the material and percent values.
      String[] parts = string.split(";");
      if (parts.length == 1) {
        Material material = Material.getMaterial(parts[0].toUpperCase());
        map.put(material, 1.0);
      } else if (parts.length == 2) {
        Material material = Material.getMaterial(parts[0].toUpperCase());
        double percentage = Double.parseDouble(parts[1]);
        map.put(material, percentage);
      }
    }

    if (target != null) {
      Mine mine = mineStorage.get(Objects.requireNonNull(target.getPlayer()));
      if (mine != null) {
        MineData mineData = mine.getMineData();
        mineData.setMaterials(map);
        mine.setMineData(mineData);
        mineStorage.replaceMineNoLog(target.getPlayer(), mine);
        mine.handleReset();
        Task.asyncDelayed(() -> SQLUtils.update(mine));
      }
    }
  }

  @Subcommand("pregen")
  @CommandPermission("privatemines.pregen")
  @Syntax("<amount>")
  public void pregen(Player player, int amount) {
    PregenFactory.pregen(player, amount);
  }

  @Subcommand("claim")
  @CommandPermission("privatemines.claim")
  public void claim(Player player) {
    if (mineStorage.hasMine(player)) {
      audienceUtils.sendMessage(player, MessagesConfig.playerAlreadyOwnsAMine);
    } else {
      String mineRegionName = String.format("mine-%s", player.getUniqueId());
      String fullRegionName = String.format("full-mine-%s", player.getUniqueId());

      PregenStorage pregenStorage = privateMines.getPregenStorage();
      if (pregenStorage.isAllRedeemed()) {
        audienceUtils.sendMessage(player, MessagesConfig.allMinesClaimed);
      } else {
        PregenMine pregenMine = pregenStorage.getAndRemove();
        MineType mineType = mineTypeManager.getDefaultMineType();
        Location location = pregenMine.getLocation();
        Location spawn = pregenMine.getSpawnLocation();
        Location corner1 = pregenMine.getLowerRails();
        Location corner2 = pregenMine.getUpperRails();
        Location minimum = pregenMine.getFullMin();
        Location maximum = pregenMine.getFullMax();
        BlockVector3 miningRegionMin = BukkitAdapter.asBlockVector(Objects.requireNonNull(corner1));
        BlockVector3 miningRegionMax = BukkitAdapter.asBlockVector(Objects.requireNonNull(corner2));
        BlockVector3 fullRegionMin = BukkitAdapter.asBlockVector(Objects.requireNonNull(minimum));
        BlockVector3 fullRegionMax = BukkitAdapter.asBlockVector(Objects.requireNonNull(maximum));
        FlagUtils flagUtils = new FlagUtils();

        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regionManager = container.get(
            BukkitAdapter.adapt(Objects.requireNonNull(spawn).getWorld()));

        ProtectedCuboidRegion miningRegion = new ProtectedCuboidRegion(mineRegionName,
            miningRegionMin, miningRegionMax);
        ProtectedCuboidRegion fullRegion = new ProtectedCuboidRegion(fullRegionName, fullRegionMin,
            fullRegionMax);

        if (regionManager != null) {
          regionManager.addRegion(miningRegion);
          regionManager.addRegion(fullRegion);
        }

        Mine mine = new Mine(privateMines);
        MineData mineData = new MineData(player.getUniqueId(), corner2, corner1, minimum, maximum,
            Objects.requireNonNull(location), spawn, mineType, false, 5.0);
        mine.setMineData(mineData);
        SQLUtils.claim(location);
        SQLUtils.insert(mine);

        mineStorage.addMine(player.getUniqueId(), mine);

        Task.syncDelayed(() -> spawn.getBlock().setType(Material.AIR, false));
        Task.syncDelayed(() -> flagUtils.setFlags(mine));
        pregenMine.teleport(player);
        mine.handleReset();
      }
    }
  }

  @Subcommand("sendpacket")
  public void sendPacket(Player player) {
    Map<BlockPosition, Map<Vector, WrappedBlockData>> changes = new HashMap<>();

    // Converts player location to chunk coordinates
    int x = player.getLocation().getBlockX() >> 4;
    int y = player.getLocation().getBlockY() >> 4;
    int z = player.getLocation().getBlockZ() >> 4;

    // Creates a new BlockPosition at chunk coordinate
    BlockPosition blockPosition = new BlockPosition(x, y, z);

    // Create a map to hold block changes within the chunk
    Map<Vector, WrappedBlockData> blockChanges = new HashMap<>();

    // Add a sample block change to the map
    Vector sampleBlockLocation = new Vector(x * 16, y * 16, z * 16); // Adjust this based on your actual block location
    WrappedBlockData sampleBlockData = WrappedBlockData.createData(Material.ACACIA_LOG);
    blockChanges.put(sampleBlockLocation, sampleBlockData);

    changes.put(blockPosition, blockChanges);

    // Create a packet for MULTI_BLOCK_CHANGE
    PacketContainer packet = new PacketContainer(Server.MULTI_BLOCK_CHANGE);
    packet.getSectionPositions().write(0, blockPosition);

    WrappedBlockData[] data = new WrappedBlockData[1];
    data[0] = WrappedBlockData.createData(Material.ACACIA_LOG);

    for (Map.Entry<Vector, WrappedBlockData> blockEntry : changes.get(blockPosition).entrySet()) {
      short[] shortLocation = new short[]{(short) ((blockEntry.getKey().getBlockX() & 0x0F) << 12
          | (blockEntry.getKey().getBlockZ() & 0x0F) << 8 | (blockEntry.getKey().getBlockY()
          & 0xFF))};


      packet.getShortArrays().writeSafely(0, shortLocation);
      packet.getBlockDataArrays().writeSafely(0, data);
    }

    ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet);
  }
}
