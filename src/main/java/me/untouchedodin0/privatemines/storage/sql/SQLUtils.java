package me.untouchedodin0.privatemines.storage.sql;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import me.untouchedodin0.kotlin.mine.data.MineData;
import me.untouchedodin0.kotlin.mine.pregen.PregenMine;
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
        mineData.getMaxPlayers(), mineData.getMaxMineSize(), "{SPONGE=1.0, STONE=1.0, DIRT=1.0}");
    sqlHelper.executeUpdate(insertQuery);
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
    Map<Material, Double> materials = mineData.getMaterials();

    SQLHelper sqlHelper = privateMines.getSqlHelper();
    String command = String.format(
        "UPDATE privatemines SET mineType = '%s', corner1 = '%s', corner2 = '%s', fullRegionMin = '%s', fullRegionMax = '%s' WHERE owner = '%s';",
        mineType.getName(), LocationUtils.toString(minMining), LocationUtils.toString(maxMining),
        LocationUtils.toString(fullRegionMin), LocationUtils.toString(fullRegionMax), owner);
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
    Location fullRegionMin = mineData.getMinimumFullRegion();
    Location fullRegionMax = mineData.getMaximumFullRegion();
    Map<Material, Double> materials = mineData.getMaterials();

    /**
     *     List<String> cacheNames = List.of("owner", "mineType", "mineLocation", "corner1", "corner2");
     */
    Map<String, SQLCache> caches = privateMines.getCaches();

    SQLCache ownerCache = caches.get("owner");
    SQLCache mineTypeCache = caches.get("mineType");
    SQLCache mineLocationCache = caches.get("mineLocation");
    SQLCache corner1Cache = caches.get("corner1");
    SQLCache corner2Cache = caches.get("corner2");

    // value - name
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
    SQLHelper sqlHelper = privateMines.getSqlHelper();

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
}
