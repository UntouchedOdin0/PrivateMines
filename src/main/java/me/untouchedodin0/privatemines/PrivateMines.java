package me.untouchedodin0.privatemines;

import io.papermc.lib.PaperLib;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.commands.PrivateMinesCommand;
import me.untouchedodin0.privatemines.config.Config;
import me.untouchedodin0.privatemines.config.MenuConfig;
import me.untouchedodin0.privatemines.config.MessagesConfig;
import me.untouchedodin0.privatemines.config.MineConfig;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.iterator.SchematicIterator;
import me.untouchedodin0.privatemines.listener.PlayerJoinListener;
import me.untouchedodin0.privatemines.listener.sell.AutoSellListener;
import me.untouchedodin0.privatemines.listener.sell.UPCSellListener;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.mine.MineTypeManager;
import me.untouchedodin0.privatemines.mine.data.MineData;
import me.untouchedodin0.privatemines.mine.data.MineDataBuilder;
import me.untouchedodin0.privatemines.storage.SchematicStorage;
import me.untouchedodin0.privatemines.storage.sql.SQLite;
import me.untouchedodin0.privatemines.utils.Utils;
import me.untouchedodin0.privatemines.utils.metrics.Metrics;
import me.untouchedodin0.privatemines.utils.metrics.Metrics.SingleLineChart;
import me.untouchedodin0.privatemines.utils.slime.SlimeUtils;
import me.untouchedodin0.privatemines.utils.world.MineWorldManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.RedLib;
import redempt.redlib.commandmanager.ArgType;
import redempt.redlib.commandmanager.CommandParser;
import redempt.redlib.commandmanager.Messages;
import redempt.redlib.config.ConfigManager;
import redempt.redlib.inventorygui.InventoryGUI;
import redempt.redlib.misc.LocationUtils;
import redempt.redlib.misc.Task;
import redempt.redlib.sql.SQLHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

/**
 * TODO Make a way for people to register mines via a discord channel before server launches
 */

public class PrivateMines extends JavaPlugin {

    private static PrivateMines privateMines;
    private static final int PLUGIN_ID = 11413;
    public int Y_LEVEL = 50;
    public int MINE_DISTANCE = 150;

    private final Path minesDirectory = getDataFolder().toPath().resolve("mines");
    private final Path schematicsDirectory = getDataFolder().toPath().resolve("schematics");
    private final Path addonsDirectory = getDataFolder().toPath().resolve("addons");

    private SchematicStorage schematicStorage;
    private SchematicIterator schematicIterator;
    private MineFactory mineFactory;
    private MineStorage mineStorage;
    private MineWorldManager mineWorldManager;
    private MineTypeManager mineTypeManager;
    private ConfigManager configManager;
    private SlimeUtils slimeUtils;
    private static Economy econ = null;
    private SQLite sqlite;
    private BukkitAudiences adventure;

    public static PrivateMines getPrivateMines() {
        return privateMines;
    }

