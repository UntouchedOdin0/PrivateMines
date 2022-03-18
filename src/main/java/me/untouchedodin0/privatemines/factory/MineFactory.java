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
import com.sk89q.worldedit.regions.RegionSelector;
import com.sk89q.worldedit.regions.selector.CuboidRegionSelector;
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
import java.util.Objects;

public class MineFactory {

    PrivateMines privateMines = PrivateMines.getPrivateMines();

    public void create(Player player, Location location, MineType mineType) {

        File schematicFile = new File("plugins/PrivateMines/schematics/" + mineType.getFile());
        player.sendMessage(schematicFile.getName());

        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(schematicFile);
        BlockVector3 vector = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        SchematicStorage storage = privateMines.getSchematicStorage();
        privateMines.getLogger().info("storage: " + storage);
        privateMines.getLogger().info("mineBlocks: " + storage.getMineBlocksMap().get(schematicFile));

        SchematicIterator.MineBlocks mineBlocks = storage.getMineBlocksMap().get(schematicFile);

        BlockVector3 spawnOffset = mineBlocks.getSpawnLocation(), cornerOneOffset = mineBlocks.getCorner1(), cornerTwoOffset = mineBlocks.getCorner2();

        Location spawn = location.clone().add(spawnOffset.getX(), spawnOffset.getY(), spawnOffset.getZ());
        Location cornerOne = location.clone().add(cornerOneOffset.getX(), cornerOneOffset.getY(), cornerOneOffset.getZ());
        Location cornerTwo = location.clone().add(cornerTwoOffset.getX(), cornerTwoOffset.getY(), cornerTwoOffset.getZ());

        /*
         *   1. Pastes schematic into world
         *   2. Loops every single block in region to find certain block type
         *   3. Sets the location in the MineData
         *   4. Stores the MineData as a JSON
         */

        Task.asyncDelayed(() -> {
            if (clipboardFormat != null) {
                try (ClipboardReader clipboardReader = clipboardFormat.getReader(new FileInputStream(schematicFile))) {
                    World world = BukkitAdapter.adapt(Objects.requireNonNull(location.getWorld()));
                    EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).build();
                    LocalSession localSession = new LocalSession();
                    BlockVector3 minimumPoint = editSession.getMinimumPoint();

                    privateMines.getLogger().info("minimumPoint: " + minimumPoint);
                    privateMines.getLogger().info("minimumPoint + vector: " + minimumPoint.add(vector));
                    privateMines.getLogger().info("minimumPoint + vector + spawn: " + minimumPoint.add(vector).add(spawnOffset));

                    Clipboard clipboard = clipboardReader.read();
                    ClipboardHolder clipboardHolder = new ClipboardHolder(clipboard);
                    Region region = clipboard.getRegion();

                    localSession.setClipboard(clipboardHolder);

                    Operation operation = clipboardHolder
                            .createPaste(editSession)
                            .to(vector)
                            .ignoreAirBlocks(true)
                            .build();
                    try {
                        Operations.complete(operation);
                        editSession.close();
                    } catch (WorldEditException worldEditException) {
                        worldEditException.printStackTrace();
                    }

                    BlockVector3 clipboardOffset = clipboard.getRegion().getMinimumPoint().subtract(clipboard.getOrigin());
                    Vector3 realTo = vector.toVector3().add(clipboardHolder.getTransform().apply(clipboardOffset.toVector3()));
                    Vector3 min = realTo.subtract(clipboardHolder.getTransform().apply(region.getMinimumPoint().add(region.getMaximumPoint()).toVector3()));
                    Vector3 max = realTo.add(clipboardHolder.getTransform().apply(region.getMaximumPoint().subtract(region.getMinimumPoint()).toVector3()));
                    RegionSelector selector = new CuboidRegionSelector(world, realTo.toBlockPoint(), max.toBlockPoint());
                    selector.learnChanges();

                    privateMines.getLogger().info("clipboardOffset: " + clipboardOffset);
                    privateMines.getLogger().info("realTo: " + realTo);
                    privateMines.getLogger().info("min: " + min);
                    privateMines.getLogger().info("max: " + max);
                    privateMines.getLogger().info("selector: " + selector);

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }
}
