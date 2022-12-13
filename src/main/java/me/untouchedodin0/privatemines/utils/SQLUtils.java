package me.untouchedodin0.privatemines.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.untouchedodin0.kotlin.mine.data.MineData;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.utils.adapter.LocationAdapter;
import me.untouchedodin0.privatemines.utils.adapter.PathAdapter;
import org.bukkit.Location;
import redempt.redlib.misc.LocationUtils;
import redempt.redlib.sql.SQLHelper;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class SQLUtils {

    public static String updateString = "INSERT INTO privatemines (owner, mineType, corner1, corner2, fullMin, fullMax, spawn, open, tax);" +
            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

//    public static String updateString = "INSERT INTO privatemines (owner, mineType, corner1, corner2, fullMin, fullMax, spawn, open, tax, materials);" +
//            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
//    public static String updateString = "INSERT INTO privatemines " +
//            "(mineOwner, mineType, mineLocation, corner1, corner2, fullRegionMin, fullRegionMax, spawn, tax, isOpen, maxPlayers, maxMineSize, materials)" +
//            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static void insert(Mine mine) {
        PrivateMines privateMines = PrivateMines.getPrivateMines();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(Location.class, new LocationAdapter());
        gsonBuilder.registerTypeAdapter(Path.class, new PathAdapter());
//        Gson gson = gsonBuilder.create();

        SQLHelper sqlHelper = privateMines.getSqlHelper();
        Connection connection = sqlHelper.getConnection();
        privateMines.getLogger().info("connection " + connection);

        MineData mineData = mine.getMineData();
        MineType mineType = mineData.getMineType();

        UUID uuid = mineData.getMineOwner();
        String uuidString = String.valueOf(uuid);
        String mineTypeName = mineType.getName();
        String corner1 = LocationUtils.toString(mineData.getMinimumMining());
        String corner2 = LocationUtils.toString(mineData.getMaximumMining());
        String fullMin = LocationUtils.toString(mineData.getMinimumFullRegion());
        String fullMax = LocationUtils.toString(mineData.getMaximumFullRegion());
        String spawn = LocationUtils.toString(mine.getSpawnLocation());
        boolean isOpen = mineData.isOpen();
        double tax = mineData.getTax();

        privateMines.getLogger().info("insertting mine " + mine);
        try {
            PreparedStatement updateStatement = connection.prepareStatement(updateString);

            updateStatement.setString(1, "uuid?");
            updateStatement.setString(2, "minetype?");
            updateStatement.setString(3, "corner1?");
            updateStatement.setString(4, "corner2?");
            updateStatement.setString(5, "fullmin");
            updateStatement.setString(6, "fullmax");
            updateStatement.setString(7, "spawn");
            updateStatement.setBoolean(8, false);
            updateStatement.setDouble(9, 5.0);
            updateStatement.setString(10, "nothing");
            privateMines.getLogger().info("" + updateStatement);

//            updateStatement.setString(1, uuidString);
//            updateStatement.setString(2, mineTypeName);
//            updateStatement.setString(3, corner1);
//            updateStatement.setString(4, corner2);
//            updateStatement.setString(5, fullMin);
//            updateStatement.setString(6, fullMax);
//            updateStatement.setString(7, spawn);
//            updateStatement.setBoolean(8, isOpen);
//            updateStatement.setDouble(9, tax);
//            updateStatement.setString(10, "none");

            updateStatement.executeUpdate();
//            updateStatement.setString(2, json);

//            updateStatement.setString(1, String.valueOf(mineData.getMineOwner()));
//            updateStatement.setString(2, mineType.getName());
//            updateStatement.setString(3, LocationUtils.toString(mineData.getMineLocation()));
//            updateStatement.setString(4, LocationUtils.toString(mineData.getMinimumMining()));
//            updateStatement.setString(5, LocationUtils.toString(mineData.getMaximumMining()));
//            updateStatement.setString(6, LocationUtils.toString(mineData.getMinimumFullRegion()));
//            updateStatement.setString(7, LocationUtils.toString(mineData.getMaximumFullRegion()));
//            updateStatement.setString(8, LocationUtils.toString(mineData.getSpawnLocation()));
//            updateStatement.setDouble(9, mineData.getTax());
//            updateStatement.setBoolean(10, mineData.isOpen());
//            updateStatement.setInt(11, mineData.getMaxPlayers());
//            updateStatement.setInt(12, mineData.getMaxMineSize());
//            updateStatement.setString(13, mineData.getMaterials().toString());

//            updateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
