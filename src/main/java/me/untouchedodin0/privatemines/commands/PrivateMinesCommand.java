package me.untouchedodin0.privatemines.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.config.MineConfig;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.storage.MineStorage;
import me.untouchedodin0.privatemines.type.MineType;
import org.bukkit.entity.Player;

@CommandAlias("privatemines|pmines|pmine")
public class PrivateMinesCommand extends BaseCommand {

    PrivateMines privateMines = PrivateMines.getPrivateMines();
    MineStorage mineStorage; // = privateMines.getMineStorage();

    @Subcommand("give")
    @CommandCompletion("@players")
    public void give(Player player, Player target) {
        player.sendMessage("Giving " + target.getName() + " a private mine!");
        MineFactory mineFactory = new MineFactory();
        MineType mineType = MineConfig.mineTypes.get("Test");
        player.sendMessage("mine factory: " + mineFactory);
        mineFactory.create(player, player.getLocation(), mineType);
    }

    @Subcommand("delete")
    @CommandCompletion("@players")
    public void delete(Player player, Player target) {
        this.mineStorage = privateMines.getMineStorage();

        if (target != null) {
            if (!mineStorage.hasMine(player.getUniqueId())) {
                player.sendMessage("Player doesn't own a mine!");
            } else {
                Mine mine = mineStorage.get(target.getUniqueId());
                player.sendMessage("Deleting " + target.getName() + "'s private mine!");
                mine.delete();
            }
        }
    }

    @Subcommand("teleport")
    public void teleport(Player player) {
        this.mineStorage = privateMines.getMineStorage();

        if (!mineStorage.hasMine(player.getUniqueId())) {
            player.sendMessage("you got no mine, fam!");
        } else {
            Mine mine = mineStorage.get(player.getUniqueId());
            mine.teleport(player);
        }
    }
}
