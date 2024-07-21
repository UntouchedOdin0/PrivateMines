package me.untouchedodin0.privatemines.playershops;

import java.util.Objects;
import java.util.UUID;
import me.untouchedodin0.kotlin.mine.data.MineData;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mine.Mine;
import net.royawesome.jlibnoise.module.combiner.Min;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import redempt.redlib.misc.Task;
import redempt.redlib.sql.SQLHelper;
import redempt.redlib.sql.SQLHelper.Results;

public class ShopUtils {

  private static final PrivateMines privateMines = PrivateMines.getPrivateMines();
//  private SQLHelper sqlHelper = privateMines.getSqlHelper();

  public static void updatePrice(UUID uuid, Material material, double price) {
    SQLHelper sqlHelper = privateMines.getSqlHelper();
    MineStorage mineStorage = privateMines.getMineStorage();
    Mine mine = mineStorage.get(uuid);
    if (mine != null) {
      MineData mineData = mine.getMineData();

      String insertQuery = String.format(
          "INSERT INTO shops (owner, seller, item, quantity, price, tax) " +
              "VALUES ('%s', '%s', '%s', %d, %f, %f) " +
              "ON CONFLICT(owner, item) DO UPDATE SET price = excluded.price;",
          uuid, uuid, material.name(), 0, price, mineData.getTax());

//    String updateQuery = String.format(
//        "UPDATE shops SET price = %f WHERE owner = '%s' AND item = '%s';", price, uuid,
//        material.name());

      // Log the query for debugging purposes
//    System.out.println("Executing query: " + updateQuery);

      Bukkit.broadcastMessage(
          String.format("updating item %s in %s's mine to %f", material.name(), uuid.toString(),
              price));

      Task.asyncDelayed(() -> sqlHelper.executeUpdate(insertQuery));
    }
  }

  public static void setDefaultPrices(Mine mine) {
    SQLHelper sqlHelper = privateMines.getSqlHelper();
    MineData mineData = mine.getMineData();
    String uuid = mineData.getMineOwner().toString();

    // Log the query for debugging purposes
    for (Material material : Objects.requireNonNull(mineData.getMineType().getMaterials()).keySet()) {
      double price = Objects.requireNonNull(mineData.getMineType().getPrices()).get(material);
      double finalPrice = mineData.getTax() / 100 * price;
      double tax = mineData.getTax();

      String insertQuery = String.format(
          "INSERT INTO shops (owner, seller, item, quantity, price, tax) " +
              "VALUES ('%s', '%s', '%s', %d, %f, %f) " +
              "ON CONFLICT(owner, item) DO UPDATE SET price = excluded.price;",
          uuid, "", material.name(), 0, finalPrice, tax);

      // Log the query being executed
      System.out.println("Executing query: " + insertQuery);

      Task.asyncDelayed(() -> {
        try {
          sqlHelper.executeUpdate(insertQuery);
        } catch (Exception e) {
          // Print stack trace or log the exception
          e.printStackTrace();
          System.err.println("Error executing query: " + insertQuery);
        }
      });
    }
  }


