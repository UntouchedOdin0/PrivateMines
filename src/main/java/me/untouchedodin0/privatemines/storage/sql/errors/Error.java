package me.untouchedodin0.privatemines.storage.sql.errors;

import java.util.logging.Level;
import me.untouchedodin0.privatemines.PrivateMines;

public class Error {

  public static void execute(PrivateMines privateMines, Exception ex) {
    privateMines.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
  }

  public static void close(PrivateMines privateMines, Exception ex) {
    privateMines.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
  }
}