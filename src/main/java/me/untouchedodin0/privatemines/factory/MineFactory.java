package me.untouchedodin0.privatemines.factory;

import com.sk89q.worldedit.*;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.math.Vector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.iterator.SchematicIterator;
import me.untouchedodin0.privatemines.storage.SchematicStorage;
import me.untouchedodin0.privatemines.type.MineType;
import me.untouchedodin0.privatemines.utils.task.Task;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.Objects;

public class MineFactory {

    PrivateMines privateMines = PrivateMines.getPrivateMines();
    SchematicStorage schematicStorage;

    BlockVector3 spawn;
    BlockVector3 corner1;
    BlockVector3 corner2;

    public void create(Player player, Location location, MineType mineType) {

        String dataSource = "file";
        String worldName = player.getUniqueId().toString();
        File schematicFile = new File("plugins/PrivateMines/schematics/" + mineType.getFile());
        player.sendMessage(schematicFile.getName());
//        SlimePlugin slimePlugin = (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
//        SlimeLoader slimeLoader;
//        SlimePropertyMap slimePropertyMap = new SlimePropertyMap();
//        slimePropertyMap.setValue(SlimeProperties.WORLD_TYPE, "flat");
//
//        if (slimePlugin != null) {
//            slimeLoader = slimePlugin.getLoader("file");
//            slimePlugin.asyncCreateEmptyWorld(slimeLoader, "test", false, slimePropertyMap).thenAcceptAsync(optSlimeWorld ->
//                    optSlimeWorld.ifPresent(slimePlugin::generateWorld));
//            try {
//                byte[] slimeWorld = slimeLoader.loadWorld("test", false);
//                privateMines.getLogger().info("slimeWorld: " + Arrays.toString(slimeWorld));
//            } catch (UnknownWorldException | WorldInUseException | IOException e) {
//                e.printStackTrace();
//            }
//
//            World world = Bukkit.getWorld("test");
//            privateMines.getLogger().info("world " + world);
//            if (world != null) {
//                player.teleport(world.getSpawnLocation());
//            }
//
//
////            slimePlugin.asyncCreateEmptyWorld(slimeLoader, "test", false, slimePropertyMap);
////            slimePlugin.asyncCreateEmptyWorld(slimeLoader, "test", false, slimePropertyMap)
////                    .thenAcceptAsync(slimeWorld -> slimeWorld.ifPresent(slimeWorld1 -> {
////                        Bukkit.getScheduler().runTask(privateMines, () -> {
////                            slimePlugin.generateWorld(slimeWorld1);
////                        });
////                    }));
//
////                SlimeWorld slimeWorld = slimePlugin.loadWorld(slimeLoader, "test", false, slimePropertyMap);
////                slimePlugin.generateWorld(slimeWorld);
//        }

//        World world = Bukkit.getWorld("test");
//        if (world != null) {
//            player.teleport(world.getSpawnLocation());
//        }

        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(schematicFile);
        BlockVector3 blockVector3 = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        // /pmine teleport
        // /pmine teleport <name>
        // /pmine give <name>

        schematicStorage = privateMines.getSchematicStorage();
        SchematicIterator.MineBlocks mineBlocks = schematicStorage.getMineBlocksMap().get(schematicFile);
        int corner1X = mineBlocks.getCorner1().getX();
        int corner1Y = mineBlocks.getCorner1().getY();
        int corner1Z = mineBlocks.getCorner1().getZ();

        int corner2X = mineBlocks.getCorner2().getX();
        int corner2Y = mineBlocks.getCorner2().getY();
        int corner2Z = mineBlocks.getCorner2().getZ();

        int spawnX = mineBlocks.getSpawnLocation().getX();
        int spawnY = mineBlocks.getSpawnLocation().getY();
        int spawnZ = mineBlocks.getSpawnLocation().getZ();


        // Pastes schematic into world -> Loops every single block in region to find certain block type -> sets the location in the minedata -> stores the minedata file as a json

        Task task = Task.asyncDelayed(() -> {
            Clipboard clipboard;
            ClipboardHolder clipboardHolder;
            Region region;
            BlockVector3 origin;

            if (clipboardFormat != null) {
                try (ClipboardReader clipboardReader = clipboardFormat.getReader(new FileInputStream(schematicFile))) {
                    World world = BukkitAdapter.adapt(Objects.requireNonNull(location.getWorld()));
                    EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).build();
                    LocalSession localSession = new LocalSession();

                    clipboard = clipboardReader.read();
                    clipboardHolder = new ClipboardHolder(clipboard);
                    region = clipboard.getRegion();
                    localSession.setClipboard(clipboardHolder);

                    Instant start = Instant.now();

                    Operation operation = clipboardHolder
                            .createPaste(editSession)
                            .to(blockVector3)
                            .ignoreAirBlocks(true)
                            .build();

                    privateMines.getLogger().info("world: " + world);
                    privateMines.getLogger().info("editSession: " + editSession);
                    privateMines.getLogger().info("localSession: " + localSession);
                    privateMines.getLogger().info("localSession clipboard: " + localSession.getClipboard());

                    try {
                        Operations.complete(operation);
                        editSession.close();
                    } catch (WorldEditException worldEditException) {
                        worldEditException.printStackTrace();
                    }

//                    Operation operation = new ClipboardHolder(clipboard)
//                            .createPaste(editSession)
//                            .to(blockVector3)
//                            .ignoreAirBlocks(true)
//                            .build();

                    BlockVector3 clipboardOffset = clipboard.getRegion().getMinimumPoint().subtract(clipboard.getOrigin());
                    Vector3 realTo = blockVector3.toVector3().add(clipboardHolder.getTransform().apply(clipboardOffset.toVector3()));
                    privateMines.getLogger().info("realTo: " + realTo);

                    BlockVector3 spawnVector3 = clipboardOffset.add(mineBlocks.getSpawnLocation());
                    BlockVector3 test = BlockVector3.at(clipboard.getOrigin().getX(), clipboard.getOrigin().getY(), clipboard.getOrigin().getZ());

                    //Spawn Location
                    Location spawn = new Location(location.getWorld(), blockVector3.getBlockX(), blockVector3.getBlockY(), blockVector3.getBlockZ());
                    spawn.add(spawnX, spawnY, spawnZ);

                    //Corner1
                    Location location1 = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());

                    //Corner2
                    Location location2 = new Location(location.getWorld(), location.getBlockX(), location.getBlockY(), location.getBlockZ());

////                    privateMines.getLogger().info("clipboard: " + clipboard);
////                    privateMines.getLogger().info("edit session: " + editSession);
////                    privateMines.getLogger().info("blockVector3: " + blockVector3);
////                    privateMines.getLogger().info("The spawn vector for " + schematicFile.getName() + " is " + mineBlocks.getSpawnLocation());
////                    privateMines.getLogger().info("The corner 1 vector for " + schematicFile.getName() + " is " + mineBlocks.getCorner1());
////                    privateMines.getLogger().info("The corner 2 vector for " + schematicFile.getName() + " is " + mineBlocks.getCorner2());
////                    privateMines.getLogger().info("" + world.getBlock(blockVector3.add(mineBlocks.getCorner1())));
////                    privateMines.getLogger().info("corner1: " + Utils.getRelative(region, mineBlocks.getCorner1().getX(), mineBlocks.getCorner1().getY(), mineBlocks.getCorner1().getZ()));
////
////                    BlockVector3 spawnVector = BlockVector3.at(mineBlocks.getSpawnLocation().getX(),
////                                                               mineBlocks.getSpawnLocation().getY(),
////                                                               mineBlocks.getSpawnLocation().getZ());
////
////                    Location pasteLocation = BukkitAdapter.adapt(BukkitAdapter.adapt(world), blockVector3);
////                    Location spawnTest = BukkitAdapter.adapt(BukkitAdapter.adapt(world), region.getCenter().add(spawnVector.toVector3()));
////
////                    privateMines.getLogger().info("paste location: " + pasteLocation);
////                    privateMines.getLogger().info("spawn test location: " + pasteLocation.clone().subtract(spawnTest));
////
////                    Location spawnLocation = Utils.getRelative(region,
////                                                               mineBlocks.getSpawnLocation().getBlockX(),
////                                                               mineBlocks.getSpawnLocation().getBlockY(),
////                                                               mineBlocks.getSpawnLocation().getBlockZ());
////                    Location corner1Location = BukkitAdapter.adapt(location.getWorld(), blockVector3.subtract(corner1));
////                    Location corner2Location = BukkitAdapter.adapt(location.getWorld(), blockVector3.subtract(corner2));
//
////                    privateMines.getLogger().info("spawn: " + spawnLocation);
//
//                    Instant end = Instant.now();
//                    Duration timeElapsedStream = Duration.between(start, end);
//                    privateMines.getLogger().info("Time elapsed: " + timeElapsedStream.toMillis() + "ms");
////                    privateMines.getLogger().info("spawnLocation: " + spawnLocation);
////                    privateMines.getLogger().info("corner1Location: " + corner1Location);
////                    privateMines.getLogger().info("corner2Location: " + corner2Location);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
                } catch (IOException | EmptyClipboardException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }
}
