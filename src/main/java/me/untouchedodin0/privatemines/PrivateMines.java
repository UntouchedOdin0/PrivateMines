package me.untouchedodin0.privatemines;

import co.aikar.commands.PaperCommandManager;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.commands.PrivateMinesCommand;
import me.untouchedodin0.privatemines.config.MineConfig;
import me.untouchedodin0.privatemines.configmanager.ConfigManager;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.iterator.SchematicIterator;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.mine.data.MineData;
import me.untouchedodin0.privatemines.storage.SchematicStorage;
import me.untouchedodin0.privatemines.utils.version.VersionUtils;
import me.untouchedodin0.privatemines.utils.world.MineWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import redempt.redlib.misc.LocationUtils;
import redempt.redlib.sql.SQLHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class PrivateMines extends JavaPlugin {

    private static PrivateMines privateMines;
    private final Path minesDirectory = getDataFolder().toPath().resolve("mines");
    private final Path schematicsDirectory = getDataFolder().toPath().resolve("schematics");
    private SchematicStorage schematicStorage;
    private SchematicIterator schematicIterator;
    private MineFactory mineFactory;
    private MineStorage mineStorage;
    private MineWorldManager mineWorldManager;
    private Connection connection;
    private SQLHelper sqlHelper;

    public static PrivateMines getPrivateMines() {
        return privateMines;
    }

    @Override
    public void onEnable() {
        Instant start = Instant.now();
        getLogger().info("Loading Private Mines v" + getDescription().getVersion());
        saveDefaultConfig();
        privateMines = this;
        mineFactory = new MineFactory();
        mineStorage = new MineStorage();
        mineWorldManager = new MineWorldManager();

        registerCommands();
        setupSchematicUtils();
        try {
            Files.createDirectories(minesDirectory);
            Files.createDirectories(schematicsDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ConfigManager mineConfig = ConfigManager.create(this)
                .addConverter(Material.class, Material::valueOf, Material::toString)
                .target(MineConfig.class)
                .saveDefaults()
                .load();
        MineConfig.mineTypes.forEach((name, mineType) -> {
            File schematicFile = new File("plugins/PrivateMines/schematics/" + mineType.getFile());
            if (!schematicFile.exists()) {
                getLogger().info("File doesn't exist!");
                return;
            }
            getLogger().info("MineType name: " + name);
            getLogger().info("MineType mineType: " + mineType);
            getLogger().info("MineType file: " + mineType.getFile());
            getLogger().info("MineType schematicFile: " + schematicFile);
            getLogger().info("MineType time: " + mineType.getResetTime());
            getLogger().info("MineType reset percentage: " + mineType.getResetPercentage());
            getLogger().info("Schematic iterator: " + schematicIterator);

            SchematicIterator.MineBlocks mineBlocks = schematicIterator.findRelativePoints(schematicFile);

            getLogger().info("spawn: " + mineBlocks.getSpawnLocation());
            getLogger().info("corners: " + Arrays.toString(mineBlocks.getCorners()));
            schematicStorage.addSchematic(schematicFile, mineBlocks);
        });

        connection = SQLHelper.openSQLite(getDataFolder().toPath().resolve("database.sql"));
        sqlHelper = new SQLHelper(connection);

        privateMines.getLogger().info("Connection: " + connection);
        privateMines.getLogger().info("sqlHelper: " + sqlHelper);
        String sqlCommand = "INSERT INTO privatemines(mineOwner, mineLocation, corner1, corner2, spawn) " +
                "VALUES('%uuid%', '%minelocation%', '%corner1%', '%corner2%', '%spawn%');";

        String replacedCommand;
        replacedCommand = sqlCommand
                .replace("%uuid%", "i-am-the-player-uuid")
                .replace("%minelocation%", "i-am-the-mine-location")
                .replace("%corner1%", "i-am-the-corner-1")
                .replace("%corner2%", "i-am-the-corner2")
                .replace("%spawn%", "i-am-the-spawn");

        sqlHelper.execute("CREATE TABLE IF NOT EXISTS privatemines (mineOwner UUID, mineLocation STRING, corner1 STRING, corner2 STRING, spawn STRING);");
        sqlHelper.executeUpdate(replacedCommand);

//        sqlHelper.execute("UPDATE privatemines SET mineOwner=? WHERE mineLocation=?;", UUID.randomUUID(), location);

        Instant end = Instant.now();
        Duration loadTime = Duration.between(start, end);
        getLogger().info("time to load: " + loadTime.toMillis() + "ms");
        loadMines();

        if (Bukkit.getPluginManager().isPluginEnabled("SlimeWorldManager"))
            setupSlimeWorld();
    }

    private void registerCommands() {
        PaperCommandManager paperCommandManager = new PaperCommandManager(this);
        paperCommandManager.registerCommand(new PrivateMinesCommand(this));
    }

    public void setupSchematicUtils() {
        this.schematicStorage = new SchematicStorage();
        this.schematicIterator = new SchematicIterator(getSchematicStorage());
    }

    public void loadMines() {
        final PathMatcher jsonMatcher = FileSystems.getDefault().getPathMatcher("glob:**/*.yml"); // Credits to Brister Mitten
        Path path = getMinesDirectory();

        CompletableFuture.runAsync(() -> {
            Thread thread = Thread.currentThread();
            privateMines.getLogger().info("Loading mines on thread #" + thread.getId());
            try {
                Files.list(path).filter(jsonMatcher::matches).forEach(filePath -> {
                    File file = filePath.toFile();
                    Mine mine = new Mine(privateMines);
                    MineData mineData = new MineData();

                    privateMines.getLogger().info("Lets go load file " + file);
                    YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

                    UUID owner = UUID.fromString(Objects.requireNonNull(yml.getString("mineOwner")));
                    String mineTypeName = yml.getString("mineType");
                    Location corner1 = LocationUtils.fromString(yml.getString("corner1"));
                    Location corner2 = LocationUtils.fromString(yml.getString("corner2"));
                    Location spawn = LocationUtils.fromString(yml.getString("spawn"));
                    Location mineLocation = LocationUtils.fromString(yml.getString("mineLocation"));
                    MineType mineType = MineConfig.mineTypes.get(mineTypeName);

                    privateMines.getLogger().info("owner: " + owner);
                    privateMines.getLogger().info("mineTypeName: " + mineTypeName);
                    privateMines.getLogger().info("corner1: " + corner1);
                    privateMines.getLogger().info("corner2: " + corner2);
                    privateMines.getLogger().info("spawn: " + spawn);
                    privateMines.getLogger().info("mineLocation: " + mineLocation);
                    privateMines.getLogger().info("mineType: " + mineType);

                    mineData.setMinimumMining(corner1);
                    mineData.setMaximumMining(corner2);
                    mineData.setSpawnLocation(spawn);
                    mineData.setMineLocation(mineLocation);
                    mineData.setMineType(mineTypeName);

                    mine.setMineData(mineData);
                    privateMines.getLogger().info("mines: " + getMineStorage().getMines());
                    getMineStorage().addMine(owner, mine);
                    privateMines.getLogger().info("mines: " + getMineStorage().getMines());
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
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

    public void setupSlimeWorld() {
        privateMines.getLogger().info("Setting up the slime world...");
        // Create a new empty property map
        SlimePropertyMap slimePropertyMap = new SlimePropertyMap();
        privateMines.getLogger().info("slimePropertyMap: " + slimePropertyMap);
    }
}
