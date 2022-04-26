package me.untouchedodin0.privatemines.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.mine.MineTypeManager;
import me.untouchedodin0.privatemines.mine.data.MineData;
import me.untouchedodin0.privatemines.utils.inventory.MainMenu;
import me.untouchedodin0.privatemines.utils.slime.SlimeUtils;
import me.untouchedodin0.privatemines.utils.world.MineWorldManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.redlib.config.ConfigManager;
import redempt.redlib.inventorygui.InventoryGUI;
import redempt.redlib.inventorygui.ItemButton;
import redempt.redlib.itemutils.ItemBuilder;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static net.md_5.bungee.api.ChatColor.*;

@CommandAlias("privatemines|pmines|pmine")
public class PrivateMinesCommand extends BaseCommand {

    PrivateMines privateMines;
    MineStorage mineStorage;
    MineTypeManager mineTypeManager;

    public PrivateMinesCommand(PrivateMines privateMines) {
        this.privateMines = privateMines;
        this.mineStorage = privateMines.getMineStorage();
        this.mineTypeManager = privateMines.getMineTypeManager();
    }

    @Default
    public void mainCommand(Player player) {
        MainMenu mainMenu = new MainMenu(mineStorage);
        mainMenu.openMainMenu(player);
    }

    @Subcommand("give")
    @CommandCompletion("@players")
    @CommandPermission("privatemines.give")
    public void give(CommandSender player, Player target) {
        player.sendMessage(ChatColor.GREEN + "Giving " + target.getName() + " a private mine!");
        MineFactory mineFactory = new MineFactory();
        MineWorldManager mineWorldManager = privateMines.getMineWorldManager();
        Location location = mineWorldManager.getNextFreeLocation();
        MineType mineType = mineTypeManager.getDefaultMineType();
        privateMines.getLogger().info("Giving the player a mine using the mine type of " + mineType.getName());
        mineFactory.create(target, location, mineType);
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

    @Subcommand("purge")
    @CommandPermission("privatemines.purge")
    public void purge(Player player) {
        int totalMines = mineStorage.getTotalMines();
        TextComponent clickMeToPurge = new TextComponent(GRAY + "Click me to purge ");
        TextComponent amountOfMines = new TextComponent(String.format(GOLD + " %d", totalMines));
        TextComponent mines = new TextComponent(GRAY + " mines");
        clickMeToPurge.addExtra(amountOfMines);
        clickMeToPurge.addExtra(mines);
        clickMeToPurge.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/privatemines dev/purge/noconfirm"));
        player.spigot().sendMessage(clickMeToPurge);
    }

    @Subcommand("teleport")
    @CommandPermission("privatemines.teleport")
    public void teleport(Player player) {
        if (!mineStorage.hasMine(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Player doesn't own a mine!");
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

    //todo fix this
    @Subcommand("expand")
    @CommandPermission("privatemines.expand")
    public void expand(Player player, int amount) {
        privateMines.getLogger().info("soon.");
//        if (!privateMines.getMineStorage().hasMine(player.getUniqueId())) {
//            player.sendMessage(ChatColor.RED + "Player doesn't own a mine!");
//        } else {
//            Mine mine = privateMines.getMineStorage().get(player.getUniqueId());
//            if (mine != null) {
//                player.sendMessage("" + mine.canExpand(amount));
//                mine.expand(amount);
//            }
//        }
    }

    @Subcommand("upgrade")
    @CommandPermission("privatemines.upgrade")
    public void upgrade(Player player) {
        if (!privateMines.getMineStorage().hasMine(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Target doesn't have a mine!");
        } else {
            Mine mine = privateMines.getMineStorage().get(player.getUniqueId());
            if (mine != null) {
                mine.upgrade();
            }
        }
    }

    @Subcommand("tax")
    @CommandPermission("privatemines.tax")
    public void tax(Player player, Double tax) {
        if (!privateMines.getMineStorage().hasMine(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You don't have a mine!");
        } else {
            Mine mine = privateMines.getMineStorage().get(player.getUniqueId());
            if (mine != null) {
                if (tax == null) {
                    player.sendMessage(ChatColor.GREEN + "Your tax is currently set to " + tax + "%");
                    return;
                }
                MineData mineData = mine.getMineData();
                if (tax <= 100 && tax > 0) {
                    mineData.setTax(tax);
                    player.sendMessage(ChatColor.GREEN + "Successfully set your tax to " + tax + "%!");
                    mine.setMineData(mineData);
                    mineStorage.replaceMine(player.getUniqueId(), mine);
                    mine.saveMineData(player, mineData);
                } else {
                    player.sendMessage(ChatColor.RED + "Please set a valid tax amount!");
                }
            }
        }
    }

    @Subcommand("info")
    @CommandPermission("privatemines.info")
    public void info(Player player) {
        Mine mine = mineStorage.getClosest(player, player.getLocation());
        MineData mineData;
        if (mine == null) {
            player.sendMessage(ChatColor.RED + "You're not in any mines!");
        } else {
            mineData = mine.getMineData();
            String name = Bukkit.getOfflinePlayer(mineData.getMineOwner()).getName();
            String inventoryName = String.format("%s's Private Mine", name);
            if (inventoryName.length() <= 32) {
                // if inventory name is less than or equals to 32 (max inventory size) add players name into gui else don't
                InventoryGUI inventoryGUI = new InventoryGUI(9, name + "'s Mine");
                ItemBuilder tax = new ItemBuilder(Material.PAPER)
                        .setName(ChatColor.GREEN + "Tax")
                        .setLore(ChatColor.GREEN + String.valueOf(mineData.getTax()) + "%");
                ItemButton taxButton = ItemButton.create(tax, inventoryClickEvent -> {
                    // put click t hings here?
                });
                ItemBuilder askToReset = new ItemBuilder(Material.STONE_BUTTON)
                        .setName(ChatColor.GREEN + "Request Mine Reset")
                        .addLore(ChatColor.GRAY + "Click me to request")
                        .addLore(ChatColor.GRAY + "the owner to reset the mine");
                ItemButton askToResetButton = ItemButton.create(askToReset, inventoryClickEvent -> {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(mineData.getMineOwner());
                    if (!offlinePlayer.isOnline()) return;
                    Player owner = offlinePlayer.getPlayer();
                    if (owner != null) {
                        TextComponent clicktoreset = new TextComponent(GREEN + player.getName() + " has requested you to reset the mine, click me to reset the mine!");
                        clicktoreset.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/privatemines reset"));
                        owner.spigot().sendMessage(clicktoreset);
                    }
                });
                inventoryGUI.addButton(0, taxButton);
                inventoryGUI.addButton(1, askToResetButton);

                inventoryGUI.open(player);
            }
        }
    }

    @Subcommand("open")
    @CommandPermission("privatemines.open")
    public void open(Player player) {
        Mine mine = mineStorage.get(player.getUniqueId());
        if (mine == null) {
            player.sendMessage(ChatColor.RED + "You don't own a mine!");
        } else {
            MineData mineData = mine.getMineData();
            mineData.setOpen(true);
            mine.setMineData(mineData);
            mine.saveMineData(player, mineData);
            player.sendMessage(ChatColor.GREEN + "You have opened your mine!");
        }
    }

    @Subcommand("close")
    @CommandPermission("privatemines.close")
    public void close(Player player) {
        Mine mine = mineStorage.get(player.getUniqueId());
        if (mine == null) {
            player.sendMessage(ChatColor.RED + "You don't own a mine!");
        } else {
            MineData mineData = mine.getMineData();
            mineData.setOpen(false);
            mine.setMineData(mineData);
            mine.saveMineData(player, mineData);
            player.sendMessage(ChatColor.GRAY + "You have closed your mine!");
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

    @Subcommand("dev/types/listall")
    @CommandPermission("privatemines.dev.listall")
    public void listall(Player player) {
        mineTypeManager.getMineTypes().forEach((s, mineType) -> {
            player.sendMessage(ChatColor.GREEN + "- " + mineType.getName());
        });
    }

    @Subcommand("dev/purge/noconfirm")
    @CommandPermission("privatemines.purge.noconfirm")
    public void purgeNoConfirm(Player player) {
        Map<UUID, Mine> mines = mineStorage.getMines();
        mines.forEach((uuid, mine) -> {
            mine.delete(uuid);
        });
    }


    // loadSlimeWorld(String worldName, String loaderName, boolean readOnly, SlimePropertyMap propertyMap, Consumer<SlimeWorld> slimeWorldConsumer) {
    @Subcommand("dev/slime/createmine")
    @CommandPermission("privatemines.slime.createmine")
    public void createSlimeWorld(Player player) {
        SlimeUtils slimeUtils = privateMines.getSlimeUtils();
        Map<UUID, SlimePropertyMap> slimePropertyMapMap = slimeUtils.getSlimeMap();
        UUID uuid = player.getUniqueId();
        String name = uuid.toString();
        SlimePropertyMap propertyMap = slimePropertyMapMap.get(uuid);
        slimeUtils.setupSlimeWorld(uuid);
//        slimeUtils.loadSlimeWorld(name, "file", false, propertyMap);
        slimeUtils.loadAndGenerateSlimeWorld(name, "file", false, propertyMap);

        World world = Bukkit.getWorld(name);
        if (world == null) {
            player.sendMessage("World is null");
            return;
        } else {
            Location location = world.getSpawnLocation();
            player.teleport(location);
            player.sendMessage("location: " + location);
        }
    }

    @Subcommand("reload")
    @CommandPermission("privatemines.reload")
    public void reload(Player player) {
        ConfigManager configManager = privateMines.getConfigManager();
        configManager.reload();
        player.sendMessage(ChatColor.GREEN + "Private Mines has been reloaded!");
    }
}
