package me.untouchedodin0.privatemines.utils;

import com.google.gson.Gson;
import me.untouchedodin0.kotlin.mine.data.MineData;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mine.Mine;
import redempt.redlib.misc.LocationUtils;
import redempt.redlib.sql.SQLHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class SQLUtils {

    public static String updateString = "INSERT INTO privatemines (uuid, json) VALUES (?, ?);";
//    public static String updateString = "INSERT INTO privatemines " +
//            "(mineOwner, mineType, mineLocation, corner1, corner2, fullRegionMin, fullRegionMax, spawn, tax, isOpen, maxPlayers, maxMineSize, materials)" +
//            " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public static void insert(Mine mine) {
        PrivateMines privateMines = PrivateMines.getPrivateMines();
        Gson gson = privateMines.getGson();

        SQLHelper sqlHelper = privateMines.getSqlHelper();
        Connection connection = sqlHelper.getConnection();

        MineData mineData = mine.getMineData();
        MineType mineType = mineData.getMineType();

        UUID uuid = mineData.getMineOwner();
        String uuidString = String.valueOf(uuid);
        String json = gson.toJson(mine);

//        privateMines.getLogger().info("saving mine.....");
//        privateMines.getLogger().info("uuid: " + uuid);
//        privateMines.getLogger().info("connection " + connection);
        privateMines.getLogger().info("json: " + json);

        try {
            PreparedStatement updateStatement = connection.prepareStatement(updateString);

            updateStatement.setString(1, uuidString);
            updateStatement.setString(2, "jsonTest");
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
