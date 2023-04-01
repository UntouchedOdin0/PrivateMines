package me.untouchedodin0.privatemines.storage.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.storage.sql.errors.Error;

public abstract class Database {
  PrivateMines privateMines = PrivateMines.getPrivateMines();
  Connection connection;
  String databaseName = "privatemines";

  public abstract Connection getSQLConnection();
//  public void initialize() {
//    connection = getSQLConnection();
//    try {
//      PreparedStatement preparedStatement = connection.prepareStatement(
//          "SELECT * FROM " + databaseName + " WHERE owner = ?");
//      ResultSet resultSet = preparedStatement.executeQuery();
//      close(preparedStatement, resultSet);
//
//    } catch (SQLException ex) {
//      privateMines.getLogger().log(Level.SEVERE, "Unable to retrieve connection", ex);
//    }
//  }

  public void close(PreparedStatement preparedStatement, ResultSet resultSet) {
    try {
      if (preparedStatement != null) {
        preparedStatement.close();
      }
      if (resultSet != null) {
        resultSet.close();
      }
    } catch (SQLException ex) {
      Error.close(privateMines, ex);
    }
  }
}
