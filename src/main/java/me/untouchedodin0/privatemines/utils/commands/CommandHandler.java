package me.untouchedodin0.privatemines.utils.commands;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.utils.commands.annotations.Command;
import me.untouchedodin0.privatemines.utils.commands.annotations.DefaultCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandHandler {
  private final Map<String, CommandInfo> commands;
  private CommandMap commandMap;

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

  public void registerCommand(Class<?> clazz) {
//    SimpleCommandMap commandMap = getCommandMap();
    PluginCommand pluginCommand = null;
    String commandName = null;

    Command command = clazz.getAnnotation(Command.class);
    Annotation[] clazzAnnotations = clazz.getAnnotations();

    commandName = command.value();

    System.out.println("command name up " + commandName);
    Method[] methods = clazz.getDeclaredMethods();
    Constructor<PluginCommand> c = null;
    try {
      c = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
      c.setAccessible(true);
      pluginCommand = c.newInstance(commandName, PrivateMines.getPrivateMines());
      register(pluginCommand);

//      commandMap.register("privatemines", pluginCommand);

//      pluginCommand = c.newInstance("", PrivateMines.getPrivateMines());

      System.out.println("class annotations " + clazzAnnotations);
      System.out.println("c? " + c);
      System.out.println("pluginCommand " + pluginCommand);
      System.out.println("command name " + commandName);

    } catch (NoSuchMethodException | InvocationTargetException | InstantiationException |
             IllegalAccessException e) {
      throw new RuntimeException(e);
    }

    for (Method method : methods) {
      Annotation[] annotations = method.getAnnotations();

      for (Annotation annotation : annotations) {
        if (annotation instanceof  DefaultCommand) {
          String value = ((DefaultCommand) annotation).value();
        }
      }
    }
//    for (Method method : methods) {
//      Annotation[] annotations = method.getAnnotations();
//
//      for (Annotation annotation : annotations) {
//       if (annotation instanceof DefaultCommand) {
//         String value = ((DefaultCommand) annotation).value();
//         System.out.println("Found command " + value);
//       }
//
////        if (annotation instanceof SubCommand) {
////          String value = ((SubCommand) annotation).value();
////
////          System.out.println("Found sub command '" + value + "' at method: " + method.getName());
////        } else if (annotation instanceof CommandPermission) {
////          String permissionValue = ((CommandPermission) annotation).value();
////
////        }
//      }
//    }
  }

  public boolean executeCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("Only players can use this command.");
      return true;
    }

    Player player = (Player) sender;
    String commandName = cmd.getName().toLowerCase();

    CommandInfo commandInfo = commands.get(commandName);
    commandInfo.executeMain(sender, cmd, label, args);

//    if (commandInfo != null) {
//      if (args.length == 0 || commandInfo.getSubCommands().isEmpty()) {
//        executeMethod(commandInfo.getMainCommand(), player, cmd, label, args);
//      } else {
//        String subCommandName = args[0].toLowerCase();
//        Method subCommandMethod = commandInfo.getSubCommand(subCommandName);
//        if (subCommandMethod != null) {
//          String[] subCommandArgs = new String[args.length - 1];
//          System.arraycopy(args, 1, subCommandArgs, 0, args.length - 1);
//          executeMethod(subCommandMethod, player, cmd, label, subCommandArgs);
//        } else {
//          player.sendMessage("Unknown sub-command. Type /help for help.");
//        }
//      }
//      return true;
//    }

    player.sendMessage("Unknown command. Type /help for help.");
    return true;
  }

  private void executeMethod(Method method, Player player, org.bukkit.command.Command cmd, String label, String[] args) {
    try {
      method.invoke(null, player, cmd, label, args);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static void register(PluginCommand pluginCommand) {
    Field field;
    try {
      field = Bukkit.getServer().getClass().getDeclaredField("commandMap");

      field.setAccessible(true);

      CommandMap commandMap = (CommandMap) field.get(Bukkit.getServer());
      commandMap.register("privatemines", pluginCommand);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }

  }
//  public static SimpleCommandMap getCommandMap() {
//    try {
//      Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
//      field.setAccessible(true);
////      Field field = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
////      field.setAccessible(true);
//
//
//      return (SimpleCommandMap) field.get(Bukkit.getPluginManager());
//    } catch (NoSuchFieldException | IllegalAccessException e) {
//      throw new RuntimeException(e);
//    }
//  }
}