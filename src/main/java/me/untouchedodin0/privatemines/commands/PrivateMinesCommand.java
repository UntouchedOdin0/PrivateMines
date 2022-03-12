package me.untouchedodin0.privatemines.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.config.MineConfig;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.type.MineType;
import org.bukkit.entity.Player;

@CommandAlias("privatemines|pmines|pmine")
public class PrivateMinesCommand extends BaseCommand {

    PrivateMines privateMines = PrivateMines.getPrivateMines();

    @Subcommand("give")
    @CommandCompletion("@players")
    public void give(Player player, Player target) {
        player.sendMessage("Giving " + target.getName() + " a private mine!");
        MineFactory mineFactory = new MineFactory();
        MineType mineType = MineConfig.mineTypes.get("Test");
        player.sendMessage("mine factory: " + mineFactory);
        mineFactory.create(player.getLocation(), mineType);
    }

    @Subcommand("delete")
    @CommandCompletion("@players")
    public void delete(Player player, Player target) {
        if (target != null) {
            player.sendMessage("Deleting " + target.getName() + "'s private mine!");
        }
    }
}