  public static void addItem(UUID uuid, Material material, int quantity, double price) {
    SQLHelper sqlHelper = privateMines.getSqlHelper();
    MineStorage mineStorage = privateMines.getMineStorage();
    Mine mine = mineStorage.get(uuid);

    if (mine != null) {
      MineData mineData = mine.getMineData();
      String ownerUUID = uuid.toString();
      String materialName = material.name();
      double taxRate = mineData.getTax() / 100.0;
      double finalPrice = price * (1 + taxRate);

      Task.asyncDelayed(() -> {
        try {
          // Check if the item already exists
          String checkQuery = "SELECT quantity FROM shops WHERE owner = ? AND item = ?";
          Integer currentQuantity = sqlHelper.querySingleResult(checkQuery, ownerUUID, materialName);

          if (currentQuantity != null) {
            // Item exists, update the quantity and price
            int newQuantity = currentQuantity + quantity;
            String updateQuery = "UPDATE shops SET quantity = ?, price = ? WHERE owner = ? AND item = ?";
            sqlHelper.executeUpdate(updateQuery, newQuantity, finalPrice, ownerUUID, materialName);

            Bukkit.broadcastMessage("Updated item: " + materialName + " with new quantity: " + newQuantity);
          } else {
            // Item doesn't exist, insert a new row
            String insertQuery = "INSERT INTO shops (owner, seller, item, quantity, price, tax) VALUES (?, ?, ?, ?, ?, ?)";
            sqlHelper.executeUpdate(insertQuery, ownerUUID, ownerUUID, materialName, quantity, finalPrice, taxRate);

            Bukkit.broadcastMessage("Inserted new item: " + materialName + " with quantity: " + quantity);
          }
        } catch (Exception e) {
          e.printStackTrace();
          Bukkit.broadcastMessage("Error handling item: " + materialName);
        }
      });
    }
  }


//  public static void addItem(UUID uuid, Material material, int quantity, double price) {
//    MineStorage mineStorage = privateMines.getMineStorage();
//    Mine mine = mineStorage.get(uuid);
//
//    if (mine != null) {
//      MineData mineData = mine.getMineData();
//      String ownerUUID = uuid.toString();
//      String materialName = material.name();
//
//      // Query to check if the item with the specific owner and item already exists
//      String checkQuery = "SELECT quantity FROM shops WHERE owner = ? AND item = ?;";
//
//      String insertQuery = String.format(
//          "INSERT INTO shops (shop_owner, item, quantity, price) "
//              + "VALUES ('%s', '%s', %d, %f) "
//              + "ON CONFLICT(shop_owner, item) DO UPDATE SET price = excluded.price;",
//          uuid, material.name(), 0, 1.0);
//
//      // Log the check query for debugging purposes
//      Bukkit.broadcastMessage("Executing check query: " + checkQuery);
////      Bukkit.broadcastMessage("quantity before " + quantityBefore);
////      Bukkit.broadcastMessage("quantity after " + quantityBefore + quantity);
//
//      Task.asyncDelayed(() -> {
//        sqlHelper.executeUpdate(insertQuery);
////        sqlHelper.execute("UPDATE shops SET price=? WHERE item=? AND owner=?;",
////            price, "COBBLESTONE", uuid.toString());
////        sqlHelper.execute("UPDATE shops SET quantity=? WHERE item=? AND owner=?;",
////            quantity, "COBBLESTONE", uuid.toString());
//
//        int quantityBefore = sqlHelper.querySingleResult("SELECT quantity FROM shops WHERE owner = ? AND item = ?", ownerUUID, materialName);
//
//        // Item doesn't exist, insert a new row
////        String insertQuery = "INSERT INTO shops (owner, seller, item, quantity, price, tax) VALUES (?, ?, ?, ?, ?, ?)";
////        sqlHelper.execute(insertQuery, ownerUUID, ownerUUID, materialName, quantity,
////            (mineData.getTax() / 100) * price, 0.0); // Adjust tax value as needed
//
////        sqlHelper.executeUpdate("UPDATE shops SET quantity=? WHERE item=? AND price =? AND owner=?;",
////            quantityBefore + quantity, "COBBLESTONE", uuid.toString());
//
////        try {
////
////        }
////          Results results = sqlHelper.queryResults(checkQuery, ownerUUID, materialName);
////
////          if (results.next()) {
////            // Item exists, update the quantity and price
////            int currentQuantity = results.get(4);
////            int newQuantity = currentQuantity + quantity;
////
////            String updateQuery = "UPDATE shops SET quantity = ?, price = ? WHERE owner = ? AND item = ?";
////            sqlHelper.execute(updateQuery, newQuantity, (mineData.getTax() / 100) * price,
////                ownerUUID, materialName);
////
////            Bukkit.broadcastMessage(
////                "Updated item: " + materialName + " with new quantity: " + newQuantity);
////          } else {
////            // Item doesn't exist, insert a new row
////            String insertQuery = "INSERT INTO shops (owner, seller, item, quantity, price, tax) VALUES (?, ?, ?, ?, ?, ?)";
////            sqlHelper.execute(insertQuery, ownerUUID, ownerUUID, materialName, quantity,
////                (mineData.getTax() / 100) * price, 0.0); // Adjust tax value as needed
////
////            Bukkit.broadcastMessage(
////                "Inserted new item: " + materialName + " with quantity: " + quantity);
////          }
////
////          // Clean up the resources
////          results.close();
////        } catch (Exception e) {
////          throw new RuntimeException(e);
////        }
//      });
//    }
//  }

//  public static void addItem(UUID uuid, Material material, int quantity, double price) {
//    MineStorage mineStorage = privateMines.getMineStorage();
//
//    Mine mine = mineStorage.get(uuid);
//
//    if (mine != null) {
//      // Query to check if the item with the specific price already exists for the shop owner
//      String checkQuery = "SELECT quantity FROM shops WHERE owner = ? AND item = ? AND price = ?;";
//
//      // Get the material name
//      String materialName = material.name();
//
//      // Log the check query for debugging purposes
//      System.out.println("Executing check query: " + checkQuery);
//
//      Bukkit.broadcastMessage("materialName " + materialName);
//
//      Task.asyncDelayed(() -> {
//        int quantityBefore = sqlHelper.querySingleResult(
//            "SELECT quantity FROM shops WHERE item=? AND owner=?;", "COBBLESTONE",
//            uuid.toString());
//        sqlHelper.execute("UPDATE shops SET price=? WHERE item=? AND owner=?;", price,
//            "COBBLESTONE", uuid.toString());
//        sqlHelper.execute("UPDATE shops SET quantity=? WHERE item=? AND owner=?;",
//            quantityBefore + quantity, "COBBLESTONE", uuid.toString());
//
//        int quantityAdded = quantityBefore + quantity;
//
//        Bukkit.broadcastMessage("addItem quantity before " + quantityBefore);
//        Bukkit.broadcastMessage("addItem quantity added " + quantityAdded);
//      });
//    }
//  }
}

