package me.untouchedodin0.privatemines.utils.conversion;

import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mine.MineTypeManager;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import redempt.redlib.misc.LocationUtils;

import java.util.Objects;
import java.util.UUID;

public class ConversionSQLUtils {

    private static final PrivateMines privateMines = PrivateMines.getPrivateMines();
    private static final MineTypeManager mineTypeManager = privateMines.getMineTypeManager();

    public static String generateCommand(YamlConfiguration yml) {

        UUID owner = UUID.fromString(Objects.requireNonNull(yml.getString("mineOwner")));
        String mineTypeName = yml.getString("mineType");
        MineType mineType = mineTypeManager.getMineType(mineTypeName);
        Location corner1 = LocationUtils.fromString(yml.getString("corner1"));
        Location corner2 = LocationUtils.fromString(yml.getString("corner2"));
        Location fullRegionMin = LocationUtils.fromString(yml.getString("fullRegionMin"));
        Location fullRegionMax = LocationUtils.fromString(yml.getString("fullRegionMax"));
        Location spawn = LocationUtils.fromString(yml.getString("spawn"));
        Location mineLocation = LocationUtils.fromString(yml.getString("mineLocation"));
        boolean isOpen = yml.getBoolean("isOpen");
        double tax = yml.getDouble("tax");
        String materialsString = yml.getString("materials");

        return String.format("INSERT INTO privatemines (mineOwner) VALUES(%s);", "test");
    }
}
