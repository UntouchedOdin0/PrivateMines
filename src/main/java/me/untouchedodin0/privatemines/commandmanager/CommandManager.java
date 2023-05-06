package me.untouchedodin0.privatemines.commandmanager;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.SimplePluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandManager implements CommandExecutor, TabCompleter {

  private CommandMap cmdMap;

  private static final List<BaseCommand> commands = new ArrayList<>();

  public static void addBaseCommand(BaseCommand command) {
    commands.add(command);
  }


  public void setupCommands(JavaPlugin plugin) {
    for (BaseCommand command : commands) {
      try {
        final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");
        bukkitCommandMap.setAccessible(true);

        this.cmdMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

      } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ignored) {
      }

      cmdMap.register(command.getName(), command);
    }
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

    ArrayList<String> subCommands = new ArrayList<>();

    for (Command main : commands) {
      if (command.getLabel().equals(main.getName())) {
        if (args.length != 1) {
          return null;
        }
      }
    }
    return subCommands;
  }

  public static void unregisterCommand(String label) {
    try {
      SimplePluginManager simplePluginManager = (SimplePluginManager) Bukkit.getServer()
          .getPluginManager();

      Field field = SimplePluginManager.class.getDeclaredField("commandMap");
      field.setAccessible(true);

      SimpleCommandMap simpleCommandMap = (SimpleCommandMap) field.get(simplePluginManager);
      Collection<Command> collection = simpleCommandMap.getCommands();
      Map<String, Command> map = new HashMap<>();

      for (Command command : collection) {
        if (!command.getName().equals(label)) {
          map.put(command.getName(), command);
        }
      }
      simpleCommandMap.clearCommands();
      map.keySet().forEach(key -> simpleCommandMap.register(key, map.get(key)));

      field.setAccessible(false);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void removeAlias(String command, String aliasToRemove) {
    try {
      SimplePluginManager simplePluginManager = (SimplePluginManager) Bukkit.getServer()
          .getPluginManager();

      Field field = SimplePluginManager.class.getDeclaredField("commandMap");
      field.setAccessible(true);

      SimpleCommandMap simpleCommandMap = (SimpleCommandMap) field.get(simplePluginManager);

      Collection<Command> collection = simpleCommandMap.getCommands();
      Map<String, Command> map = new HashMap<>();

      for (Command command1 : collection) {

        if (command1.getName().equals(command)) {
          List<String> aliases = command1.getAliases();
          aliases.remove(aliasToRemove);
          command1.setAliases(aliases);
        }
        map.put(command1.getName(), command1);
      }
      simpleCommandMap.clearCommands();
      map.keySet().forEach(key -> simpleCommandMap.register(key, map.get(key)));
      field.setAccessible(false);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}