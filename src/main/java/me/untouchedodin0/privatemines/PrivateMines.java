package me.untouchedodin0.privatemines;

import co.aikar.commands.PaperCommandManager;
import com.convallyria.languagy.api.language.Translator;
import io.papermc.lib.PaperLib;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.commands.PrivateMinesCommand;
import me.untouchedodin0.privatemines.commands.UsePlayerShop;
import me.untouchedodin0.privatemines.config.Config;
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
import me.untouchedodin0.privatemines.utils.locale.LocaleManager;
import me.untouchedodin0.privatemines.utils.locale.LocaleObject;
import me.untouchedodin0.privatemines.utils.Utils;
import me.untouchedodin0.privatemines.utils.metrics.Metrics;
import me.untouchedodin0.privatemines.utils.metrics.Metrics.SingleLineChart;
import me.untouchedodin0.privatemines.utils.slime.SlimeUtils;
import me.untouchedodin0.privatemines.utils.world.MineWorldManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.RedLib;
import redempt.redlib.commandmanager.Messages;
import redempt.redlib.config.ConfigManager;
import redempt.redlib.misc.LocationUtils;
import redempt.redlib.misc.Task;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class PrivateMines extends JavaPlugin {

    private static PrivateMines privateMines;
    private static final int PLUGIN_ID = 11413;

    private final Path minesDirectory = getDataFolder().toPath().resolve("mines");
    private final Path schematicsDirectory = getDataFolder().toPath().resolve("schematics");
    private final Path addonsDirectory = getDataFolder().toPath().resolve("addons");
    private final Path localesDirectory = getDataFolder().toPath().resolve("locales");

    private final Map<String, Messages> locales = new HashMap<>();
    private final Map<UUID, LocaleObject> playerLocales = new HashMap<>();
    private SchematicStorage schematicStorage;
    private SchematicIterator schematicIterator;
    private MineFactory mineFactory;
    private MineStorage mineStorage;
    private MineWorldManager mineWorldManager;
    private MineTypeManager mineTypeManager;
    private ConfigManager configManager;
    private LocaleManager localeManager;
    private Translator translator;
    private SlimeUtils slimeUtils;
    private static Economy econ = null;
    private static PaperCommandManager paperCommandManager;

    public static PrivateMines getPrivateMines() {
        return privateMines;
    }

    @Override
    public void onEnable() {
        Instant start = Instant.now();
        getLogger().info("Loading Private Mines v" + getDescription().getVersion());
        saveDefaultConfig();
        privateMines = this;
        if (RedLib.MID_VERSION < 13) {
            Utils.complain();
        } else {
            mineFactory = new MineFactory();
            mineStorage = new MineStorage();
            mineWorldManager = new MineWorldManager();
            mineTypeManager = new MineTypeManager(this);
            localeManager = new LocaleManager();

            registerCommands();
            registerSellListener();
            setupSchematicUtils();
            saveLocaleFiles();

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

            SQLite sqlite = new SQLite();
            sqlite.load();

            loadMines();
            startAutoReset();
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
        getLogger().info("Shutting down translation service...");
        translator.close();
        getLogger().info("Translation service shut down.");
        getLogger().info(String.format("%s v%s has successfully been Disabled",
                                       getDescription().getName(),
                                       getDescription().getVersion()));
    }

    private void registerCommands() {
        paperCommandManager = new PaperCommandManager(this);
        paperCommandManager.registerCommand(new PrivateMinesCommand(this));
        paperCommandManager.registerCommand(new UsePlayerShop(this, mineStorage));
    }

    public void setupLanguages() {
        paperCommandManager.getLocales().addBundleClassLoader(getClassLoader());
        paperCommandManager.getLocales().loadLanguages();
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
                    mine.startResetTask();
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

    public void startAutoReset() {
        Map<UUID, Mine> mines = mineStorage.getMines();
//        Task.asyncDelayed(() -> mines.forEach((uuid, mine) -> mine.startResetTask()));
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

    public LocaleManager getLocaleManager() {
        return localeManager;
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

    public PaperCommandManager getPaperCommandManager() {
        return paperCommandManager;
    }

    public SlimeUtils getSlimeUtils() {
        return slimeUtils;
    }

    public static Economy getEconomy() {
        return econ;
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

    public void saveLocaleFiles() {
        getLogger().info("Saving locale files...");
        saveResource("locales/acf-privatemines_de.properties", false);
        saveResource("locales/acf-privatemines_en.properties", false);
        saveResource("locales/acf-privatemines_es.properties", false);
        saveResource("locales/acf-privatemines_fr.properties", false);
        getLogger().info("Successfully saved locale files!");
    }
    public void putPlayerInLocale(UUID uuid, LocaleObject localeObject) {
        playerLocales.put(uuid, localeObject);
    }

    public LocaleObject get(UUID uuid) {
        return playerLocales.get(uuid);
    }

    public Map<String, Messages> getLocales() {
        return locales;
    }
    public Map<UUID, LocaleObject> getPlayerLocales() {
        return playerLocales;
    }
}
