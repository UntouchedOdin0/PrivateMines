package me.untouchedodin0.privatemines.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.World;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mine.data.MineData;
import org.bukkit.Location;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

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

    public void saveMineData(UUID uuid, MineData mineData) {
        Path minesDirectory = privateMines.getMinesDirectory();
        Path playerDataFile = minesDirectory.resolve(uuid + ".json");
        try {
            Files.write(playerDataFile, gson.toJson(mineData).getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Could not save mine data", e);
        }
    }
}
