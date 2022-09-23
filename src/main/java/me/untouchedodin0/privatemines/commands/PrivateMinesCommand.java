package me.untouchedodin0.privatemines.commands;

import me.untouchedodin0.kotlin.menu.Menu;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.WorldBorderUtils;
import me.untouchedodin0.privatemines.config.Config;
import me.untouchedodin0.privatemines.config.MenuConfig;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.mine.MineTypeManager;
import me.untouchedodin0.privatemines.mine.data.MineData;
import me.untouchedodin0.privatemines.utils.LangUtils;
import me.untouchedodin0.privatemines.utils.inventory.PublicMinesMenu;
import me.untouchedodin0.privatemines.utils.world.MineWorldManager;
import org.apache.commons.lang.WordUtils;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.redlib.commandmanager.CommandHook;
import redempt.redlib.commandmanager.Messages;
import redempt.redlib.misc.ChatPrompt;

import java.util.*;

@SuppressWarnings("unused")
public class PrivateMinesCommand {

    PrivateMines privateMines = PrivateMines.getPrivateMines();
    MineStorage mineStorage = privateMines.getMineStorage();
    MineTypeManager mineTypeManager = privateMines.getMineTypeManager();

    @CommandHook("main")
    public void main(CommandSender sender) {
        if (sender instanceof Player player) {
            Menu mainMenu = MenuConfig.getMenus().get("mainMenu");
            mainMenu.open(player);

            WorldBorderUtils worldBorderUtils = new WorldBorderUtils();
            Server server = Bukkit.getServer();
            Location location = player.getLocation();
            double size = 5;

//            player.sendMessage("worldBorderUtils: " + worldBorderUtils);

//            worldBorderUtils.clearBorder(player);
//            if (worldBorderUtils.isSetBorder()) {
//                worldBorderUtils.clearBorder(player);
//            } else {
//                worldBorderUtils.sendWorldBorder(server, player, location, size);
//            }

//
//            PacketContainer fakeExplosion = new PacketContainer(PacketType.Play.Server.EXPLOSION);
//            fakeExplosion.getDoubles()
//                    .write(0, player.getLocation().getX())
//                    .write(1, player.getLocation().getY())
//                    .write(2, player.getLocation().getZ());
//            fakeExplosion.getFloat().write(0, 3.0F);
//            fakeExplosion.getBlockPositionCollectionModifier().write(0, new ArrayList<>());
//
//            player.sendMessage("packetContainer " + fakeExplosion);
//
//            try {
//                protocolManager.sendServerPacket(player, fakeExplosion);
//            } catch (InvocationTargetException e) {
//                throw new RuntimeException(e);
//            }
        }
    }

    @CommandHook("give")
    public void give(CommandSender commandSender, OfflinePlayer target, MineType mineType) {
        MineFactory mineFactory = new MineFactory();
        MineWorldManager mineWorldManager = privateMines.getMineWorldManager();
        Location location = mineWorldManager.getNextFreeLocation();
        MineType defaultMineType = mineTypeManager.getDefaultMineType();

        if (target.getPlayer() != null) {
            if (mineStorage.hasMine(target.getUniqueId())) {
                commandSender.sendMessage(Messages.msg("playerAlreadyOwnsAMine"));
            } else {
                mineFactory.create(target.getPlayer(), location, Objects.requireNonNullElse(mineType, defaultMineType));
                commandSender.sendMessage(ChatColor.GREEN + "You gave " + target.getName() + " a private mine!");
            }
        }
    }

    @CommandHook("delete")
    public void delete(CommandSender commandSender, OfflinePlayer target) {
        if (!mineStorage.hasMine(target.getUniqueId())) {
            commandSender.sendMessage(Messages.msg("playerAlreadyOwnsAMine"));
        } else {
            Mine mine = mineStorage.get(target.getUniqueId());
            commandSender.sendMessage(ChatColor.GREEN + "You deleted " + target.getName() + "'s private mine!");
            if (mine != null) {
                mine.delete();
            }
        }
    }

    @CommandHook("reset")
    public void reset(Player player) {
        if (!mineStorage.hasMine(player)) {
            player.sendMessage(Messages.msg("youDontOwnAMine"));
        } else {
            Mine mine = mineStorage.get(player);
            if (mine != null) {
                mine.resetNoCheck();
            }
            player.sendMessage(Messages.msg("yourMineHasBeenReset"));
        }
    }

