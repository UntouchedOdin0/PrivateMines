package me.untouchedodin0.privatemines.utils.commands;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class CommandInfo {
  private final Method mainCommand;
  private final Map<String, Method> subCommands;

  public CommandInfo(Method mainCommand) {
    this.mainCommand = mainCommand;
    this.subCommands = new HashMap<>();
  }

  public void addSubCommand(String subCommandName, Method subCommandMethod) {
    subCommands.put(subCommandName.toLowerCase(), subCommandMethod);
  }

  public Method getMainCommand() {
    return mainCommand;
  }

  public Method getSubCommand(String subCommandName) {
    return subCommands.get(subCommandName.toLowerCase());
  }

  public Map<String, Method> getSubCommands() {
    return subCommands;
  }
}