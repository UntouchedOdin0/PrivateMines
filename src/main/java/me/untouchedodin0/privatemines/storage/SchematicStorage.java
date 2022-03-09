package me.untouchedodin0.privatemines.storage;

import me.untouchedodin0.privatemines.storage.points.SchematicPoints;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class SchematicStorage {

    public Map<File, SchematicPoints> schematicPointsMap = new HashMap<>();

    public void addSchematic(File file, SchematicPoints schematicPoints) {
        if (schematicPointsMap.containsKey(file)) {
            Bukkit.getLogger().info(String.format("File %s was already stored in the map!", file.getName()));
        }
    }
}
