package me.untouchedodin0.privatemines.commands;

import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.mine.MineTypeManager;
import me.untouchedodin0.privatemines.utils.inventory.MainMenu;
import me.untouchedodin0.privatemines.utils.world.MineWorldManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.redlib.commandmanager.CommandHook;

@SuppressWarnings("unused")
public class PrivateMinesCommand {

    PrivateMines privateMines = PrivateMines.getPrivateMines();
    MineStorage mineStorage = privateMines.getMineStorage();
    MineTypeManager mineTypeManager = privateMines.getMineTypeManager();

    @CommandHook("main")
    public void main(CommandSender sender) {
        Player player = (Player) sender;
        MainMenu mainMenu = new MainMenu(mineStorage);
        mainMenu.openMainMenu(player);
    }

    @CommandHook("give")
    public void give(CommandSender commandSender, OfflinePlayer target) {
        MineFactory mineFactory = new MineFactory();
        MineWorldManager mineWorldManager = privateMines.getMineWorldManager();
        Location location = mineWorldManager.getNextFreeLocation();
        MineType mineType = mineTypeManager.getDefaultMineType();

        if (target.getPlayer() != null) {
            if (mineStorage.hasMine(target.getUniqueId())) {
                commandSender.sendMessage(ChatColor.RED + "That player already has a mine!");
            } else {
                mineFactory.create(target.getPlayer(), location, mineType);
                commandSender.sendMessage(ChatColor.GREEN + "You gave " + target.getName() + " a private mine!");
            }
        }
    }
    @CommandHook("delete")
    public void delete(CommandSender commandSender, OfflinePlayer target) {
        if (!mineStorage.hasMine(target.getUniqueId())) {
            commandSender.sendMessage(ChatColor.RED + "That player does not have a mine!");
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
        if (!mineStorage.hasMine(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You do not have a mine!");
        } else {
            Mine mine = mineStorage.get(player.getUniqueId());
            if (mine != null) {
                mine.reset();
            }
            player.sendMessage(ChatColor.GREEN + "You have reset your mine!");
        }
    }

    @CommandHook("upgrade")
    public void upgrade(CommandSender commandSender, Player player) {
        if (!mineStorage.hasMine(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You do not have a mine!");
        } else {
            Mine mine = mineStorage.get(player.getUniqueId());
            if (mine != null) {
                mine.upgrade();
            }
        }
    }

    @CommandHook("expand")
    public void expand(Player player, Player target, int amount) {
        if (!mineStorage.hasMine(target.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "That player does not have a mine!");
        } else {
            Mine mine = mineStorage.get(target.getUniqueId());
            if (mine != null) {
                if (mine.canExpand(amount)) {
                    for (int i = 0; i < amount; i++) {
                        mine.expand();
                    }
                }
            }
        }
    }

    @CommandHook("teleport")
    public void teleport(Player player) {
        if (!mineStorage.hasMine(player.getUniqueId())) {
            player.sendMessage(ChatColor.GREEN + "You don't own a mine!");
        } else {
            Mine mine = mineStorage.get(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "You teleported to your mine!");
            if (mine != null) {
                mine.teleport(player);
            }
        }
    }
    @CommandHook("visit")
    public void visit(Player player, OfflinePlayer target) {
        if (!mineStorage.hasMine(target.getUniqueId())) {
            player.sendMessage("That player does not have a mine!");
        } else {
            Mine mine = mineStorage.get(target.getUniqueId());
            if (mine != null) {
                mine.teleport(player);
                player.sendMessage(ChatColor.GREEN + "You are now visiting " + target.getName() + "'s mine!");
            }
        }
    }
}
