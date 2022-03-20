package me.untouchedodin0.privatemines.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import me.untouchedodin0.privatemines.PrivateMines;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

public class Utils {

    private final PrivateMines privateMines;
    private final Gson gson = new GsonBuilder().create();

    public Utils(PrivateMines privateMines) {
        this.privateMines = privateMines;
    }

    public static Location getRelative(Region region, int x, int y, int z) {
        final BlockVector3 point = region.getMinimumPoint().getMinimum(region.getMaximumPoint());
        final int regionX = point.getX();
        final int regionY = point.getY();
        final int regionZ = point.getZ();
        final BlockVector3 maxPoint = region.getMaximumPoint().getMaximum(region.getMinimumPoint());
        final int maxX = maxPoint.getX();
        final int maxY = maxPoint.getY();
        final int maxZ = maxPoint.getZ();
//        if (x < 0 || y < 0 || z < 0
//                || x > maxX - regionX || y > maxY - regionY || z > maxZ - regionZ) {
//            throw new IndexOutOfBoundsException("Relative location outside bounds of structure: " + x + ", " + y + ", " + z);
//        }
        final World worldeditWorld = region.getWorld();
        final org.bukkit.World bukkitWorld;
        if (worldeditWorld != null) {
            bukkitWorld = BukkitAdapter.asBukkitWorld(worldeditWorld).getWorld();
        } else {
            bukkitWorld = null;
        }
        return new Location(bukkitWorld, regionX + x, regionY + y, regionZ + z);
    }

    public static Location toLocation(BlockVector3 vector3, org.bukkit.World world) {
        return new Location(world, vector3.getX(), vector3.getY(), vector3.getZ());
    }

    /**
     * @param location - The location where you want the schematic to be pasted at
     * @param file     - The file of the schematic you want to paste into the world
     * @see org.bukkit.Location
     * @see java.io.File
     */
    public void paste(Location location, File file) {

        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(file);
        Clipboard clipboard;

        // Create a block vector 3 at the location you want the schematic to be pasted at
        BlockVector3 blockVector3 = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        // If the clipboard format isn't null meaning it found the file load it in and read the data
        if (clipboardFormat != null) {
            try (ClipboardReader clipboardReader = clipboardFormat.getReader(new FileInputStream(file))) {

                // Get the world from the location
                World world = BukkitAdapter.adapt(Objects.requireNonNull(location.getWorld()));

                // Make a new Edit Session by building one.
                EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).build();

                // Read the clipboard reader and set the clipboard data.
                clipboard = clipboardReader.read();

                // Create an operation and paste the schematic

                Operation operation = new ClipboardHolder(clipboard) // Create a new operation instance using the clipboard
                        .createPaste(editSession) // Create a builder using the edit session
                        .to(blockVector3) // Set where you want the paste to go
                        .ignoreAirBlocks(true) // Tell world edit not to paste air blocks (true/false)
                        .build(); // Build the operation

                // Now we try to complete the operation and catch any exceptions

                try {
                    Operations.complete(operation);
                    editSession.close(); // We now close it to flush the buffers and run the cleanup tasks.
                } catch (WorldEditException worldEditException) {
                    worldEditException.printStackTrace();
                }
            } catch (IOException e) {
                // Print any stack traces of which may occur.
                e.printStackTrace();
            }
        }
    }


    public static CuboidRegion toWorldEditCuboid(me.untouchedodin0.privatemines.utils.regions.CuboidRegion cuboidRegion) {
        var min = BlockVector3.at(
                cuboidRegion.getMinimumPoint().getBlockX(),
                cuboidRegion.getMinimumPoint().getBlockY(),
                cuboidRegion.getMinimumPoint().getBlockZ()
        );

        var max = BlockVector3.at(
                cuboidRegion.getMaximumPoint().getBlockX(),
                cuboidRegion.getMaximumPoint().getBlockY(),
                cuboidRegion.getMaximumPoint().getBlockZ()
        );

        return new CuboidRegion(min, max);
    }
}
