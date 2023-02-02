package me.untouchedodin0.privatemines.storage.sql;

import java.util.Map;
import java.util.UUID;
import me.untouchedodin0.kotlin.mine.data.MineData;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mine.Mine;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import redempt.redlib.misc.LocationUtils;
import redempt.redlib.misc.Task;
import redempt.redlib.sql.SQLHelper;
import redempt.redlib.sql.SQLHelper.Results;

public class SQLUtils {

  private static final PrivateMines privateMines = PrivateMines.getPrivateMines();
  private static final SQLHelper sqlHelper = privateMines.getSqlHelper();

  public static Location getCurrentLocation() {
    Results results = sqlHelper.queryResults("SELECT * FROM privatemines;");

    results.forEach(results1 -> {
      if (!results1.next()) {
        privateMines.getLogger().info("The location stopped at " + results1);
      }
    });
    return privateMines.getMineWorldManager().getCurrentLocation();
  }

  public static void insert(Mine mine) {
    MineData mineData = mine.getMineData();
    MineType mineType = mineData.getMineType();
    String owner = mineData.getMineOwner().toString();

    String insertQuery = String.format(
        "INSERT INTO privatemines (owner, mineType, mineLocation, corner1, corner2,"
            + " fullRegionMin, fullRegionMax, spawn, tax, isOpen, maxPlayers, maxMineSize, materials) "
            + "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', %f, %b, %d, %d, '%s');"
            + "ON CONFLICT IGNORE;",
        owner,
        mineType.getName(),
        LocationUtils.toString(mineData.getMineLocation()),
        LocationUtils.toString(mineData.getMinimumMining()),
        LocationUtils.toString(mineData.getMaximumMining()),
        LocationUtils.toString(mineData.getMinimumFullRegion()),
        LocationUtils.toString(mineData.getMaximumFullRegion()),
        LocationUtils.toString(mine.getSpawnLocation()),
        mineData.getTax(),
        mineData.isOpen(),
        mineData.getMaxPlayers(),
        mineData.getMaxMineSize(),
        "{SPONGE=1.0, STONE=1.0, DIRT=1.0}"
    );

    sqlHelper.executeUpdate(insertQuery);
  }

  public static void replace(Mine mine) {
    MineData mineData = mine.getMineData();
    MineType mineType = mineData.getMineType();
    String owner = mineData.getMineOwner().toString();

    String dropQuery = String.format("DELETE FROM privatemines WHERE owner = '%s'", owner);
    String insertQuery = String.format(
        "INSERT INTO privatemines (owner, mineType, mineLocation, corner1, corner2,"
            + " fullRegionMin, fullRegionMax, spawn, tax, isOpen, maxPlayers, maxMineSize, materials) "
            + "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', %f, %b, %d, %d, '%s');"
            + "ON CONFLICT (owner, mineType, mineLocation) DO UPDATE SET "
            + "corner1 = EXCLUDED.corner1, corner2 = EXCLUDED.corner2, fullRegionMin = EXCLUDED.fullRegionMin, "
            + "fullRegionMax = EXCLUDED.fullRegionMax, spawn = EXCLUDED.spawn, tax = EXCLUDED.tax, "
            + "isOpen = EXCLUDED.isOpen, maxPlayers = EXCLUDED.maxPlayers, maxMineSize = EXCLUDED.maxMineSize, "
            + "materials = '%s';",
        owner,
        mineType.getName(),
        LocationUtils.toString(mineData.getMineLocation()),
        LocationUtils.toString(mineData.getMinimumMining()),
        LocationUtils.toString(mineData.getMaximumMining()),
        LocationUtils.toString(mineData.getMinimumFullRegion()),
        LocationUtils.toString(mineData.getMaximumFullRegion()),
        LocationUtils.toString(mine.getSpawnLocation()),
        mineData.getTax(),
        mineData.isOpen(),
        mineData.getMaxPlayers(),
        mineData.getMaxMineSize(),
        "{DIRT=1.0}", //todo replace with actual materials
        "updateMaterials"
    );
    delete(mine);
    Task.asyncDelayed(() -> {
      sqlHelper.executeUpdate(dropQuery);
      sqlHelper.executeUpdate(insertQuery);
      sqlHelper.commit();
    }, 20L);
  }

  public static void delete(Mine mine) {
    MineData mineData = mine.getMineData();
    String owner = mineData.getMineOwner().toString();
    String dropQuery = String.format("DELETE FROM privatemines WHERE owner = '%s'", owner);

    sqlHelper.execute(dropQuery);
    sqlHelper.commit();

//    sqlHelper.commit();
  }

  public static void get(UUID uuid) {
    String owner = uuid.toString();
    String selectQuery = String.format("SELECT * FROM privatemines WHERE owner = '%s'", owner);

    Results results = sqlHelper.queryResults(selectQuery);

    //todo fix as it's not getting the data correctly
    while (results.next()) {
      String resultsOwner = results.getString(1);
      String mineType = results.getString(2);
      String mineLocation = results.getString(3);
      String corner1 = results.getString(4);
      String corner2 = results.getString(5);
      String fullRegionMin = results.getString(6);
      String fullRegionMax = results.getString(7);
      String spawn = results.getString(8);
      double tax = results.get(9);
      int isOpen = results.get(10);
      int maxPlayers = results.get(11);
      int maxMineSize = results.get(12);
      String materials = results.getString(13);

      privateMines.getLogger().info("results: " + results);

      privateMines.getLogger().info("mineType: " + mineType);
      privateMines.getLogger().info("mineLocation: " + mineLocation);
      privateMines.getLogger().info("corner1: " + corner1);
      privateMines.getLogger().info("corner2: " + corner2);
      privateMines.getLogger().info("fullRegionMin: " + fullRegionMin);
      privateMines.getLogger().info("fullRegionMax: " + fullRegionMax);
      privateMines.getLogger().info("spawn: " + spawn);
      privateMines.getLogger().info("tax: " + tax);
      privateMines.getLogger().info("isOpen: " + isOpen);
      privateMines.getLogger().info("maxPlayers: " + maxPlayers);
      privateMines.getLogger().info("maxMineSize: " + maxMineSize);
      privateMines.getLogger().info("materials: " + materials);
    }
    sqlHelper.commit();
  }

  public static void updateMaterials(Mine mine) {
    MineData mineData = mine.getMineData();
    UUID owner = mineData.getMineOwner();

    Map<Material, Double> mats = mineData.getMaterials();
    Bukkit.broadcastMessage("mats " + mats);

    String command = String.format("UPDATE privatemines SET materials = "
        + "'%s' "
        + "WHERE owner = '%s';", mats, owner);
    sqlHelper.executeUpdate(command);
    sqlHelper.commit();
  }
}
