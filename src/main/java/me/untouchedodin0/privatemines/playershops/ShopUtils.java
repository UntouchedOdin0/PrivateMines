package me.untouchedodin0.privatemines.playershops;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import me.untouchedodin0.kotlin.mine.data.MineData;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mine.Mine;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import redempt.redlib.misc.Task;
import redempt.redlib.sql.SQLHelper;
import redempt.redlib.sql.SQLHelper.Results;

public class ShopUtils {

  private static final PrivateMines privateMines = PrivateMines.getPrivateMines();

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

      Bukkit.broadcastMessage(
          String.format("updating item %s in %s's mine to %f", material.name(), uuid,
              price));

      Task.asyncDelayed(() -> sqlHelper.executeUpdate(insertQuery));
    }
  }

  public static void setDefaultPrices(Mine mine) {
    SQLHelper sqlHelper = privateMines.getSqlHelper();
    MineData mineData = mine.getMineData();
    String uuid = mineData.getMineOwner().toString();

    // Log the query for debugging purposes
    for (Material material : Objects.requireNonNull(mineData.getMineType().getMaterials())
        .keySet()) {
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


  public static void addItem(UUID uuid, ShopItem shopItem) {
    SQLHelper sqlHelper = privateMines.getSqlHelper();
    MineStorage mineStorage = privateMines.getMineStorage();
    Mine mine = mineStorage.get(uuid);

    if (mine != null) {
      MineData mineData = mine.getMineData();
      String ownerUUID = uuid.toString();
      String materialName = shopItem.getItem().name();
      double taxRate = mineData.getTax() / 100.0;
      double finalPrice = shopItem.getPrice() * (1 + taxRate);

      Task.asyncDelayed(() -> {
        try {
          // Check if the item already exists
          String checkQuery = "SELECT quantity FROM shops WHERE owner = ? AND item = ?";
          long currentQuantity = 0L;
          boolean itemExists = false;

          try (PreparedStatement statement = sqlHelper.getConnection()
              .prepareStatement(checkQuery)) {
            statement.setString(1, ownerUUID);
            statement.setString(2, materialName);

            try (ResultSet resultSet = statement.executeQuery()) {
              if (resultSet.next()) {
                currentQuantity = resultSet.getLong("quantity"); // Retrieves BIGINT as long
                itemExists = true;
              }
            }
          }

          // Calculate the new quantity and check for overflow
          long newQuantity;
          if (shopItem.getQuantity() > 0 && (Long.MAX_VALUE - currentQuantity < shopItem.getQuantity())) {
            // Overflow detected
            newQuantity = Long.MAX_VALUE; // Cap to max value
          } else {
            newQuantity = currentQuantity + shopItem.getQuantity();
          }

          // Update or insert the item in the database
          String updateQuery;
          if (itemExists) {
            updateQuery = "UPDATE shops SET quantity = ?, price = ? WHERE owner = ? AND item = ?";
          } else {
            updateQuery = "INSERT INTO shops (owner, item, quantity, price) VALUES (?, ?, ?, ?)";
          }

          try (PreparedStatement statement = sqlHelper.getConnection()
              .prepareStatement(updateQuery)) {
            statement.setLong(1, newQuantity);
            statement.setDouble(2, finalPrice);
            statement.setString(3, ownerUUID);
            statement.setString(4, materialName);

            statement.executeUpdate();
          }
        } catch (Exception e) {
          Bukkit.getLogger().severe("Error updating item: " + e.getMessage());
          e.printStackTrace();
        }
      });
    } else {
      Bukkit.getLogger().warning("Mine not found for UUID: " + uuid);
    }
  }

  public static void removeItem(UUID uuid, Material material, long quantity) {
    SQLHelper sqlHelper = privateMines.getSqlHelper();
    MineStorage mineStorage = privateMines.getMineStorage();
    Mine mine = mineStorage.get(uuid);

    if (mine != null) {
      String ownerUUID = uuid.toString();
      String materialName = material.name();

      Task.asyncDelayed(() -> {
        int maxRetries = 5;
        int retryDelay = 1000; // 1 second delay between retries

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
          try {
            // Check if the item exists and get the current quantity
            String checkQuery = "SELECT quantity FROM shops WHERE owner = ? AND item = ?";
            long currentQuantity = 0L;

            try (PreparedStatement statement = sqlHelper.getConnection()
                .prepareStatement(checkQuery)) {
              statement.setString(1, ownerUUID);
              statement.setString(2, materialName);

              try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                  currentQuantity = resultSet.getLong("quantity"); // Retrieves BIGINT as long
                }
              }
            }

            // Calculate the new quantity and check for underflow
            long newQuantity = Math.max(currentQuantity - quantity, 0);

            // Update the quantity in the database
            String updateQuery = "UPDATE shops SET quantity = ? WHERE owner = ? AND item = ?";
            sqlHelper.executeUpdate(updateQuery, newQuantity, ownerUUID, materialName);
            break; // Exit the retry loop on success

          } catch (Exception e) {
            // Log the exception and retry if needed
            if (attempt < maxRetries) {
              try {
                Thread.sleep(retryDelay); // Wait before retrying
              } catch (InterruptedException ie) {
                Thread.currentThread().interrupt(); // Restore interrupted status
              }
            } else {
              throw new RuntimeException("Failed to remove item after " + maxRetries + " attempts",
                  e);
            }
          }
        }
      });
    }
  }

  public List<ShopItem> getShopItems(UUID uuid) {
    SQLHelper sqlHelper = privateMines.getSqlHelper();
    String ownerUUID = uuid.toString();
    List<ShopItem> shopItems = new ArrayList<>();

    Results results = sqlHelper.queryResults("SELECT * FROM shops WHERE owner=?;", ownerUUID);

    if (results == null) {
      return null;
    }

    results.forEach(result -> {
      String item = result.getString(3);
      long quantity = result.getLong(4);
      double price = result.get(5);
      double tax = result.get(6);

      Material material = Material.getMaterial(item);
      if (material != null) {
        ShopItem shopItem = new ShopItem(material, quantity, price, tax);
        shopItems.add(shopItem);
      }
    });
    return shopItems;
  }

  public void sellItems(UUID uuid, boolean includeTax) {
    SQLHelper sqlHelper = privateMines.getSqlHelper();
    String ownerUUID = uuid.toString();
    Map<Material, Long> shopItems = new HashMap<>();

    Results results = sqlHelper.queryResults("SELECT * FROM shops WHERE owner=?;", ownerUUID);

    if (results == null) {
      Bukkit.broadcastMessage("No results found.");
    }

    if (results != null) {
      results.forEach(result -> {
        String item = result.getString(3);
        long price = result.getLong(5);
        double tax = result.get(6);

        if (!includeTax) {
          shopItems.put(Material.getMaterial(item), price);
        } else {
          // This calculates the tax amount (how much is subtracted for tax purposes)
          double taxAmount = price * tax / 100.0;

          // This calculates the earner's amount (how much the seller receives after tax)
          double earnerAmount = price - taxAmount;

          Bukkit.getPlayer(uuid).sendMessage("You earnt $" + earnerAmount);
          Bukkit.getPlayer(uuid).sendMessage("You paid $" + taxAmount + " in taxes..");

          shopItems.put(Material.getMaterial(item), (long) taxAmount);
        }
      });
    }
  }
}