    @CommandHook("upgrade")
    public void upgrade(CommandSender commandSender, Player player) {
        if (!mineStorage.hasMine(player)) {
            commandSender.sendMessage(ChatColor.RED + "That player doesn't own a mine!");
            player.sendMessage(Messages.msg("youDontOwnAMine"));
        } else {
            Mine mine = mineStorage.get(player);
            if (mine != null) {
                mine.upgrade();
            }
        }
    }

    @CommandHook("expand")
    public void expand(CommandSender commandSender, Player target, int amount) {
        if (!mineStorage.hasMine(target)) {
            commandSender.sendMessage(ChatColor.RED + "That player doesn't own a mine!");
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
            }
        }
    }

    @CommandHook("teleport")
    public void teleport(Player player) {
        if (!mineStorage.hasMine(player.getUniqueId())) {
            player.sendMessage(Messages.msg("youDontOwnAMine"));
        } else {
            Mine mine = mineStorage.get(player.getUniqueId());
            player.sendMessage(Messages.msg("youHaveBeenTeleportedToYourMine"));
            if (mine != null) {
                mine.teleport(player);
            }
        }
    }

    @CommandHook("visit")
    public void visit(Player player, OfflinePlayer target) {
        if (!mineStorage.hasMine(target.getUniqueId())) {
            player.sendMessage(Messages.msg("playerDoesntOwnAMine"));
        } else {
            Mine mine = mineStorage.get(target.getUniqueId());
            if (mine != null) {
                mine.teleport(player);
                player.sendMessage(ChatColor.GREEN + "You are now visiting " + target.getName() + "'s mine!");
            }
        }
    }

    @CommandHook("setblocks")
    public void setBlocks(Player player, Player target, Material[] materials) {
        Mine mine = mineStorage.get(target);
        MineData mineData;
        Map<Material, Double> map = new HashMap<>();

        if (mine != null) {
            for (Material material : materials) {
                if (material.isBlock()) {
                    map.put(material, 1.0);
                } else {
                    player.sendMessage(ChatColor.RED + "Could not add " + material.name() + " as it wasn't a solid block!");
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
            player.sendMessage(ChatColor.GREEN + "Successfully set your tax to " + tax + "%");
        }
    }

    @CommandHook("ban")
    public void ban(Player player, Player target) {
        Mine mine = mineStorage.get(player);
        if (mine != null) {
            MineData mineData = mine.getMineData();
            UUID uuid = target.getUniqueId();

            if (mineData.getBannedPlayers().contains(uuid)) {
                player.sendMessage(ChatColor.RED + "Target is already banned!");
            } else {
                mineData.addBannedPlayer(uuid);
                mine.setMineData(mineData);
                mine.saveMineData(player, mineData);
                player.sendMessage(ChatColor.GREEN + "Successfully banned " + target.getName());
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
                player.sendMessage(ChatColor.RED + "Target isn't banned!");
            } else {
                mineData.removeBannedPlayer(uuid);
                mine.setMineData(mineData);
                mine.saveMineData(player, mineData);
                player.sendMessage(ChatColor.GREEN + "Successfully unbanned " + target.getName());
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
    public void pregen(Player player) {

        List<String> YES = LangUtils.YES;
        List<String> NO = LangUtils.NO;

        if (privateMines.isPregenMode()) {
            player.sendMessage("" + LangUtils.YES);

            ChatPrompt.prompt(player, ChatColor.YELLOW + "Would you like to disable pregen mode?", response -> {
                if (LangUtils.YES.contains(WordUtils.capitalize(response))) {
                    player.sendMessage(ChatColor.GREEN + "Disabling pregen mode!");
                    privateMines.setPregenMode(false);
                } else if (LangUtils.NO.contains(WordUtils.capitalize(response))) {
                    player.sendMessage(ChatColor.GREEN + "Pregen mode will be kept on!");
                }
            });
        } else {
            ChatPrompt.prompt(player, ChatColor.YELLOW + "Would you like to enable pregen mode?", response -> {
                if (LangUtils.YES.contains(WordUtils.capitalize(response))) {
                    player.sendMessage(ChatColor.GREEN + "Enabling pregen mode!");
                    privateMines.setPregenMode(true);
                } else if (LangUtils.NO.contains(WordUtils.capitalize(response))) {
                    player.sendMessage(ChatColor.GREEN + "Pregen mode will be turned off!");
                    privateMines.setPregenMode(false);
                }
            });
        }
    }

    @CommandHook("reload")
    public void reload(Player player) {
        privateMines.getConfigManager().reload();
        privateMines.getConfigManager().load();
        player.sendMessage("reload test");
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
}