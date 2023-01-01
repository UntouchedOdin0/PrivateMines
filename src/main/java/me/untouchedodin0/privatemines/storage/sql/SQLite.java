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

package me.untouchedodin0.privatemines.storage.sql;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class SQLite extends Database {

  String databaseName = "privatemines";

//    public String SQLiteCreateTokensTable = "CREATE TABLE IF NOT EXISTS " + databaseName + " (" + // make sure to put your table name in here too.
//            "`mineOwner` text NOT NULL," + // This creates the different colums you will save data too. varchar(32) Is a string, int = integer
//            "`mineType` text NOT NULL," +
//            "`mineLocation` text NOT NULL," +
//            "`corner1` text NOT NULL," +
//            "`corner2` text NOT NULL," +
//            "`fullRegionMin` text NOT NULL," +
//            "`fullRegionMax` text NOT NULL," +
//            "`spawn` text NOT NULL," +
//            "`tax` double NOT NULL," +
//            "`isOpen` boolean NOT NULL," +
//            "PRIMARY KEY (`mineOwner`)" +  // This is creating 3 colums Player, Kills, Total. Primary key is what you are going to use as your indexer. Here we want to use player so
//            ");"; // we can search by player, and get kills and total. If you some how were searching kills it would provide total and player.


  // SQL creation stuff, You can leave the blow stuff untouched.
  public Connection getSQLConnection() {
    File dataFolder = new File(privateMines.getDataFolder(), databaseName + ".db");
    if (!dataFolder.exists()) {
      try {
        boolean created = dataFolder.createNewFile();
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
      s.executeUpdate("""
          CREATE TABLE IF NOT EXISTS `privatemines` (
          `owner` TEXT NOT NULL,
          `mineType` TEXT,
          `corner1` TEXT,
          `corner2` TEXT,
          `fullMin` TEXT,
          `fullMax` TEXT,
          `spawn` TEXT,
          `open` BOOLEAN,
          );""");
//            s.executeUpdate("CREATE TABLE IF NOT EXISTS privatemines (`owner` TEXT, `data` TEXT);");
//            s.executeUpdate(SQLiteCreateTokensTable);
      s.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    initialize();
  }
}