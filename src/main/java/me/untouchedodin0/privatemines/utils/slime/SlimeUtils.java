package me.untouchedodin0.privatemines.utils.slime;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.CorruptedWorldException;
import com.grinderwolf.swm.api.exceptions.NewerFormatException;
import com.grinderwolf.swm.api.exceptions.UnknownWorldException;
import com.grinderwolf.swm.api.exceptions.WorldInUseException;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import me.untouchedodin0.privatemines.PrivateMines;
import org.bukkit.Bukkit;
import redempt.redlib.misc.Task;

import java.io.IOException;
import java.util.UUID;

public class SlimeUtils {

    private PrivateMines privateMines = PrivateMines.getPrivateMines();
    private SlimePlugin slime;

    public void setupSlimeWorld(UUID uuid) {
        privateMines.getLogger().info("Attempting to setup a mine world...");
        slime = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        privateMines.getLogger().info("slime: " + slime);

//        slimePropertyMap = new SlimePropertyMap();

//        SlimeProperty<Integer> corner1X = new SlimePropertyInt("corner1X", 0);
//        SlimeProperty<Integer> corner1Y = new SlimePropertyInt("corner1Y", 0);
//        SlimeProperty<Integer> corner1Z = new SlimePropertyInt("corner1Z", 0);
//        SlimeProperty<Integer> corner2X = new SlimePropertyInt("corner2X", 0);
//        SlimeProperty<Integer> corner2Y = new SlimePropertyInt("corner2Y", 0);
//        SlimeProperty<Integer> corner2Z = new SlimePropertyInt("corner2Z", 0);

//        SlimeProperty<Integer> spawnX = new SlimePropertyInt("spawnX", 1);
//        SlimeProperty<Integer> spawnY = new SlimePropertyInt("spawnY", 1);
//        SlimeProperty<Integer> spawnZ = new SlimePropertyInt("spawnZ", 1);
//
//        slimePropertyMap.setValue(spawnX, 2);
//        slimePropertyMap.setValue(spawnY, 3);
//        slimePropertyMap.setValue(spawnZ, 4);

        if (slime != null) {
            SlimeLoader slimeLoader = slime.getLoader("file");
            privateMines.getLogger().info("slimeLoader: " + slimeLoader);
            try {
                Task.asyncDelayed(() -> {
                    SlimePropertyMap slimePropertyMap = new SlimePropertyMap();
                    slimePropertyMap.setValue(SlimeProperties.WORLD_TYPE, "flat");
                    slimePropertyMap.setValue(SlimeProperties.SPAWN_X, 1);
                    slimePropertyMap.setValue(SlimeProperties.SPAWN_Y, 1);
                    slimePropertyMap.setValue(SlimeProperties.SPAWN_Z, 1);
                    privateMines.getLogger().info("slimePropertyMap: " + slimePropertyMap);
                    try {
                        SlimeWorld slimeWorld = slime.loadWorld(slimeLoader, uuid.toString(), true, slimePropertyMap);
                        privateMines.getLogger().info("slimeWorld: " + slimeWorld);
                    } catch (UnknownWorldException | IOException | CorruptedWorldException | NewerFormatException | WorldInUseException ex) {
                        ex.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
