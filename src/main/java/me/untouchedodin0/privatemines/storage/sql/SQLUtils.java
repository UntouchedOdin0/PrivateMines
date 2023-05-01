package me.untouchedodin0.privatemines.storage.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import me.untouchedodin0.kotlin.mine.data.MineData;
import me.untouchedodin0.kotlin.mine.pregen.PregenMine;
import me.untouchedodin0.kotlin.mine.storage.PregenStorage;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.utils.Utils;
import me.untouchedodin0.privatemines.utils.world.MineWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import redempt.redlib.misc.LocationUtils;
import redempt.redlib.misc.Task;
import redempt.redlib.sql.SQLCache;
import redempt.redlib.sql.SQLHelper;
import redempt.redlib.sql.SQLHelper.Results;

public class SQLUtils {

  private static final PrivateMines privateMines = PrivateMines.getPrivateMines();
  private static final SQLHelper sqlHelper = privateMines.getSqlHelper();
  private static List<PregenMine> pregenMines = new ArrayList<>();
  static PregenStorage pregenStorage = privateMines.getPregenStorage();

  public static Location getCurrentLocation() {
    MineWorldManager mineWorldManager = PrivateMines.getPrivateMines().getMineWorldManager();
    SQLHelper sqlHelper = privateMines.getSqlHelper();
    Results results = sqlHelper.queryResults("SELECT * FROM privatemines;");
    var ref = new Object() {
      Location location = null;
    };

    if (results.isEmpty()) {
      return mineWorldManager.getDefaultLocation();
    } else {
      results.forEach(results1 -> ref.location = LocationUtils.fromString(results1.getString(3)));
      return ref.location;
    }
  }

  public static void insert(Mine mine) {
    SQLHelper sqlHelper = privateMines.getSqlHelper();
    MineData mineData = mine.getMineData();
    MineType mineType = mineData.getMineType();
    String owner = mineData.getMineOwner().toString();

    String insertQuery = String.format(
        "INSERT INTO privatemines (owner, mineType, mineLocation, corner1, corner2,"
            + " fullRegionMin, fullRegionMax, spawn, tax, isOpen, maxPlayers, maxMineSize, materials) "
            + "VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s', '%s', %f, %b, %d, %d, '%s');"
            + "ON CONFLICT IGNORE;", owner, mineType.getName(),
        LocationUtils.toString(mineData.getMineLocation()),
        LocationUtils.toString(mineData.getMinimumMining()),
        LocationUtils.toString(mineData.getMaximumMining()),
        LocationUtils.toString(mineData.getMinimumFullRegion()),
        LocationUtils.toString(mineData.getMaximumFullRegion()),
        LocationUtils.toString(mine.getSpawnLocation()), mineData.getTax(), mineData.isOpen(),
        mineData.getMaxPlayers(), mineData.getMaxMineSize(),
        Utils.mapToString(Objects.requireNonNull(mineType.getMaterials())));

    Task.asyncDelayed(() -> sqlHelper.executeUpdate(insertQuery));
  }

  public static void replace(Mine mine) {
    SQLHelper sqlHelper = privateMines.getSqlHelper();
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
            + "materials = '%s';", owner, mineType.getName(),
        LocationUtils.toString(mineData.getMineLocation()),
        LocationUtils.toString(mineData.getMinimumMining()),
        LocationUtils.toString(mineData.getMaximumMining()),
        LocationUtils.toString(mineData.getMinimumFullRegion()),
        LocationUtils.toString(mineData.getMaximumFullRegion()),
        LocationUtils.toString(mine.getSpawnLocation()), mineData.getTax(), mineData.isOpen(),
        mineData.getMaxPlayers(), mineData.getMaxMineSize(),
        Utils.mapToString(mineData.getMaterials()));

