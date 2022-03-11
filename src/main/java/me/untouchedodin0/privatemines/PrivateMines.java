package me.untouchedodin0.privatemines;

import co.aikar.commands.PaperCommandManager;
import me.untouchedodin0.privatemines.commands.PrivateMinesCommand;
import me.untouchedodin0.privatemines.config.MineConfig;
import me.untouchedodin0.privatemines.configmanager.ConfigManager;
import me.untouchedodin0.privatemines.iterator.SchematicIterator;
import me.untouchedodin0.privatemines.storage.SchematicStorage;
import me.untouchedodin0.privatemines.utils.version.VersionUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

public class PrivateMines extends JavaPlugin {

    private static PaperCommandManager paperCommandManager;
    private static PrivateMines privateMines;
    private final Path minesDirectory = getDataFolder().toPath().resolve("mines");
    private final Path schematicsDirectory = getDataFolder().toPath().resolve("schematics");
    private SchematicStorage schematicStorage;
    private SchematicIterator schematicIterator;

    public static PrivateMines getPrivateMines() {
        return privateMines;
    }

    public static PaperCommandManager getPaperCommandManager() {
        return paperCommandManager;
    }

    /**
     * Gets the plugin that called the calling method of this method
     * <p>
     * Credits to Redempt
     *
     * @return The plugin which called the method
     */

    public static Plugin getCallingPlugin() {
        Exception ex = new Exception();
        try {
            Class<?> clazz = Class.forName(ex.getStackTrace()[2].getClassName());
            Plugin plugin = JavaPlugin.getProvidingPlugin(clazz);
            return plugin.isEnabled() ? plugin : Bukkit.getPluginManager().getPlugin(plugin.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onEnable() {
        Instant start = Instant.now();
        getLogger().info("Loading Private Mines v" + getDescription().getVersion());
        privateMines = this;
        saveDefaultConfig();
        registerCommands();
        setupSchematicUtils();
        try {
            Files.createDirectories(minesDirectory);
            Files.createDirectories(schematicsDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }

        getLogger().info("Schematic Storage: " + getSchematicStorage());
        getLogger().info("Schematic Iterator: " + getSchematicIterator());
        getLogger().info("Mid Version: " + VersionUtils.MID_VERSION);
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
            Bukkit.getLogger().info("corners: " + Arrays.toString(mineBlocks.getCorners()));
        });

        Instant end = Instant.now();
        getLogger().info("mineConfig: " + mineConfig);
        Duration loadTime = Duration.between(start, end);
        getLogger().info("time to load: " + loadTime.toMillis() + "ms");
    }

    private void registerCommands() {
        paperCommandManager = new PaperCommandManager(this);
        paperCommandManager.registerCommand(new PrivateMinesCommand());
    }

    public void setupSchematicUtils() {
        this.schematicStorage = new SchematicStorage();
        this.schematicIterator = new SchematicIterator(getSchematicStorage());
    }

    public SchematicStorage getSchematicStorage() {
        return schematicStorage;
    }

    public SchematicIterator getSchematicIterator() {
        return schematicIterator;
    }
}
