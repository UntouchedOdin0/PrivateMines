/**
 * MIT License
 * <p>
 * Copyright (c) 2021 - 2023 Kyle Hicks
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.untouchedodin0.privatemines.utils;

import com.google.gson.GsonBuilder;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import me.untouchedodin0.kotlin.mine.data.MineData;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.utils.adapter.LocationAdapter;
import me.untouchedodin0.privatemines.utils.adapter.PathAdapter;
import org.bukkit.Location;
import redempt.redlib.misc.LocationUtils;
import redempt.redlib.sql.SQLHelper;

public class SQLUtils {

  public static String updateString =
      "INSERT INTO privatemines (owner, mineType, corner1, corner2, fullMin, fullMax, spawn, open) "
          + " VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

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

      /**
       *         sqlHelper.executeUpdate("CREATE TABLE IF NOT EXISTS `privatemines` (" +
       *                 "`owner` TEXT NOT NULL," +
       *                 "`mineType` TEXT," +
       *                 "`corner1` TEXT," +
       *                 "`corner2` TEXT," +
       *                 "`fullMin` TEXT," +
       *                 "`fullMax` TEXT," +
       *                 "`spawn` TEXT," +
       *                 "`open` BOOLEAN);");
       */

      updateStatement.setString(1, "uuid?");
      updateStatement.setString(2, "minetype?");
      updateStatement.setString(3, "corner1?");
      updateStatement.setString(4, "corner2?");
      updateStatement.setString(5, "fullmin");
      updateStatement.setString(6, "fullmax");
      updateStatement.setString(7, "spawn");
      updateStatement.setBoolean(8, true);

      updateStatement.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
//
//        sqlHelper.executeUpdate("INSERT INTO privatemines (owner=?, mineType=?, corner1=?, corner2=?, fullMin=?, fullMax=?, spawn=?, open=?);",
//                "uuid", "minetype", "corner1", "corner2", "fullMin", "fullMax", "spawn", false);
//        sqlHelper.commit();
//        privateMines.getLogger().info("" + sqlHelper);

//        try {
//            PreparedStatement updateStatement = connection.prepareStatement(updateString);
//
//            /**
//             *         sqlHelper.executeUpdate("CREATE TABLE IF NOT EXISTS `privatemines` (" +
//             *                 "`owner` TEXT NOT NULL," +
//             *                 "`mineType` TEXT," +
//             *                 "`corner1` TEXT," +
//             *                 "`corner2` TEXT," +
//             *                 "`fullMin` TEXT," +
//             *                 "`fullMax` TEXT," +
//             *                 "`spawn` TEXT," +
//             *                 "`open` BOOLEAN);");
//             */
//
////            updateStatement.setString(1, "uuid?");
////            updateStatement.setString(2, "minetype?");
////            updateStatement.setString(3, "corner1?");
////            updateStatement.setString(4, "corner2?");
////            updateStatement.setString(5, "fullmin");
////            updateStatement.setString(6, "fullmax");
////            updateStatement.setString(7, "spawn");
////            updateStatement.setBoolean(8, false);
//
//            // INSERT INTO TABLE_NAME (column1, column2, column3,...columnN)
//            //VALUES (value1, value2, value3,...valueN);
//
//
//
////            updateStatement.setString(1, uuidString);
////            updateStatement.setString(2, mineTypeName);
////            updateStatement.setString(3, corner1);
////            updateStatement.setString(4, corner2);
////            updateStatement.setString(5, fullMin);
////            updateStatement.setString(6, fullMax);
////            updateStatement.setString(7, spawn);
////            updateStatement.setBoolean(8, isOpen);
////            updateStatement.setDouble(9, tax);
////            updateStatement.setString(10, "none");
//
//            updateStatement.executeUpdate();
////            updateStatement.setString(2, json);
//
////            updateStatement.setString(1, String.valueOf(mineData.getMineOwner()));
////            updateStatement.setString(2, mineType.getName());
////            updateStatement.setString(3, LocationUtils.toString(mineData.getMineLocation()));
////            updateStatement.setString(4, LocationUtils.toString(mineData.getMinimumMining()));
////            updateStatement.setString(5, LocationUtils.toString(mineData.getMaximumMining()));
////            updateStatement.setString(6, LocationUtils.toString(mineData.getMinimumFullRegion()));
////            updateStatement.setString(7, LocationUtils.toString(mineData.getMaximumFullRegion()));
////            updateStatement.setString(8, LocationUtils.toString(mineData.getSpawnLocation()));
////            updateStatement.setDouble(9, mineData.getTax());
////            updateStatement.setBoolean(10, mineData.isOpen());
////            updateStatement.setInt(11, mineData.getMaxPlayers());
////            updateStatement.setInt(12, mineData.getMaxMineSize());
////            updateStatement.setString(13, mineData.getMaterials().toString());
//
////            updateStatement.executeUpdate();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
  }
}
