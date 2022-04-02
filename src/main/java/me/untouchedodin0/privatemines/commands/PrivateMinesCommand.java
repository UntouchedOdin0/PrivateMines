package me.untouchedodin0.privatemines.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.config.Config;
import me.untouchedodin0.privatemines.config.MineConfig;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.utils.inventory.MainMenu;
import me.untouchedodin0.privatemines.utils.world.MineWorldManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import redempt.redlib.config.ConfigManager;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@CommandAlias("privatemines|pmines|pmine")
public class PrivateMinesCommand extends BaseCommand {

    PrivateMines privateMines;
    MineStorage mineStorage;

    public PrivateMinesCommand(PrivateMines privateMines) {
        this.privateMines = privateMines;
        this.mineStorage = privateMines.getMineStorage();
    }

    @Default
    public void mainCommand(Player player) {
        MainMenu mainMenu = new MainMenu(mineStorage);
        mainMenu.openMainMenu(player);
    }

    @Subcommand("give")
    @CommandCompletion("@players")
    @CommandPermission("privatemines.give")
    public void give(Player player, Player target, String type) {
        player.sendMessage(ChatColor.GREEN + "Giving " + target.getName() + " a private mine!");
        MineFactory mineFactory = new MineFactory();
        MineWorldManager mineWorldManager = privateMines.getMineWorldManager();
        Location location = mineWorldManager.getNextFreeLocation();
        MineType mineType = MineConfig.mineTypes.get("Test");
        privateMines.getLogger().info(MineConfig.mineTypes.keySet().toString());
        mineFactory.create(player, location, mineType);
    }


    @Subcommand("delete")
    @CommandCompletion("@players")
    @CommandPermission("privatemines.delete")
    public void delete(Player player, Player target) {
        if (target != null) {
            if (!privateMines.getMineStorage().hasMine(target.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "Player doesn't own a mine!");
            } else {
                Mine mine = privateMines.getMineStorage().get(target.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "Deleting " + target.getName() + "'s private mine!");
                if (mine != null) {
                    mine.delete(target.getUniqueId());
                }
                privateMines.getMineStorage().removeMine(target.getUniqueId());
            }
        }
    }

    @Subcommand("teleport")
    @CommandPermission("privatemines.teleport")
    public void teleport(Player player) {
        if (!mineStorage.hasMine(player.getUniqueId())) {
            player.sendMessage("Player doesn't own a mine!");
        } else {
            Mine mine = mineStorage.get(player.getUniqueId());
            if (mine != null) {
                mine.reset();
                mine.teleport(player);
            }
        }
    }

    @Subcommand("reset")
    @CommandPermission("privatemines.teleport")
    public void reset(Player player) {
        if (!privateMines.getMineStorage().hasMine(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Player doesn't own a mine!");
        } else {
            Mine mine = privateMines.getMineStorage().get(player.getUniqueId());
            if (mine != null) {
                mine.reset();
                mine.teleport(player);
            }
        }
    }

    /*
        This can create severe lag on the server, I take no blame for the lag caused.
     */

    @Subcommand("dev/reset/stresstest")
    @CommandPermission("privatemines.dev.stresstest")
    @Syntax("<times> &e- Reset your mine a certain amount of times to test the speed")
    public void stressTest(Player player, int times) {
        if (!privateMines.getMineStorage().hasMine(player.getUniqueId())) {
            player.sendMessage("Player doesn't own a mine!");
        } else {
            Mine mine = privateMines.getMineStorage().get(player.getUniqueId());
            if (mine != null) {
                player.sendMessage(ChatColor.GREEN + "Stress test resetting your mine " + ChatColor.GOLD + times + ChatColor.GREEN + " times!");
                AtomicInteger atomicInteger = new AtomicInteger();

                Instant start = Instant.now();
                for (int i = 0; i < times; i++) {
                    mine.resetNoMessage();
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                                TextComponent.fromLegacyText(ChatColor.GREEN + "Finished Reset #" +
                                                                                     atomicInteger.incrementAndGet()));
                }
                Instant filled = Instant.now();
                Duration durationToFill = Duration.between(start, filled);
                player.sendMessage(String.format(ChatColor.GREEN + "It took %dms to fill your mine %d times!", durationToFill.toMillis(), times));
            }
        }
    }

    @Subcommand("reload")
    public void reload(Player player) {
        ConfigManager configManager = privateMines.getConfigManager();
        configManager.reload();
        player.sendMessage(ChatColor.GREEN + "Private Mines has been reloaded!");
    }
}
