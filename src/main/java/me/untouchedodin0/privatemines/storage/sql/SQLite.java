package me.untouchedodin0.privatemines.storage.sql;

import me.untouchedodin0.privatemines.utils.Utils;

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
        if (!dataFolder.exists()){
            try {
                boolean created = dataFolder.createNewFile();
            } catch (IOException e) {
                privateMines.getLogger().log(Level.SEVERE, "File write error: "+ databaseName + ".db");
            }
        }
        try {
            if(connection != null && !connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            privateMines.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            privateMines.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }

    public void load() {
        privateMines.getLogger().log(Level.INFO, "Loading SQLite database...");
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
//            s.executeUpdate(SQLiteCreateTokensTable);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }
}