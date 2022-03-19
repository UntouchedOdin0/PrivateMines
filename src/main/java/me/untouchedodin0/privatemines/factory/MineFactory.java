package me.untouchedodin0.privatemines.factory;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.LocalSession;
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

        /*
         *   1. Pastes schematic into world
         *   2. Loops every single block in region to find certain block type
         *   3. Sets the location in the MineData
         *   4. Stores the MineData as a JSON
         */

        Task.asyncDelayed(() -> {
            if (clipboardFormat != null) {
                try (ClipboardReader clipboardReader = clipboardFormat.getReader(new FileInputStream(schematicFile))) {

                    Instant start = Instant.now();

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

                    System.out.println("mb0|" + mineBlocks.getSpawnLocation().toParserString());
                    System.out.println("mb1|" + mineBlocks.getCorner1().toParserString());
                    System.out.println("mb2|" + mineBlocks.getCorner2().toParserString());

                    System.out.println("cbo|" + clipboard.getOrigin().toParserString());

                    System.out.println("min|" + minimumPoint.toParserString());

                    System.out.println("loc|" + location);

//                    mb0|0,50,-150
//                    mb1|13,48,-144
//                    mb2|-12,20,-116
//                    cbo|0,50,-151
//                    min|-30000000,-64,-30000000
//                    loc|763.6692645437984,140.45037494032877,728.8705431310638
//
//                    763,140,729 Sponge
//                    751,110,763 Lower Rails Sponge - mb0 + mb2 -> 763 - 0 +-12 = 751
//                    776,138,735 Upper Rails Sponge - mb0 + mb1 -> 763 - 0 + 13 = 776

//                    Location sponge = vector;
//                    Location lrails = sponge - mineBlocks.getSpawnLocation() + mineBlocks.getCorner2();
//                    Location urails = sponge - mineBlocks.getSpawnLocation() + mineBlocks.getCorner1();

                    BlockVector3 lrailsV = vector.subtract(mineBlocks.getSpawnLocation()).add(mineBlocks.getCorner2());
                    BlockVector3 urailsV = vector.subtract(mineBlocks.getSpawnLocation()).add(mineBlocks.getCorner1());

                    Location spongeL = new Location(location.getWorld(), vector.getBlockX(),  vector.getBlockY(),  vector.getBlockZ() + 1);
                    Location lrailsL = new Location(location.getWorld(), lrailsV.getBlockX(), lrailsV.getBlockY(), lrailsV.getBlockZ() + 1);
                    Location urailsL = new Location(location.getWorld(), urailsV.getBlockX(), urailsV.getBlockY(), urailsV.getBlockZ() + 1);

                    System.out.println("S|" + spongeL); // spawn
                    System.out.println("L|" + lrailsL); // lower corner
                    System.out.println("U|" + urailsL); // upper corner

                    Instant endOfIterator = Instant.now();

                    localSession.setClipboard(clipboardHolder);

                    Operation operation = clipboardHolder
                            .createPaste(editSession)
                            .to(vector)
                            .ignoreAirBlocks(true)
                            .build();
                    try {
                        Operations.completeLegacy(operation);
                        editSession.close();
                    } catch (WorldEditException worldEditException) {
                        worldEditException.printStackTrace();
                    }
                    Instant pasted = Instant.now();
                    Duration durationIterator = Duration.between(start, endOfIterator);
                    Duration durationPasted = Duration.between(start, pasted);

                    privateMines.getLogger().info("Iterator time: " + durationIterator.toMillis() + "ms");
                    privateMines.getLogger().info("Pasted time: " + durationPasted.toMillis() + "ms");

                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });
    }
}