    delete(mine);
    Task.asyncDelayed(() -> {
      sqlHelper.executeUpdate(dropQuery);
      sqlHelper.executeUpdate(insertQuery);
    }, 20L);
  }

  public static void update(Mine mine) {
    MineData mineData = mine.getMineData();
    MineType mineType = mineData.getMineType();
    UUID owner = mineData.getMineOwner();
    Location minMining = mineData.getMinimumMining();
    Location maxMining = mineData.getMaximumMining();
    Location fullRegionMin = mineData.getMinimumFullRegion();
    Location fullRegionMax = mineData.getMaximumFullRegion();
    boolean isOpen = mineData.isOpen();
    int open;

    if (!isOpen) {
      open = 0;
    } else {
      open = 1;
    }

    SQLHelper sqlHelper = privateMines.getSqlHelper();
    String command = String.format(
        "UPDATE privatemines SET mineType = '%s', corner1 = '%s', corner2 = '%s', fullRegionMin = '%s', fullRegionMax = '%s', isOpen = '%d', materials = '%s' WHERE owner = '%s';",
        mineType.getName(), LocationUtils.toString(minMining), LocationUtils.toString(maxMining),
        LocationUtils.toString(fullRegionMin), LocationUtils.toString(fullRegionMax), open,
        Utils.mapToString(mineData.getMaterials()), owner);
    sqlHelper.executeUpdate(command);
  }

  public static void updateCache(Mine mine) {
    SQLHelper sqlHelper = privateMines.getSqlHelper();

    MineData mineData = mine.getMineData();
    MineType mineType = mineData.getMineType();
    UUID owner = mineData.getMineOwner();
    Location location = mineData.getMineLocation();
    Location minMining = mineData.getMinimumMining();
    Location maxMining = mineData.getMaximumMining();

    Map<String, SQLCache> caches = privateMines.getCaches();

    SQLCache ownerCache = caches.get("owner");
    SQLCache mineTypeCache = caches.get("mineType");
    SQLCache mineLocationCache = caches.get("mineLocation");
    SQLCache corner1Cache = caches.get("corner1");
    SQLCache corner2Cache = caches.get("corner2");

    ownerCache.update(owner.toString());
    mineTypeCache.update(mineType.getName());
    mineLocationCache.update(LocationUtils.toString(location));
    corner1Cache.update(LocationUtils.toString(minMining));
    corner2Cache.update(LocationUtils.toString(maxMining));

    Task.asyncDelayed(sqlHelper::flushAllCaches);
  }

  public static void delete(Mine mine) {
    SQLHelper sqlHelper = privateMines.getSqlHelper();
    MineData mineData = mine.getMineData();
    String owner = mineData.getMineOwner().toString();
    String dropQuery = String.format("DELETE FROM privatemines WHERE owner = '%s'", owner);

    Task.asyncDelayed(() -> sqlHelper.executeUpdate(dropQuery));
  }

  public static void updateMaterials(Mine mine) {
    SQLHelper sqlHelper = privateMines.getSqlHelper();
    MineData mineData = mine.getMineData();
    UUID owner = mineData.getMineOwner();

    Map<Material, Double> mats = mineData.getMaterials();

    String command = String.format(
        "UPDATE privatemines SET materials = " + "'%s' " + "WHERE owner = '%s';", mats, owner);
    sqlHelper.executeUpdate(command);
  }

  public static void insertPregen(PregenMine pregenMine) {
    Connection connection = sqlHelper.getConnection();
    SQLHelper sqlHelper = new SQLHelper(connection);

//    Bukkit.broadcastMessage("connection " + sqlHelper.getConnection());
//    Bukkit.broadcastMessage("connection2 " + PrivateMines.getPrivateMines().getSqlHelper().getConnection());
    Bukkit.broadcastMessage("sql lite " + privateMines.getSqlite());
    Bukkit.broadcastMessage("connection " + connection);
    Bukkit.broadcastMessage("sql helper 2 " + sqlHelper);

    String insert = String.format(
        "INSERT INTO pregenmines (location, min_mining, max_mining, spawn, min_full, max_full) "
            + "VALUES ('%s', '%s', '%s', '%s', '%s', '%s');",
        LocationUtils.toString(Objects.requireNonNull(pregenMine.getLocation())),
        LocationUtils.toString(Objects.requireNonNull(pregenMine.getLowerRails())),
        LocationUtils.toString(Objects.requireNonNull(pregenMine.getUpperRails())),
        LocationUtils.toString(Objects.requireNonNull(pregenMine.getSpawnLocation())),
        LocationUtils.toString(Objects.requireNonNull(pregenMine.getFullMin())),
        LocationUtils.toString(Objects.requireNonNull(pregenMine.getFullMax())));
    sqlHelper.executeUpdate(insert);
  }

  public static void loadPregens() {
    Connection connection = sqlHelper.getConnection();
    SQLHelper sqlHelper = new SQLHelper(connection);
    Results results = sqlHelper.queryResults("SELECT * FROM pregenmines;");

    try {
      results.forEach(results1 -> {
        PregenMine pregenMine = new PregenMine();
        Location location = LocationUtils.fromString(results1.getString(1));
        Location minMining = LocationUtils.fromString(results1.getString(2));
        Location maxMining = LocationUtils.fromString(results1.getString(3));
        Location spawn = LocationUtils.fromString(results1.getString(4));
        Location minFull = LocationUtils.fromString(results1.getString(5));
        Location maxFull = LocationUtils.fromString(results1.getString(6));

        pregenMine.setLocation(location);
        pregenMine.setLowerRails(minMining);
        pregenMine.setUpperRails(maxMining);
        pregenMine.setSpawnLocation(spawn);
        pregenMine.setFullMin(minFull);
        pregenMine.setFullMax(maxFull);
        pregenStorage.addMine(pregenMine);
      });
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static void claim(Location location) {
    Connection connection = sqlHelper.getConnection();
    SQLHelper sqlHelper = new SQLHelper(connection);
    String loc = LocationUtils.toString(location);

    sqlHelper.executeUpdate(String.format("DELETE FROM pregenmines WHERE location = '%s';", loc));
  }

  public static void getPregen() {
    SQLHelper sqlHelper = privateMines.getSqlHelper();
    Connection connection = sqlHelper.getConnection();
    try {
      Statement statement = connection.createStatement();
      String sql = "SELECT * FROM pregenmines";
      ResultSet resultSet = statement.executeQuery(sql);
      Bukkit.broadcastMessage(String.valueOf(resultSet));

      while (resultSet.next()) {
        Location location = LocationUtils.fromString(resultSet.getString("location"));
        Location minMining = LocationUtils.fromString(resultSet.getString("min_mining"));
        Location maxMining = LocationUtils.fromString(resultSet.getString("max_mining"));
        Location spawn = LocationUtils.fromString(resultSet.getString("spawn"));
        Location minFull = LocationUtils.fromString(resultSet.getString("min_full"));
        Location maxFull = LocationUtils.fromString(resultSet.getString("max_full"));

        Bukkit.broadcastMessage("" + location);
        Bukkit.broadcastMessage("" + minMining);
        Bukkit.broadcastMessage("" + maxMining);
        Bukkit.broadcastMessage("" + spawn);
        Bukkit.broadcastMessage("" + minFull);
        Bukkit.broadcastMessage("" + maxFull);

//        String minMining = resultSet.getString("min_mining");
//        String maxMining = resultSet.getString("max_mining");
//        String spawn = resultSet.getString("spawn");
//        String minFull = resultSet.getString("min_full");
//        String maxFull = resultSet.getString("max_full");

//        Bukkit.broadcastMessage("location " + location);
      }
      connection.close();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }

//    Results results = sqlHelper.queryResults("SELECT * FROM pregenmines;");

//    results.forEach(results1 -> {
//
//    });
  }
}
