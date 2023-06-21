/**
 * MIT License
 * <p>
 * Copyright (c) 2021 - 2023 Kyle Hicks
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.untouchedodin0.privatemines;

import co.aikar.commands.BukkitCommandManager;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import me.untouchedodin0.kotlin.mine.data.MineData;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.kotlin.mine.storage.PregenStorage;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.commands.AddonsCommand;
import me.untouchedodin0.privatemines.commands.PrivateMinesCommand;
import me.untouchedodin0.privatemines.commands.PublicMinesCommand;
import me.untouchedodin0.privatemines.config.Config;
import me.untouchedodin0.privatemines.config.MenuConfig;
import me.untouchedodin0.privatemines.config.MessagesConfig;
import me.untouchedodin0.privatemines.config.MineConfig;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.iterator.SchematicIterator;
import me.untouchedodin0.privatemines.listener.MineResetListener;
import me.untouchedodin0.privatemines.listener.PlayerJoinListener;
import me.untouchedodin0.privatemines.listener.sell.AutoSellListener;
import me.untouchedodin0.privatemines.listener.sell.UPCSellListener;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.mine.MineTypeManager;
import me.untouchedodin0.privatemines.storage.SchematicStorage;
import me.untouchedodin0.privatemines.storage.StorageType;
import me.untouchedodin0.privatemines.storage.sql.SQLUtils;
import me.untouchedodin0.privatemines.storage.sql.SQLite;
import me.untouchedodin0.privatemines.utils.QueueUtils;
import me.untouchedodin0.privatemines.utils.UpdateChecker;
import me.untouchedodin0.privatemines.utils.adapter.LocationAdapter;
import me.untouchedodin0.privatemines.utils.adapter.PathAdapter;
import me.untouchedodin0.privatemines.utils.addon.Addon;
import me.untouchedodin0.privatemines.utils.addon.AddonManager;
import me.untouchedodin0.privatemines.utils.placeholderapi.PrivateMinesExpansion;
import me.untouchedodin0.privatemines.utils.world.MineWorldManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.milkbowl.vault.economy.Economy;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SingleLineChart;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;
import redempt.redlib.config.ConfigManager;
import redempt.redlib.misc.LocationUtils;
import redempt.redlib.misc.Task;
import redempt.redlib.sql.SQLCache;
import redempt.redlib.sql.SQLHelper;
import redempt.redlib.sql.SQLHelper.Results;

public class PrivateMines extends JavaPlugin {

  private static PrivateMines privateMines;
  private static final int PLUGIN_ID = 11413;
  public int Y_LEVEL = 50;
  public int MINE_DISTANCE = 150;
  private final Path schematicsDirectory = getDataFolder().toPath().resolve("schematics");
  private final Path addonsDirectory = getDataFolder().toPath().resolve("addons");
  private SchematicStorage schematicStorage;
  private SchematicIterator schematicIterator;
  private MineFactory mineFactory;
  private MineStorage mineStorage;
  private PregenStorage pregenStorage;
  private MineWorldManager mineWorldManager;
  private MineTypeManager mineTypeManager;
  private QueueUtils queueUtils;
  private static Economy econ = null;
  private SQLite sqlite;
  private SQLHelper sqlHelper;
  private Map<String, SQLCache> caches;
  private BukkitAudiences adventure;
  private AddonManager addonManager;

  public static PrivateMines getPrivateMines() {
    return privateMines;
  }

  @Override
  public void onEnable() {
    Instant start = Instant.now();
    getLogger().info("Loading Private Mines v" + getDescription().getVersion());
    saveDefaultConfig();
    saveResource("menus.yml", false);
    saveResource("messages.yml", false);

    privateMines = this;

    this.mineWorldManager = new MineWorldManager();
    this.mineFactory = new MineFactory();
    this.mineStorage = new MineStorage();
    this.pregenStorage = new PregenStorage();
    this.mineTypeManager = new MineTypeManager(this);
    this.queueUtils = new QueueUtils();
    this.addonManager = new AddonManager();

    if (Config.enableTax) {
      registerSellListener();
    }
    registerListeners();
    setupSchematicUtils();

    if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
      boolean registered = new PrivateMinesExpansion().register();
      if (registered) {
        privateMines.getLogger().info("Registered the PlaceholderAPI expansion!");
      }
    }

    if (Bukkit.getPluginManager().isPluginEnabled("Oraxen")) {
      String oraxenVersion = Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("Oraxen"))
          .getDescription().getVersion();

      getLogger().info(String.format("""
          Found Oraxen v%s installed,
          make sure to list the materials under the oraxen section
          within each mine type if you want to use oraxen
          blocks or it won't load correctly!""", oraxenVersion));
    }

    if (Bukkit.getPluginManager().isPluginEnabled("ItemsAdder")) {
      String itemsAdderVersion = Objects.requireNonNull(
          Bukkit.getPluginManager().getPlugin("ItemsAdder")).getDescription().getVersion();

      getLogger().info(String.format("""

          Found ItemsAdder v%s installed,
          make sure to list the materials under the itemsadder section
          within each mine type if you want to use itemsadder
          blocks or it won't load correctly!""", itemsAdderVersion));
    }

    try {
      Files.createDirectories(schematicsDirectory);
      Files.createDirectories(addonsDirectory);
    } catch (IOException e) {
      e.printStackTrace();
    }

    ConfigManager configManager = ConfigManager.create(this)
        .addConverter(Material.class, Material::valueOf, Material::toString)
        .addConverter(StorageType.class, StorageType::valueOf, StorageType::toString)
        .target(Config.class).saveDefaults().load();
    //noinspection unused - This is the way the config manager is designed so stop complaining pls IntelliJ.
    ConfigManager mineConfig = ConfigManager.create(this)
        .addConverter(Material.class, Material::valueOf, Material::toString)
        .target(MineConfig.class).saveDefaults().load();
    //noinspection unused - This is the way the config manager is designed so stop complaining pls IntelliJ.
    ConfigManager menuConfig = ConfigManager.create(this, "menus.yml")
        .addConverter(Material.class, Material::valueOf, Material::toString)
        .target(MenuConfig.class).load();
    //noinspection unused - This is the way the config manager is designed so stop complaining pls IntelliJ.
    ConfigManager messagesConfig = ConfigManager.create(this, "messages.yml")
        .target(MessagesConfig.class).saveDefaults().load();

    this.adventure = BukkitAudiences.create(this);
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
    });

    File dataFolder = new File(privateMines.getDataFolder(), "privatemines.db");
    if (!dataFolder.exists()) {
      try {
        boolean created = dataFolder.createNewFile();
        if (created) {
          return;
        }
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    this.sqlite = new SQLite();
    sqlite.load();
    this.sqlHelper = new SQLHelper(sqlite.getSQLConnection());

    sqlHelper.executeUpdate("""
        CREATE TABLE IF NOT EXISTS privatemines (
        owner VARCHAR(36) UNIQUE NOT NULL,
        mineType VARCHAR(10) NOT NULL,
        mineLocation VARCHAR(30) NOT NULL,
        corner1 VARCHAR(30) NOT NULL,
        corner2 VARCHAR(30) NOT NULL,
        fullRegionMin VARCHAR(30) NOT NULL,
        fullRegionMax VARCHAR(30) NOT NULL,
        spawn VARCHAR(30) NOT NULL,
        tax FLOAT NOT NULL,
        isOpen INT NOT NULL,
        maxPlayers INT NOT NULL,
        maxMineSize INT NOT NULL,
        materials VARCHAR(50) NOT NULL
        );""");
    sqlHelper.executeUpdate("""
        CREATE TABLE IF NOT EXISTS pregenmines (
        location VARCHAR(20),
        min_mining VARCHAR(20),
        max_mining VARCHAR(20),
        spawn VARCHAR(20),
        min_full VARCHAR(20),
        max_full VARCHAR(20)
        );
        """);
    sqlHelper.setAutoCommit(true);

    this.caches = new HashMap<>();

    String databaseName = "privatemines";
    List<String> cacheNames = List.of("owner", "mineType", "mineLocation", "corner1", "corner2");

    cacheNames.forEach(string -> {
      SQLCache sqlCache = sqlHelper.createCache(databaseName, string);
      caches.put(string, sqlCache);
    });

    Task.asyncDelayed(this::loadSQLMines);
    Task.asyncDelayed(SQLUtils::loadPregens);
    Task.asyncDelayed(this::loadAddons);

    BukkitCommandManager bukkitCommandManager = new BukkitCommandManager(this);
    bukkitCommandManager.registerCommand(new PrivateMinesCommand());
    bukkitCommandManager.registerCommand(new PublicMinesCommand());
    bukkitCommandManager.registerCommand(new AddonsCommand());
    bukkitCommandManager.enableUnstableAPI("help");
    bukkitCommandManager.getCommandCompletions().registerCompletion("addons", context -> {
      ArrayList<String> addons = new ArrayList<>();
      AddonManager.getAddons().forEach((s, addon) -> addons.add(s));
      return addons;
    });

    getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
    if (!setupEconomy()) {
      privateMines.getLogger().severe(
          String.format("[%s] - Disabled due to no Vault dependency found!",
              getDescription().getName()));
      getServer().getPluginManager().disablePlugin(this);
      return;
    }
    Metrics metrics = new Metrics(this, PLUGIN_ID);
    metrics.addCustomChart(new SingleLineChart("mines", () -> mineStorage.getTotalMines()));

    new UpdateChecker(this).fetch();

    Instant end = Instant.now();
    Duration loadTime = Duration.between(start, end);
    getLogger().info("Successfully loaded private mines in " + loadTime.toMillis() + "ms");
  }


  @Override
  public void onDisable() {
    if (adventure != null) {
      getLogger().info(String.format("Disabling adventure for %s", getDescription().getName()));
      adventure.close();
      this.adventure = null;
      getLogger().info(String.format("Disabled adventure for %s", getDescription().getName()));
    }

    getLogger().info(
        String.format("%s v%s has successfully been Disabled", getDescription().getName(),
            getDescription().getVersion()));
    saveMines();
//    savePregenMines();
    sqlHelper.close();
  }

  public void setupSchematicUtils() {
    this.schematicStorage = new SchematicStorage();
    this.schematicIterator = new SchematicIterator(getSchematicStorage());
  }

  private boolean setupEconomy() {
    if (getServer().getPluginManager().getPlugin("Vault") == null) {
      return false;
    }
    RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager()
        .getRegistration(Economy.class);
    if (rsp == null) {
      return false;
    }
    econ = rsp.getProvider();
    return true;
  }

  public void loadSQLMines() {
    SQLHelper sqlHelper = getSqlHelper();
    Results results = sqlHelper.queryResults("SELECT * FROM privatemines;");

    results.forEach(result -> {
      String owner = result.getString(1);
      String mineType = result.getString(2);
      String mineLocation = result.getString(3);
      String corner1 = result.getString(4);
      String corner2 = result.getString(5);
      String fullRegionMin = result.getString(6);
      String fullRegionMax = result.getString(7);
      String spawn = result.getString(8);
      double tax = result.get(9);
      int isOpen = result.get(10);
      String resultsMaterial = result.getString(13);
      resultsMaterial = resultsMaterial.substring(1); // remove starting '{'

      Map<Material, Double> materials = new HashMap<>();

      String[] pairs = resultsMaterial.split("\\s*,\\s*");

      for (String string : pairs) {
        String[] parts = string.split("=");
        String matString = parts[0];
        double percent = Double.parseDouble(parts[1].substring(0, parts[1].length() - 1));
        Material material = Material.valueOf(matString);
        materials.put(material, percent);
      }

      Mine mine = new Mine(this);
      UUID uuid = UUID.fromString(owner);
      MineType type = mineTypeManager.getMineType(mineType);
      Location minMining = LocationUtils.fromString(corner1);
      Location maxMining = LocationUtils.fromString(corner2);
      Location fullMin = LocationUtils.fromString(fullRegionMin);
      Location fullMax = LocationUtils.fromString(fullRegionMax);
      Location location = LocationUtils.fromString(mineLocation);
      Location spawnLocation = LocationUtils.fromString(spawn);
      boolean open = isOpen != 0;

      MineData mineData = new MineData(uuid, minMining, maxMining, fullMin, fullMax, location,
          spawnLocation, type, open, tax);
      mineData.setMaterials(materials);
      mine.setMineData(mineData);
      mineStorage.addMine(uuid, mine);
    });
  }

  public void loadAddons() {
    final PathMatcher jarMatcher = FileSystems.getDefault()
        .getPathMatcher("glob:**/*.jar"); // Credits to Brister Mitten

    try (Stream<Path> paths = Files.walk(addonsDirectory).filter(jarMatcher::matches)) {
      paths.forEach(jar -> {
        File file = jar.toFile();
        CompletableFuture<@Nullable Class<? extends Addon>> addon = AddonManager.findExpansionInFile(
            file);
        Optional<Addon> loaded = addonManager.register(addon);

        if (loaded.isPresent()) {
          Addon addonLoaded = loaded.get();
          addonLoaded.onEnable();
        }
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void saveMines() {
    getMineStorage().getMines().forEach((uuid, mine) -> {
      Player player = Bukkit.getOfflinePlayer(uuid).getPlayer();
      if (player != null) {
        SQLUtils.update(mine);
        mine.stopTasks();
      }
    });
  }

//  public void savePregenMines() {
//    getPregenStorage().getMines().forEach(PregenMine::save);
//  }

  public SchematicStorage getSchematicStorage() {
    return schematicStorage;
  }

  public MineFactory getMineFactory() {
    return mineFactory;
  }

  public MineStorage getMineStorage() {
    return mineStorage;
  }

  public PregenStorage getPregenStorage() {
    return pregenStorage;
  }

  public MineWorldManager getMineWorldManager() {
    return mineWorldManager;
  }

  public MineTypeManager getMineTypeManager() {
    return mineTypeManager;
  }

  public AddonManager getAddonManager() {
    return addonManager;
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

  private void registerListeners() {
    getServer().getPluginManager().registerEvents(new MineResetListener(), this);
  }

  public SQLite getSqlite() {
    return sqlite;
  }

  public SQLHelper getSqlHelper() {
    return sqlHelper;
  }

  public Map<String, SQLCache> getCaches() {
    return caches;
  }

  public BukkitAudiences getAdventure() {
    if (this.adventure == null) {
      throw new IllegalStateException("Adventure was not initialized!");
    }
    return adventure;
  }

  public QueueUtils getQueueUtils() {
    return queueUtils;
  }
}
