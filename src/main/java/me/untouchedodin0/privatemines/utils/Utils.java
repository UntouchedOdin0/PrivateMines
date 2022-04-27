package me.untouchedodin0.privatemines.utils;

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
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.FlagContext;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.untouchedodin0.privatemines.PrivateMines;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class Utils {

    private final PrivateMines privateMines;
    public static final String TABLE_NAME = "privatemines";

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

    public static void complain() {
        PrivateMines.getPrivateMines().getLogger().info(ChatColor.RED + "This version of Minecraft is extremely outdated and support\n for it has reached its end of life.");
        PrivateMines.getPrivateMines().getLogger().info(ChatColor.RED + "You will be unable to run Private Mines on this Minecraft version,");
        PrivateMines.getPrivateMines().getLogger().info(ChatColor.RED + "and we will not to provide any further fixes or help with problems specific to legacy Minecraft versions.");
        PrivateMines.getPrivateMines().getLogger().info(ChatColor.RED + "Please consider updating to give your players a better experience and to avoid issues that have long been fixed.");
    }

    @SuppressWarnings("all") // I know I know, this is bad to do but ffs it wont' shut up
    public static void setMineFlags(ProtectedRegion protectedRegion, Map<String, Boolean> flags) {
        FlagRegistry flagRegistry = WorldGuard.getInstance().getFlagRegistry();

        //todo fix this

//        flags.entrySet().stream().forEach(stringBooleanEntry -> {
//            String flag = stringBooleanEntry.getKey();
//            boolean value = stringBooleanEntry.getValue();
//
//            Bukkit.getLogger().info("flag: " + flag);
//            Bukkit.getLogger().info("value: " + value);
////            Optional<IWrappedFlag<WrappedState>> iWrappedFlag = worldGuardWrapper.getFlag(flag, WrappedState.class);
////            if (value == false) {
////                iWrappedRegion.get().setFlag(iWrappedFlag.get(), WrappedState.DENY);
////            } else if (value == true) {
////                iWrappedRegion.get().setFlag(iWrappedFlag.get(), WrappedState.ALLOW);
////            }
//        });
    }

    /**
     * Utility method to set a flag.
     * <p>
     * Borrowed from https://github.com/EngineHub/WorldGuard/blob/bc63119373d4603e5b040460c41e712275a4d062/worldguard-core/src/main/java/com/sk89q/worldguard/commands/region/RegionCommandsBase.java#L414-L427
     *
     * @param region the region
     * @param flag   the flag
     * @param value  the value
     * @throws InvalidFlagFormat thrown if the value is invalid
     */
    public static <V> void setFlag(ProtectedRegion region, Flag<V> flag, String value) throws InvalidFlagFormat {
        V val = flag.parseInput(FlagContext.create().setInput(value).setObject("region", region).build());
        region.setFlag(flag, val);
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
}
