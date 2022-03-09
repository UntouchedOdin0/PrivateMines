package me.untouchedodin0.privatemines;

import co.aikar.commands.PaperCommandManager;
import me.untouchedodin0.privatemines.commands.PrivateMinesCommand;
import me.untouchedodin0.privatemines.iterator.SchematicIterator;
import me.untouchedodin0.privatemines.storage.SchematicStorage;
import me.untouchedodin0.privatemines.utils.version.VersionUtils;
import org.bukkit.plugin.java.JavaPlugin;

public class PrivateMines extends JavaPlugin {

    private static PaperCommandManager paperCommandManager;
    private SchematicStorage schematicStorage;
    private SchematicIterator schematicIterator;
    private static PrivateMines privateMines;

    @Override
    public void onEnable() {
        getLogger().info("Loading Private Mines v" + getDescription().getVersion());
        privateMines = this;
        saveDefaultConfig();
        registerCommands();
        setupSchematicUtils();
        getLogger().info("Schematic Storage: " + getSchematicStorage());
        getLogger().info("Schematic Iterator: " + getSchematicIterator());
        getLogger().info("Mid Version: " + VersionUtils.getMidVersion());
    }

    private void registerCommands() {
        paperCommandManager = new PaperCommandManager(this);
        paperCommandManager.registerCommand(new PrivateMinesCommand());
    }

    public void setupSchematicUtils() {
        this.schematicStorage = new SchematicStorage();
        this.schematicIterator = new SchematicIterator(getSchematicStorage());
    }

    public static PrivateMines getPrivateMines() {
        return privateMines;
    }

    public static PaperCommandManager getPaperCommandManager() {
        return paperCommandManager;
    }

    public SchematicStorage getSchematicStorage() {
        return schematicStorage;
    }

    public SchematicIterator getSchematicIterator() {
        return schematicIterator;
    }
}
