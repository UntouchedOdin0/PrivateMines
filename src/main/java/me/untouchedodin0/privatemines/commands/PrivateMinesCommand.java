/**
 * MIT License
 * <p>
 * Copyright (c) 2021 - 2022 Kyle Hicks
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.untouchedodin0.privatemines.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import com.sk89q.worldedit.WorldEdit;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.messages.LangKeys;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.mine.MineTypeManager;
import me.untouchedodin0.privatemines.mine.data.MineData;
import me.untouchedodin0.privatemines.utils.Utils;
import me.untouchedodin0.privatemines.utils.inventory.LocaleInventory;
import me.untouchedodin0.privatemines.utils.inventory.MainMenu;
import me.untouchedodin0.privatemines.utils.slime.SlimeUtils;
import me.untouchedodin0.privatemines.utils.world.MineWorldManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redempt.crunch.CompiledExpression;
import redempt.crunch.Crunch;
import redempt.redlib.commandmanager.Messages;
import redempt.redlib.config.ConfigManager;
import redempt.redlib.inventorygui.InventoryGUI;
import redempt.redlib.inventorygui.ItemButton;
import redempt.redlib.itemutils.ItemBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;

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
    public void mainCommand(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            return;
        } else {
            MainMenu mainMenu = new MainMenu(mineStorage);
            mainMenu.openMainMenu(player);
        }
    }

    @Subcommand("give")
    @CommandCompletion("@players")
    @CommandPermission("privatemines.give")
    public void give(CommandSender player, OfflinePlayer target) {
        Player targetPlayer = target.getPlayer();
        MineFactory mineFactory = new MineFactory();
        MineWorldManager mineWorldManager = privateMines.getMineWorldManager();
        Location location = mineWorldManager.getNextFreeLocation();
        MineType mineType = mineTypeManager.getDefaultMineType();

        if (targetPlayer != null) {
            if (mineStorage.hasMine(targetPlayer.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "That player already has a mine!");
            } else {
                player.sendMessage(ChatColor.GREEN + "Giving " + target.getName() + " a private mine!");
                privateMines.getLogger().info("Giving the player a mine using the mine type of " + mineType.getName());
                mineFactory.create(targetPlayer, location, mineType);
                targetPlayer.sendMessage(Messages.msg("youHaveBeenGivenAMine"));
            }
        }
    }

    @Subcommand("delete")
    @CommandCompletion("@players")
    @CommandPermission("privatemines.delete")
    public void delete(Player player, Player target) {
        if (target != null) {
            if (!privateMines.getMineStorage().hasMine(target.getUniqueId())) {
                getCurrentCommandIssuer().sendInfo(LangKeys.INFO_PRIVATEMINE_PLAYER_DOESNT_OWN_A_MINE);
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
        String doesntOwnAMine = Messages.msg("youDontOwnAMine");
        String teleportedToMine = Messages.msg("youHaveBeenTeleportedToYourMine");

        if (!mineStorage.hasMine(player.getUniqueId())) {
            player.sendMessage(doesntOwnAMine);
        } else {
            Mine mine = mineStorage.get(player.getUniqueId());
            if (mine != null) {
                mine.teleport(player);
                player.sendMessage(teleportedToMine);
            }
        }
    }

    @Subcommand("reset")
    @CommandPermission("privatemines.reset")
    public void reset(Player player) {
        String noMine = Messages.msg("youDontOwnAMine");
        String mineReset = Messages.msg("yourMineHasBeenReset");

        if (!privateMines.getMineStorage().hasMine(player.getUniqueId())) {
            player.sendMessage(noMine);
        } else {
            Mine mine = privateMines.getMineStorage().get(player.getUniqueId());
            if (mine != null) {
                mine.reset();
                player.sendMessage(mineReset);
            }
        }
    }

    //todo fix this
    @Subcommand("expand")
    @CommandPermission("privatemines.expand")
    public void expand(Player player, int amount) {
        if (!privateMines.getMineStorage().hasMine(player.getUniqueId())) {
            getCurrentCommandIssuer().sendInfo(LangKeys.INFO_PRIVATEMINE_PLAYER_DOESNT_OWN_A_MINE);
        } else {
            Mine mine = privateMines.getMineStorage().get(player.getUniqueId());
            if (mine != null) {
                for (int i = 0; i < amount; i++) {
                    mine.expand();
                }
            }
        }
    }

    @Subcommand("upgrade")
    @CommandPermission("privatemines.upgrade")
    public void upgrade(Player player) {
        if (!privateMines.getMineStorage().hasMine(player.getUniqueId())) {
            getCurrentCommandIssuer().sendInfo(LangKeys.INFO_PRIVATEMINE_PLAYER_DOESNT_OWN_A_MINE);
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
            getCurrentCommandIssuer().sendInfo(LangKeys.INFO_PRIVATEMINE_PLAYER_DOESNT_OWN_A_MINE);
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

    @Subcommand("ban")
    @CommandPermission("privatemines.ban")
    public void ban(Player player, Player target) {
        if (!privateMines.getMineStorage().hasMine(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You don't have a mine!");
        } else {
            Mine mine = privateMines.getMineStorage().get(player.getUniqueId());
            if (mine != null) {
                mine.ban(Objects.requireNonNull(target));
            }
        }
    }

    @Subcommand("unban")
    @CommandPermission("privatemines.unban")
    public void unban(Player player, Player target) {
        if (!privateMines.getMineStorage().hasMine(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You don't have a mine!");
        } else {
            Mine mine = privateMines.getMineStorage().get(player.getUniqueId());
            if (mine != null) {
                mine.unban(target);
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

    @Subcommand("changelocale")
    public void changeLocale(Player player) {
        LocaleInventory.openLocaleMenu(player);
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

                CompletableFuture.runAsync(() -> {
                    for (int i = 0; i < times; i++) {
                        mine.resetNoCheck();
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                                    TextComponent.fromLegacyText(ChatColor.GREEN + "Finished Reset #" +
                                                                                         atomicInteger.incrementAndGet()));
                    }
                }).thenRun(() -> {
                    Instant filled = Instant.now();
                    Duration durationToFill = Duration.between(start, filled);
                    System.out.println("Finished stress test in " + Duration.between(start, Instant.now()).toMillis() + "ms");
                    System.out.println("Thread id: " + Thread.currentThread().getId());
                    player.sendMessage(String.format(ChatColor.GREEN + "It took %dms to fill your mine %d times!", durationToFill.toMillis(), times));
                });
            }
        }
    }

    @Subcommand("dev/reset/stresstest/expression")
    @CommandPermission("privatemines.dev.stresstest.expression")
    @Syntax("<times> &e- Reset your mine a certain amount of times to test the speed using an expression")
    public void stressTestExpression(Player player, String expression) {
        if (!privateMines.getMineStorage().hasMine(player.getUniqueId())) {
            player.sendMessage("Player doesn't own a mine!");
        } else {
            Mine mine = privateMines.getMineStorage().get(player.getUniqueId());
            if (mine != null) {
                CompiledExpression compiledExpression = Crunch.compileExpression(expression);
                double value = compiledExpression.evaluate();

                player.sendMessage(ChatColor.GREEN + "Stress test resetting your mine " + ChatColor.GOLD + compiledExpression.evaluate() + ChatColor.GREEN + " times!");
                AtomicInteger atomicInteger = new AtomicInteger();
                Instant start = Instant.now();
                CompletableFuture.runAsync(() -> {
                    for (int i = 0; i < value; i++) {
                        mine.reset();
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                                    TextComponent.fromLegacyText(ChatColor.GREEN + "Finished Reset #" +
                                                                                         atomicInteger.incrementAndGet()));
                    }
                }).thenRun(() -> {
                    Instant filled = Instant.now();
                    Duration durationToFill = Duration.between(start, filled);
                    System.out.println("Finished stress test in " + Duration.between(start, Instant.now()).toMillis() + "ms");
                    System.out.println("Thread id: " + Thread.currentThread().getId());
                    player.sendMessage(String.format(ChatColor.GREEN + "It took %dms to fill your mine %d times!", durationToFill.toMillis(), (int) value));
                });
            }
        }
    }

    @Subcommand("dev/types/listall")
    @CommandPermission("privatemines.dev.listall.types")
    public void listallTypes(Player player) {
        mineTypeManager.getMineTypes().forEach((s, mineType) -> player.sendMessage(ChatColor.GREEN + "- " + mineType.getName()));
    }

    @Subcommand("dev/storage/listall")
    @CommandPermission("privatemines.dev.listall.storage")
    public void listallStorage(Player player) {
        MineStorage mineStorage = privateMines.getMineStorage();
        mineStorage.getMines().forEach((uuid, mine) -> {
            MineData mineData = mine.getMineData();
            UUID mineOwner = mineData.getMineOwner();
            player.sendMessage(ChatColor.GREEN + "- "
                                       + Bukkit.getOfflinePlayer(mineOwner).getName()
                                       + ChatColor.GRAY + " (" + mineOwner + ")");
        });
    }

    @Subcommand("dev/purge/noconfirm")
    @CommandPermission("privatemines.purge.noconfirm")
    public void purgeNoConfirm(Player player) {
        Map<UUID, Mine> mines = mineStorage.getMines();

        if (mines.isEmpty()) {
            player.sendMessage(ChatColor.RED + "No mines to purge!");
        } else {
            for (Mine mine : mines.values()) {
                player.sendMessage(ChatColor.GREEN + "Purging mine " + mine);
                mine.delete(mine.getMineData().getMineOwner());
            }
        }
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

    @Subcommand("debugpaste")
    @CommandPermission("privatemines.debugpaste")
    public void debugPaste(Player player) {
        player.sendMessage(ChatColor.GREEN + "Pasting debug paste");

        String pluginName = privateMines.getDescription().getName();
        String pluginVersion = privateMines.getDescription().getVersion();
        String worldEditVersion = WorldEdit.getVersion();
        String isRunningFastAsyncWorldEdit = Bukkit.getServer().getPluginManager().isPluginEnabled("FastAsyncWorldEdit") ? "Yes" : "No";

        String sb =
                "Plugin Name: "       + pluginName +
                "\n"                  +
                "Plugin Version: "    + pluginVersion +
                "\n"                  +
                "WorldEdit Version: " + worldEditVersion +
                "\n"                  +
                "Is FastAsyncWorldEdit enabled? " + isRunningFastAsyncWorldEdit;

        final HttpClient httpClient = HttpClient.newHttpClient();
        final HttpRequest httpRequest = HttpRequest.newBuilder(URI.create("https://www.toptal.com/developers/hastebin/documents"))
                .POST(HttpRequest.BodyPublishers.ofString(sb))
                .build();
        final CompletableFuture<HttpResponse<String>> responseFuture = httpClient.sendAsync(httpRequest, HttpResponse.BodyHandlers.ofString());
        try {
            String body = responseFuture.get().body();
            Matcher matcher = Utils.pastePattern.matcher(body);

            if (matcher.find()) {
                String pasteId = matcher.group(0);
                String url = "https://www.toptal.com/developers/hastebin/" + pasteId;
                player.sendMessage(ChatColor.GREEN + url);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
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
