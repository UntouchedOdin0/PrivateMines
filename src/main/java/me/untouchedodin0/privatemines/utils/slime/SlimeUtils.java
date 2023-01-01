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

package me.untouchedodin0.privatemines.utils.slime;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import me.untouchedodin0.privatemines.PrivateMines;
import org.bukkit.Bukkit;
import redempt.redlib.misc.Task;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class SlimeUtils {

    private final PrivateMines privateMines = PrivateMines.getPrivateMines();
    private SlimePlugin slimePlugin;
    private Map<UUID, SlimePropertyMap> slimeMap = new HashMap<>();

    /**
     * A no-operation consumer: takes an object and does absolutely nothing with it!
     */
    private static final Consumer<Object> NOOP = x -> {};

    /**
     * Creates a slime world.
     * @param worldName the name of the new world
     * @param loaderName the name of the loader to use
     * @param readOnly whether the world should be read-only or not
     * @param propertyMap a map of properties to configure the world
     * @throws IllegalStateException if the world was not created
     */
    public void createSlimeWorld(String worldName, String loaderName, boolean readOnly, SlimePropertyMap propertyMap) {
        slimePlugin.asyncCreateEmptyWorld(slimePlugin.getLoader(loaderName), worldName, readOnly, propertyMap)
                .thenAccept(optSlimeWorld -> optSlimeWorld.ifPresentOrElse(NOOP, () -> {
                    throw new IllegalStateException("Failed to create slime world: " + worldName);
                }));
    }

    /**
     * Creates a slime world, and registers a consumer for the created {@link SlimeWorld} object.
     * @param worldName the name of the new world
     * @param loaderName the name of the loader to use
     * @param readOnly whether the world should be read-only or not
     * @param propertyMap a map of properties to configure the world
     * @param slimeWorldConsumer the consumer to pass the created world to
     * @throws IllegalStateException if the world was not created
     */
    public void createSlimeWorld(String worldName, String loaderName, boolean readOnly, SlimePropertyMap propertyMap, Consumer<SlimeWorld> slimeWorldConsumer) {
        slimePlugin.asyncCreateEmptyWorld(slimePlugin.getLoader(loaderName), worldName, readOnly, propertyMap)
                .thenAccept(optSlimeWorld -> optSlimeWorld.ifPresentOrElse(slimeWorldConsumer, () -> {
                    throw new IllegalStateException("Failed to create slime world: " + worldName);
                }));
    }

    /**
     * Generates the given {@link SlimeWorld} on the main server thread.
     * @param slimeWorld the slime world to generate
     */
    public void generateSlimeWorld(SlimeWorld slimeWorld) {
        Bukkit.getScheduler().runTask(privateMines, () -> slimePlugin.generateWorld(slimeWorld));
    }

    /**
     * Loads a slime world
     * @param worldName the name of the world to load
     * @param loaderName the name of the loader to use
     * @param readOnly whether the world should be read-only or not
     * @param propertyMap a map of properties to configure the world
     * @throws IllegalStateException if the world was not loaded successfully
     */
    public void loadSlimeWorld(String worldName, String loaderName, boolean readOnly, SlimePropertyMap propertyMap) {
        slimePlugin.asyncLoadWorld(slimePlugin.getLoader(loaderName), worldName, readOnly, propertyMap)
                .thenAccept(optSlimeWorld -> optSlimeWorld.ifPresentOrElse(NOOP, () -> {
                    throw new IllegalStateException("Failed to load slime world: " + worldName);
                }));
    }

    /**
     * Loads a slime world, and registers a consumer for the loaded {@link SlimeWorld} object.
     * @param worldName the name of the world to load
     * @param loaderName the name of the loader to use
     * @param readOnly whether the world should be read-only or not
     * @param propertyMap a map of properties to configure the world
     * @param slimeWorldConsumer the consumer to pass the loaded world to
     * @throws IllegalStateException if the world was not loaded successfully
     */
    public void loadSlimeWorld(String worldName, String loaderName, boolean readOnly, SlimePropertyMap propertyMap, Consumer<SlimeWorld> slimeWorldConsumer) {
        slimePlugin.asyncLoadWorld(slimePlugin.getLoader(loaderName), worldName, readOnly, propertyMap)
                .thenAccept(optSlimeWorld -> optSlimeWorld.ifPresentOrElse(slimeWorldConsumer, () -> {
                    throw new IllegalStateException("Failed to load slime world: " + worldName);
                }));
    }

    /**
     * Wrapper method to create a new slime world and generate it.
     * @param worldName the name of the world to create
     * @param loaderName the name of the loader to use
     * @param readOnly whether the world should be read-only or not
     * @param propertyMap a map of properties to configure the world
     * @throws IllegalStateException if the world is not created successfully
     */
    public void createAndGenerateSlimeWorld(String worldName, String loaderName, boolean readOnly, SlimePropertyMap propertyMap) {
        createSlimeWorld(worldName, loaderName, readOnly, propertyMap, this::generateSlimeWorld);
    }

    /**
     * Wrapper method to load an existing slime world and generate it.
     * @param worldName the name of the world to load
     * @param loaderName the name of the loader to use
     * @param readOnly whether the world should be read-only or not
     * @param propertyMap a map of properties to configure the world
     * @throws IllegalStateException if the world is not loaded successfully
     */
    public void loadAndGenerateSlimeWorld(String worldName, String loaderName, boolean readOnly, SlimePropertyMap propertyMap) {
        loadSlimeWorld(worldName, loaderName, readOnly, propertyMap, this::generateSlimeWorld);
    }

    public void setupSlimeWorld(UUID uuid) {
        privateMines.getLogger().info("Attempting to setup a mine world...");
        slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
        privateMines.getLogger().info("slime: " + slimePlugin);

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

        if (slimePlugin != null) {
            //SlimeLoader slimeLoader = slimePlugin.getLoader("file");
            //privateMines.getLogger().info("slimeLoader: " + slimeLoader);
            try {
                Task.asyncDelayed(() -> {
                    SlimePropertyMap slimePropertyMap = new SlimePropertyMap();
                    slimePropertyMap.setValue(SlimeProperties.WORLD_TYPE, "flat");
                    slimePropertyMap.setValue(SlimeProperties.SPAWN_X, 1);
                    slimePropertyMap.setValue(SlimeProperties.SPAWN_Y, 1);
                    slimePropertyMap.setValue(SlimeProperties.SPAWN_Z, 1);
                    slimeMap.putIfAbsent(uuid, slimePropertyMap);
                    privateMines.getLogger().info("slimePropertyMap: " + slimePropertyMap);
                    // Create an empty slime world, using the UUID as the world name
                    createAndGenerateSlimeWorld(uuid.toString(), "file", true, slimePropertyMap);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Map<UUID, SlimePropertyMap> getSlimeMap() {
        return slimeMap;
    }
}