    @Override
    public void onEnable() {
        Instant start = Instant.now();
        getLogger().info("Loading Private Mines v" + getDescription().getVersion());
        saveDefaultConfig();
        saveResource("menus.yml", false);
        privateMines = this;
        if (RedLib.MID_VERSION < 13) {
            Utils.complain();
        } else {
            mineFactory = new MineFactory();
            mineStorage = new MineStorage();
            mineWorldManager = new MineWorldManager();
            mineTypeManager = new MineTypeManager(this);
            if (Config.useAdventure) {
                getLogger().info("Loading adventure hook...");
                this.adventure = BukkitAudiences.create(this);
            }

            new CommandParser(getResource("commands.rdcml"))
                    .setArgTypes(
                            ArgType.of("materials", Material.class),
                            ArgType.of("mineType", mineTypeManager.getMineTypes()))
                    .parse()
                    .register("privatemines",
                              new PrivateMinesCommand());

            registerSellListener();
            setupSchematicUtils();
            Messages.load(this);

            try {
                Files.createDirectories(minesDirectory);
                Files.createDirectories(schematicsDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }

            configManager = ConfigManager.create(this).addConverter(Material.class, Material::valueOf, Material::toString).target(Config.class).load();
            //noinspection unused - This is the way the config manager is designed so stop complaining pls IntelliJ.
            ConfigManager mineConfig = ConfigManager.create(this)
                    .addConverter(Material.class, Material::valueOf, Material::toString)
                    .target(MineConfig.class)
                    .saveDefaults()
                    .load();
            //noinspection unused - This is the way the config manager is designed so stop complaining pls IntelliJ.
            ConfigManager menuConfig = ConfigManager.create(this, "menus.yml")
                    .addConverter(Material.class, Material::valueOf, Material::toString)
                    .target(MenuConfig.class)
                    .load();
            //noinspection unused - This is the way the config manager is designed so stop complaining pls IntelliJ.
            ConfigManager messagesConfig = ConfigManager.create(this, "messages.yml")
                    .target(MessagesConfig.class)
                    .saveDefaults()
                    .load();

            this.Y_LEVEL = Config.mineYLevel;
            this.MINE_DISTANCE = Config.mineDistance;

            MineConfig.getMineTypes().forEach((s, mineType) -> mineTypeManager.registerMineType(mineType));
            MineConfig.mineTypes.forEach((name, mineType) -> {
                File schematicFile = new File("plugins/PrivateMines/schematics/" + mineType.getFile());
                if (!schematicFile.exists()) {
                    getLogger().info("File doesn't exist!");
                    return;
                }
                SchematicIterator.MineBlocks mineBlocks = schematicIterator.findRelativePoints(schematicFile);
                schematicStorage.addSchematic(schematicFile, mineBlocks);
                privateMines.getLogger().info("Loaded file: " + schematicFile);
            });

            sqlite = new SQLite();
            sqlite.load();

            Connection connection = sqlite.getSQLConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT or IGNORE into privatemines (mineOwner, mineType, mineLocation," +
                                                                                          " corner1, corner2, fullRegionMin, fullRegionMax, spawn, tax, isOpen)" +
                                                                                          "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
                preparedStatement.setString(1, "test");
                preparedStatement.setString(2, "test");
                preparedStatement.setString(3, "test");
                preparedStatement.setString(4, "test");
                preparedStatement.setString(5, "test");
                preparedStatement.setString(6, "test");
                preparedStatement.setString(7, "test");
                preparedStatement.setString(8, "test");
                preparedStatement.setString(9, "test");
                preparedStatement.setString(10, "test");

//                preparedStatement.setString(2, UUID.randomUUID().toString());
//                preparedStatement.setString(3, UUID.randomUUID().toString());
//                preparedStatement.setString(4, UUID.randomUUID().toString());
//                preparedStatement.setString(5, UUID.randomUUID().toString());
//                preparedStatement.setString(6, UUID.randomUUID().toString());
//                preparedStatement.setString(7, UUID.randomUUID().toString());
//                preparedStatement.setString(8, UUID.randomUUID().toString());
//                preparedStatement.setDouble(9, 5.0);
//                preparedStatement.setBoolean(10, true);
                preparedStatement.executeUpdate();
                SQLHelper sqlHelper = new SQLHelper(connection);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

//            loadMenus();
            loadMines();
//            startAutoReset();
            PaperLib.suggestPaper(this);

            if (Bukkit.getPluginManager().isPluginEnabled("SlimeWorldManager")) {
                slimeUtils = new SlimeUtils();
                Task.asyncDelayed(() -> slimeUtils.setupSlimeWorld(UUID.randomUUID()));
            }

            getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
            if (!setupEconomy()) {
                privateMines.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
            Metrics metrics = new Metrics(this, PLUGIN_ID);
            metrics.addCustomChart(new SingleLineChart("mines", () -> mineStorage.getTotalMines()));
            Instant end = Instant.now();
            Duration loadTime = Duration.between(start, end);
            getLogger().info("Successfully loaded private mines in " + loadTime.toMillis() + "ms");
        }
    }

    @Override
    public void onDisable() {
        getLogger().info(String.format("Disabling adventure for %s", getDescription().getName()));
        if (this.adventure != null) {
            adventure.close();
            this.adventure = null;
        }
        getLogger().info(String.format("Disabled adventure for %s", getDescription().getName()));
        getLogger().info(String.format("%s v%s has successfully been Disabled",
                                       getDescription().getName(),
                                       getDescription().getVersion()));
    }

    public void setupSchematicUtils() {
        this.schematicStorage = new SchematicStorage();
        this.schematicIterator = new SchematicIterator(getSchematicStorage());
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    public void loadMines() {
        final PathMatcher jsonMatcher = FileSystems.getDefault().getPathMatcher("glob:**/*.yml"); // Credits to Brister Mitten
        Path path = getMinesDirectory();

        CompletableFuture.runAsync(() -> {
            try (Stream<Path> paths = Files.walk(path).filter(jsonMatcher::matches)) {
                paths.forEach(streamPath -> {
                    File file = streamPath.toFile();
                    Mine mine = new Mine(privateMines);
                    getLogger().info("Loading file " + file.getName() + "....");
                    YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

                    UUID owner = UUID.fromString(Objects.requireNonNull(yml.getString("mineOwner")));
                    String mineTypeName = yml.getString("mineType");
                    MineType mineType = mineTypeManager.getMineType(mineTypeName);
                    Location corner1 = LocationUtils.fromString(yml.getString("corner1"));
                    Location corner2 = LocationUtils.fromString(yml.getString("corner2"));
                    Location fullRegionMin = LocationUtils.fromString(yml.getString("fullRegionMin"));
                    Location fullRegionMax = LocationUtils.fromString(yml.getString("fullRegionMax"));
                    Location spawn = LocationUtils.fromString(yml.getString("spawn"));
                    Location mineLocation = LocationUtils.fromString(yml.getString("mineLocation"));
                    boolean isOpen = yml.getBoolean("isOpen");
                    double tax = yml.getDouble("tax");
                    List<String> bannedPlayersString = yml.getStringList("bannedPlayers");
                    List<UUID> bannedPlayers = new ArrayList<>();

                    MineData mineData = new MineDataBuilder()
                            .setOwner(owner)
                            .setMinimumMining(corner1)
                            .setMaximumMining(corner2)
                            .setMinimumFullRegion(fullRegionMin)
                            .setMaximumFullRegion(fullRegionMax)
                            .setSpawnLocation(spawn)
                            .setMineLocation(mineLocation)
                            .setMineType(mineType)
                            .setOpen(isOpen)
                            .setTax(tax)
                            .build();
                    mine.setMineData(mineData);
                    getMineStorage().addMine(owner, mine);
//                    mine.startResetTask();
//                    mine.startPercentageTask();

                    getLogger().info("Successfully loaded " + Bukkit.getOfflinePlayer(owner).getName() + "'s Mine!");
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @SuppressWarnings("unused")
    public void loadAddons() {
        final PathMatcher jarMatcher = FileSystems.getDefault().getPathMatcher("glob:**/*.jar"); // Credits to Brister Mitten
        Path path = getAddonsDirectory();

        CompletableFuture.runAsync(() -> {
            try (Stream<Path> paths = Files.walk(path).filter(jarMatcher::matches)) {
                paths.forEach(streamPath -> {
                    File file = streamPath.toFile();
                    getLogger().info("Loading addon file " + file.getName() + "....");
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void loadMenus() {
        getLogger().info("Loading Menus...");

        getLogger().info("" + MenuConfig.menus);
        MenuConfig.menus.forEach((s, menu) -> {
            getLogger().info("s: " + s);
            getLogger().info("menu: " + menu);
            getLogger().info("name: " + menu.getName());
            getLogger().info("rows: " + menu.getRows());
            getLogger().info("items: " + menu.getItems());

            InventoryGUI inventoryGUI = new InventoryGUI(Utils.getInventorySize(Utils.rowsToSlots(1)), menu.getName());

            getLogger().info("inventoryGUI: " + inventoryGUI);

            menu.getItems().forEach((s1, menuItem) -> {
                getLogger().info("s1: " + s1);
                getLogger().info("menu item name: " + menuItem.getItemName());
                getLogger().info("menu item slot: " + menuItem.getSlot());
                getLogger().info("menu item display name: " + menuItem.getName());
                getLogger().info("menu item lore " + menuItem.getLore());
                getLogger().info("menu item action: " + menuItem.getAction());
            });
        });
//        saveResource("menus.yml", false);
    }

    public void startAutoReset() {
        Map<UUID, Mine> mines = mineStorage.getMines();
        mines.forEach((uuid, mine) -> mine.startResetTask());
    }

    public SchematicStorage getSchematicStorage() {
        return schematicStorage;
    }

    public MineFactory getMineFactory() {
        return mineFactory;
    }

    public MineStorage getMineStorage() {
        return mineStorage;
    }

    public MineWorldManager getMineWorldManager() {
        return mineWorldManager;
    }

    public Path getMinesDirectory() {
        return minesDirectory;
    }

    public Path getAddonsDirectory() {
        return addonsDirectory;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MineTypeManager getMineTypeManager() {
        return mineTypeManager;
    }

    public SlimeUtils getSlimeUtils() {
        return slimeUtils;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public SQLite getSqlite() {
        return sqlite;
    }

    public void registerSellListener() {
        if (Bukkit.getPluginManager().isPluginEnabled("UltraPrisonCore")) {
            getLogger().info("Registering Ultra Prison Core as the sell listener...");
            getServer().getPluginManager().registerEvents(new UPCSellListener(), this);
            return;
        } else if (Bukkit.getPluginManager().isPluginEnabled("AutoSell")) {
            getLogger().info("Registering AutoSell as the sell listener...");
            getServer().getPluginManager().registerEvents(new AutoSellListener(), this);
            return;
        }
        getLogger().info("Using the internal sell system!");
    }

    public BukkitAudiences getAdventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }
        return adventure;
    }
}
