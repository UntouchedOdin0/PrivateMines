package me.untouchedodin0.privatemines.storage.sql;

import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.storage.sql.errors.Error;
import me.untouchedodin0.privatemines.storage.sql.errors.Errors;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public abstract class Database {

    public int tokens = 0;
    PrivateMines privateMines = PrivateMines.getPrivateMines();
    Connection connection;
    String databaseName = "privatemines";


    public abstract Connection getSQLConnection();

    public abstract void load();

    public void initialize() {
        connection = getSQLConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + databaseName + " WHERE owner = ?");
            ResultSet resultSet = preparedStatement.executeQuery();
            close(preparedStatement, resultSet);

        } catch (SQLException ex) {
            privateMines.getLogger().log(Level.SEVERE, "Unable to retrieve connection", ex);
        }
    }

    // These are the methods you can use to get things out of your database. You of course can make new ones to return different things in the database.
    // This returns the number of people the player killed.
    public Integer getTokens(String string) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM " + databaseName + " WHERE player = '" + string + "';");

            rs = ps.executeQuery();
            while (rs.next()) {
                if (rs.getString("player").equalsIgnoreCase(string.toLowerCase())) { // Tell database to search for the player you sent into the method. e.g getTokens(sam) It will look for sam.
                    return rs.getInt("kills"); // Return the players ammount of kills. If you wanted to get total (just a random number for an example for you guys) You would change this to total!
                }
            }
        } catch (SQLException ex) {
            privateMines.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                privateMines.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return 0;
    }

    // Exact same method here, Except as mentioned above i am looking for total!
    public Integer getTotal(String string) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet;
        try {
            connection = getSQLConnection();
            preparedStatement = connection.prepareStatement("SELECT * FROM " + databaseName + " WHERE player = '" + string + "';");

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getString("player").equalsIgnoreCase(string.toLowerCase())) {
                    return resultSet.getInt("total");
                }
            }
        } catch (SQLException ex) {
            privateMines.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException ex) {
                privateMines.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
        return 0;
    }

    // Now we need methods to save things to the database
    public void setTokens(UUID uuid, Integer tokens, Integer total) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = getSQLConnection();
            preparedStatement = connection.prepareStatement("REPLACE INTO " + databaseName + " (player,kills,total) VALUES(?,?,?)"); // IMPORTANT. In SQLite class, We made 3 colums. player, Kills, Total.
            preparedStatement.setString(1, uuid.toString());                                             // YOU MUST put these into this line!! And depending on how many
            // colums you put (say you made 5) All 5 need to be in the brackets
            // Seperated with comma's (,) AND there needs to be the same amount of
            // question marks in the VALUES brackets. Right now i only have 3 colums
            // So VALUES (?,?,?) If you had 5 colums VALUES(?,?,?,?,?)

            preparedStatement.setInt(2, tokens); // This sets the value in the database. The colums go in order. Player is ID 1, kills is ID 2, Total would be 3 and so on. you can use
            // setInt, setString and so on. tokens and total are just variables sent in, You can manually send values in as well. p.setInt(2, 10) <-
            // This would set the players kills instantly to 10. Sorry about the variable names, It sets their kills to 10 i just have the variable called
            // Tokens from another plugin :/
            preparedStatement.setInt(3, total);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            privateMines.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
        } finally {
            try {
                if (preparedStatement != null)
                    preparedStatement.close();
                if (connection != null)
                    connection.close();
            } catch (SQLException ex) {
                privateMines.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
            }
        }
    }

    public void close(PreparedStatement preparedStatement, ResultSet resultSet) {
        try {
            if (preparedStatement != null)
                preparedStatement.close();
            if (resultSet != null)
                resultSet.close();
        } catch (SQLException ex) {
            Error.close(privateMines, ex);
        }
    }
}