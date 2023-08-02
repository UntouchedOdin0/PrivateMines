package me.untouchedodin0.privatemines.utils.commands;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandHandler {
  private final Map<String, CommandInfo> commands;

  public CommandHandler(JavaPlugin plugin) {
    commands = new HashMap<>();

    // Register commands by scanning for annotated classes
    scanAndRegisterCommands(plugin);
  }

  private void scanAndRegisterCommands(JavaPlugin plugin) {
    // TODO: Implement class scanning and registration here
    // Iterate through classes in your plugin package
    // Check if they have the @Command annotation
    // Check for methods with @SubCommand annotations
    // Register the commands accordingly in the commands map
  }

  public boolean executeCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("Only players can use this command.");
      return true;
    }

    Player player = (Player) sender;
    String commandName = cmd.getName().toLowerCase();

    CommandInfo commandInfo = commands.get(commandName);
    if (commandInfo != null) {
      if (args.length == 0 || commandInfo.getSubCommands().isEmpty()) {
        executeMethod(commandInfo.getMainCommand(), player, cmd, label, args);
      } else {
        String subCommandName = args[0].toLowerCase();
        Method subCommandMethod = commandInfo.getSubCommand(subCommandName);
        if (subCommandMethod != null) {
          String[] subCommandArgs = new String[args.length - 1];
          System.arraycopy(args, 1, subCommandArgs, 0, args.length - 1);
          executeMethod(subCommandMethod, player, cmd, label, subCommandArgs);
        } else {
          player.sendMessage("Unknown sub-command. Type /help for help.");
        }
      }
      return true;
    }

    player.sendMessage("Unknown command. Type /help for help.");
    return true;
  }

  private void executeMethod(Method method, Player player, Command cmd, String label, String[] args) {
    try {
      method.invoke(null, player, cmd, label, args);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}