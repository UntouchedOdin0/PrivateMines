package me.untouchedodin0.privatemines;

import co.aikar.commands.PaperCommandManager;
import io.papermc.lib.PaperLib;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.commands.PrivateMinesCommand;
import me.untouchedodin0.privatemines.config.Config;
import me.untouchedodin0.privatemines.config.MineConfig;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.iterator.SchematicIterator;
import me.untouchedodin0.privatemines.listener.PlayerJoinListener;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.mine.MineTypeManager;
import me.untouchedodin0.privatemines.mine.data.MineData;
import me.untouchedodin0.privatemines.mine.data.MineDataBuilder;
import me.untouchedodin0.privatemines.storage.SchematicStorage;
import me.untouchedodin0.privatemines.storage.sql.Database;
import me.untouchedodin0.privatemines.storage.sql.SQLite;
import me.untouchedodin0.privatemines.utils.Utils;
import me.untouchedodin0.privatemines.utils.slime.SlimeUtils;
import me.untouchedodin0.privatemines.utils.world.MineWorldManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.RedLib;
import redempt.redlib.config.ConfigManager;
import redempt.redlib.misc.LocationUtils;
import redempt.redlib.misc.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static java.nio.file.Files.list;

public class PrivateMines extends JavaPlugin {

    private static PrivateMines privateMines;
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
    private Database database;
    private SlimeUtils slimeUtils;
    private static Economy econ = null;

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

            Utils utils = new Utils(this);

            registerCommands();
            setupSchematicUtils();

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

            MineConfig.getMineTypes().forEach((s, mineType) -> {
                mineTypeManager.registerMineType(mineType);
                privateMines.getLogger().info("upgrade currency: " + mineType.getUpgradeCurrency());
            });

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

            this.database = new SQLite();
            database.load();

//            Connection connection = SQLHelper.openSQLite(getDataFolder().toPath().resolve("database.sql"));
//            sqlHelper = new SQLHelper(connection);
//            sqlHelper.execute("CREATE TABLE IF NOT EXISTS privatemines (mineOwner UUID, mineLocation STRING, corner1 STRING, corner2 STRING, spawn STRING, UNIQUE (mineOwner, mineLocation, corner1, corner2, spawn) ON CONFLICT IGNORE);");

            loadMines();
            startAutoReset();
            PaperLib.suggestPaper(this);

            if (Bukkit.getPluginManager().isPluginEnabled("SlimeWorldManager")) {
                slimeUtils = new SlimeUtils();
                Task task = Task.asyncDelayed(() -> {
                    slimeUtils.setupSlimeWorld(UUID.randomUUID());
                });
            }

            getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
            if (!setupEconomy()) {
                privateMines.getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
            Instant end = Instant.now();
            Duration loadTime = Duration.between(start, end);
            getLogger().info("Successfully loaded private mines in " + loadTime.toMillis() + "ms");
        }
    }

    private void registerCommands() {
        PaperCommandManager paperCommandManager = new PaperCommandManager(this);
        paperCommandManager.registerCommand(new PrivateMinesCommand(this));
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
            try {
                list(path)
                        .filter(jsonMatcher::matches)
                        .forEach(filePath -> {
                            File file = filePath.toFile();
                            Mine mine = new Mine(privateMines);
//                            MineData mineData = new MineData();
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

                            MineData mineData = new MineDataBuilder()
                                    .setOwner(owner)
                                    .setMinimumMining(corner1)
                                    .setMaximumMining(corner2)
                                    .setMinimumFullRegion(fullRegionMin)
                                    .setMaximumFullRegion(fullRegionMax)
                                    .setSpawnLocation(spawn)
                                    .setMineLocation(mineLocation)
                                    .setMineType(mineType)
                                    .build();
                            mine.setMineData(mineData);
                            getMineStorage().addMine(owner, mine);
                            getLogger().info("Successfully loaded " + Bukkit.getOfflinePlayer(owner).getName() + "'s Mine!");
                        });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void startAutoReset() {
        Map<UUID, Mine> mines = mineStorage.getMines();
        mines.forEach((uuid, mine) -> mine.startResetTask());
    }

    public SchematicStorage getSchematicStorage() {
        return schematicStorage;
    }

    public SchematicIterator getSchematicIterator() {
        return schematicIterator;
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
}
