package me.untouchedodin0.privatemines.factory;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
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
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public class MineFactory {

    PrivateMines privateMines = PrivateMines.getPrivateMines();
    SchematicStorage schematicStorage;

    BlockVector3 spawn;
    BlockVector3 corner1;
    BlockVector3 corner2;

    public void create(Player player, Location location, MineType mineType) {
        File schematicFile = new File("plugins/PrivateMines/schematics/" + mineType.getFile());
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

        Task task = Task.asyncDelayed(() -> {
            Clipboard clipboard;
            Region region;

            if (clipboardFormat != null) {
                try (ClipboardReader clipboardReader = clipboardFormat.getReader(new FileInputStream(schematicFile))) {
                    World world = BukkitAdapter.adapt(Objects.requireNonNull(location.getWorld()));
                    EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).build();

                    clipboard = clipboardReader.read();
                    region = clipboard.getRegion();

                    Instant start = Instant.now();

                    Operation operation = new ClipboardHolder(clipboard)
                            .createPaste(editSession)
                            .to(blockVector3)
                            .ignoreAirBlocks(true)
                            .build();
                    clipboard.setOrigin(blockVector3);
                    privateMines.getLogger().info("clipboard origin: " + clipboard.getOrigin());

                    Location location1 = new Location(location.getWorld(), location.getBlockX() + corner1X, location.getBlockY() + corner1Y, location.getBlockZ() + corner1Z);
                    Location location2 = new Location(location.getWorld(), location.getBlockX() + corner2X, location.getBlockY() + corner2Y, location.getBlockZ() + corner2Z);
                    privateMines.getLogger().info(" " + location1);
                    privateMines.getLogger().info(" " + location2);

//                    privateMines.getLogger().info("clipboard: " + clipboard);
//                    privateMines.getLogger().info("edit session: " + editSession);
//                    privateMines.getLogger().info("blockVector3: " + blockVector3);
//                    privateMines.getLogger().info("The spawn vector for " + schematicFile.getName() + " is " + mineBlocks.getSpawnLocation());
//                    privateMines.getLogger().info("The corner 1 vector for " + schematicFile.getName() + " is " + mineBlocks.getCorner1());
//                    privateMines.getLogger().info("The corner 2 vector for " + schematicFile.getName() + " is " + mineBlocks.getCorner2());
//                    privateMines.getLogger().info("" + world.getBlock(blockVector3.add(mineBlocks.getCorner1())));
//                    privateMines.getLogger().info("corner1: " + Utils.getRelative(region, mineBlocks.getCorner1().getX(), mineBlocks.getCorner1().getY(), mineBlocks.getCorner1().getZ()));
//
//                    BlockVector3 spawnVector = BlockVector3.at(mineBlocks.getSpawnLocation().getX(),
//                                                               mineBlocks.getSpawnLocation().getY(),
//                                                               mineBlocks.getSpawnLocation().getZ());
//
//                    Location pasteLocation = BukkitAdapter.adapt(BukkitAdapter.adapt(world), blockVector3);
//                    Location spawnTest = BukkitAdapter.adapt(BukkitAdapter.adapt(world), region.getCenter().add(spawnVector.toVector3()));
//
//                    privateMines.getLogger().info("paste location: " + pasteLocation);
//                    privateMines.getLogger().info("spawn test location: " + pasteLocation.clone().subtract(spawnTest));
//
//                    Location spawnLocation = Utils.getRelative(region,
//                                                               mineBlocks.getSpawnLocation().getBlockX(),
//                                                               mineBlocks.getSpawnLocation().getBlockY(),
//                                                               mineBlocks.getSpawnLocation().getBlockZ());
//                    Location corner1Location = BukkitAdapter.adapt(location.getWorld(), blockVector3.subtract(corner1));
//                    Location corner2Location = BukkitAdapter.adapt(location.getWorld(), blockVector3.subtract(corner2));

//                    privateMines.getLogger().info("spawn: " + spawnLocation);
                    try {
                        Operations.complete(operation);
                        editSession.close();
                    } catch (WorldEditException worldEditException) {
                        worldEditException.printStackTrace();
                    }

                    Instant end = Instant.now();
                    Duration timeElapsedStream = Duration.between(start, end);
                    privateMines.getLogger().info("Time elapsed: " + timeElapsedStream.toMillis() + "ms");
//                    privateMines.getLogger().info("spawnLocation: " + spawnLocation);
//                    privateMines.getLogger().info("corner1Location: " + corner1Location);
//                    privateMines.getLogger().info("corner2Location: " + corner2Location);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
