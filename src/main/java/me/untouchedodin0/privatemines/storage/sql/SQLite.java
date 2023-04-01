package me.untouchedodin0.privatemines.storage.sql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import me.untouchedodin0.privatemines.PrivateMines;

public class SQLite extends Database {

  PrivateMines privateMines = PrivateMines.getPrivateMines();
  String databaseName = "privatemines";

  // SQL creation stuff, You can leave the blow stuff untouched.
  public Connection getSQLConnection() {
    File dataFolder = new File(privateMines.getDataFolder(), databaseName + ".db");
    if (!dataFolder.exists()) {
      try {
        boolean created = dataFolder.createNewFile();
        if (created) {
          privateMines.getLogger().info("Created the privatemines.db file successfully!");
        }
      } catch (IOException e) {
        privateMines.getLogger().log(Level.SEVERE, "File write error: " + databaseName + ".db");
      }
    }
    try {
      if (connection != null && !connection.isClosed()) {
        return connection;
      }
      Class.forName("org.sqlite.JDBC");
      connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
      return connection;
    } catch (SQLException ex) {
      privateMines.getLogger().log(Level.SEVERE, "SQLite exception on initialize", ex);
    } catch (ClassNotFoundException ex) {
      privateMines.getLogger()
          .log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
    }
    return null;
  }

  public void load() {
    privateMines.getLogger().log(Level.INFO, "Loading SQLite database...");
    connection = getSQLConnection();
    try {
      Statement s = connection.createStatement();
//      s.executeUpdate("""
//          CREATE TABLE IF NOT EXISTS `privatemines` (
//          `owner` TEXT NOT NULL,
//          `mineType` TEXT,
//          `corner1` TEXT,
//          `corner2` TEXT,
//          `fullMin` TEXT,
//          `fullMax` TEXT,
//          `spawn` TEXT,
//          `open` BOOLEAN
//          );""");
      s.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
//    initialize();
  }
}