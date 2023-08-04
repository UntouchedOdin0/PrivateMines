package me.untouchedodin0.privatemines.utils.commands;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import me.untouchedodin0.privatemines.utils.commands.annotations.Command;
import org.bukkit.command.CommandSender;

public class CommandInfo {

  private final Class<?> commandClass;
  private final String name;

  public CommandInfo(Class<?> commandClass) {
    this.commandClass = commandClass;
    this.name = commandClass.getAnnotation(Command.class).value();
  }

  public String getName() {
    return name;
  }

  public void executeMain(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
    try {
      Method method = commandClass.getDeclaredMethod("defaultMethod");
      method.setAccessible(true);
      method.invoke(null);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  public void executeSub(CommandSender sender, String subCommand, org.bukkit.command.Command cmd, String label, String[] args) {
    try {
      // Implement logic to find the appropriate sub-command method and invoke it
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public boolean hasSubCommand(String subCommand) {
    // Implement logic to check if the sub-command exists
    return false;
  }
}

//  private final Method mainCommand;
//  private final Map<String, Method> subCommands;
//
//  public CommandInfo(Method mainCommand) {
//    this.mainCommand = mainCommand;
//    this.subCommands = new HashMap<>();
//  }
//
//  public void addSubCommand(String subCommandName, Method subCommandMethod) {
//    subCommands.put(subCommandName.toLowerCase(), subCommandMethod);
//  }
//
//  public Method getMainCommand() {
//    return mainCommand;
//  }
//
//  public Method getSubCommand(String subCommandName) {
//    return subCommands.get(subCommandName.toLowerCase());
//  }
//
//  public Map<String, Method> getSubCommands() {
//    return subCommands;
//  }
