package me.untouchedodin0.privatemines.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.privatemines.PrivateMines;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("useshop|useplayershop|usepshop")
public class UsePlayerShop extends BaseCommand {

    PrivateMines privateMines;
    MineStorage mineStorage;

    public UsePlayerShop(PrivateMines privateMines, MineStorage mineStorage) {
        this.privateMines = privateMines;
        this.mineStorage = mineStorage;
    }

    @Default
    @CommandCompletion("@players")
    public void usePlayerShop(Player player, Player target) {
        if (!target.isOnline()) {
            player.sendMessage(ChatColor.RED + "Player not found!");
            return;
        } else if (!mineStorage.hasMine(target.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Player does not have a mine!");
        } else {
            player.sendMessage(String.format(ChatColor.GREEN + "Trying to use %s's shop", target.getName()));
        }
    }
}

// Generated with love by @0xC0FFEE (untouchedodin0)
