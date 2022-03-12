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
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.iterator.SchematicIterator;
import me.untouchedodin0.privatemines.storage.SchematicStorage;
import me.untouchedodin0.privatemines.type.MineType;
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

    public void create(Location location, MineType mineType) {
        File schematicFile = new File("plugins/PrivateMines/schematics/" + mineType.getFile());
        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(schematicFile);
        BlockVector3 blockVector3 = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        schematicStorage = privateMines.getSchematicStorage();
        SchematicIterator.MineBlocks mineBlocks = schematicStorage.getMineBlocksMap().get(schematicFile);

        Task task = Task.asyncDelayed(() -> {
            Clipboard clipboard;
            if (clipboardFormat != null) {
                try (ClipboardReader clipboardReader = clipboardFormat.getReader(new FileInputStream(schematicFile))) {
                    clipboard = clipboardReader.read();

                    World world = BukkitAdapter.adapt(Objects.requireNonNull(location.getWorld()));
                    EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).build();
                    privateMines.getLogger().info("clipboard: " + clipboard);
                    privateMines.getLogger().info("edit session: " + editSession);
                    privateMines.getLogger().info("blockVector3: " + blockVector3);
                    privateMines.getLogger().info("The spawn vector for " + schematicFile.getName() + " is " + mineBlocks.getSpawnLocation());
                    privateMines.getLogger().info("The corner 1 vector for " + schematicFile.getName() + " is " + mineBlocks.getCorner1());
                    privateMines.getLogger().info("The corner 2 vector for " + schematicFile.getName() + " is " + mineBlocks.getCorner2());

                    Instant start = Instant.now();

                    Operation operation = new ClipboardHolder(clipboard)
                            .createPaste(editSession)
                            .to(blockVector3)
                            .ignoreAirBlocks(true)
                            .build();
                    try {
                        Operations.complete(operation);
                        editSession.close();
                    } catch (WorldEditException worldEditException) {
                        worldEditException.printStackTrace();
                    }

                    Instant end = Instant.now();
                    Duration timeElapsedStream = Duration.between(start, end);
                    privateMines.getLogger().info("Time elapsed: " + timeElapsedStream.toMillis() + "ms");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
