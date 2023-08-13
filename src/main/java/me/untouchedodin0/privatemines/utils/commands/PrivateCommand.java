package me.untouchedodin0.privatemines.utils.commands;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;

abstract class PrivateCommand extends Command implements PluginIdentifiableCommand {

  private Plugin plugin;

  protected PrivateCommand(Plugin plugin, String name, String description, String usageMessage, String permission, List<String> aliases) {
    super(name, description, usageMessage, aliases);
    setPermission(permission);
    this.plugin = plugin;
  }

  @Override
  public Plugin getPlugin() {
    return plugin;
  }
}
