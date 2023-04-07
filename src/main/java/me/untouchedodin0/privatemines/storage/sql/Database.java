package me.untouchedodin0.privatemines.storage.sql;

import java.sql.Connection;

public abstract class Database {
  Connection connection;

  public abstract Connection getSQLConnection();
}
