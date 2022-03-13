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
import me.untouchedodin0.privatemines.utils.Utils;
import me.untouchedodin0.privatemines.utils.task.Task;
import org.bukkit.Location;

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

    public void create(Location location, MineType mineType) {
        File schematicFile = new File("plugins/PrivateMines/schematics/" + mineType.getFile());
        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(schematicFile);
        BlockVector3 blockVector3 = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        schematicStorage = privateMines.getSchematicStorage();
        SchematicIterator.MineBlocks mineBlocks = schematicStorage.getMineBlocksMap().get(schematicFile);

        Task task = Task.asyncDelayed(() -> {
            Clipboard clipboard;
            Region region;
            BlockVector3 clipboardOffset;
            BlockVector3 realTo;
            if (clipboardFormat != null) {
                try (ClipboardReader clipboardReader = clipboardFormat.getReader(new FileInputStream(schematicFile))) {
                    World world = BukkitAdapter.adapt(Objects.requireNonNull(location.getWorld()));
                    EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).build();

                    clipboard = clipboardReader.read();
                    region = clipboard.getRegion();

                    clipboardOffset = clipboard.getRegion().getMinimumPoint().subtract(clipboard.getOrigin());
                    realTo = blockVector3.toVector3().add(clipboard.getOrigin().getX(), clipboard.getOrigin().getY(), clipboard.getOrigin().getZ()).toBlockPoint();
                    spawn = blockVector3.toVector3().subtract(clipboard.getOrigin().getX(), clipboard.getOrigin().getY(), clipboard.getOrigin().getZ()).toBlockPoint();

                    privateMines.getLogger().info("clipboard: " + clipboard);
                    privateMines.getLogger().info("edit session: " + editSession);
                    privateMines.getLogger().info("blockVector3: " + blockVector3);
                    privateMines.getLogger().info("The spawn vector for " + schematicFile.getName() + " is " + mineBlocks.getSpawnLocation());
                    privateMines.getLogger().info("The corner 1 vector for " + schematicFile.getName() + " is " + mineBlocks.getCorner1());
                    privateMines.getLogger().info("The corner 2 vector for " + schematicFile.getName() + " is " + mineBlocks.getCorner2());
                    privateMines.getLogger().info("" + world.getBlock(blockVector3.add(mineBlocks.getCorner1())));

//                    Location location1 = BukkitAdapter.adapt(location.getWorld(), corner1);
//                    Location location2 = BukkitAdapter.adapt(location.getWorld(), corner2);

                    Instant start = Instant.now();

                    Operation operation = new ClipboardHolder(clipboard)
                            .createPaste(editSession)
                            .to(blockVector3)
                            .ignoreAirBlocks(true)
                            .build();



                    Location spawnLocation = Utils.getRelative(region,
                                                               mineBlocks.getSpawnLocation().getX(),
                                                               mineBlocks.getSpawnLocation().getY(),
                                                               mineBlocks.getSpawnLocation().getZ());
//                    Location corner1Location = BukkitAdapter.adapt(location.getWorld(), blockVector3.subtract(corner1));
//                    Location corner2Location = BukkitAdapter.adapt(location.getWorld(), blockVector3.subtract(corner2));

                    privateMines.getLogger().info("spawn: " + spawn);
                    try {
                        Operations.complete(operation);
                        editSession.close();
                    } catch (WorldEditException worldEditException) {
                        worldEditException.printStackTrace();
                    }

                    Instant end = Instant.now();
                    Duration timeElapsedStream = Duration.between(start, end);
                    privateMines.getLogger().info("Time elapsed: " + timeElapsedStream.toMillis() + "ms");
                    privateMines.getLogger().info("spawnLocation: " + spawnLocation);
//                    privateMines.getLogger().info("corner1Location: " + corner1Location);
//                    privateMines.getLogger().info("corner2Location: " + corner2Location);

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
