package me.untouchedodin0.privatemines.commands;


import me.untouchedodin0.privatemines.utils.commands.annotations.Command;
import me.untouchedodin0.privatemines.utils.commands.annotations.CommandPermission;
import me.untouchedodin0.privatemines.utils.commands.annotations.DefaultCommand;
import me.untouchedodin0.privatemines.utils.commands.annotations.SubCommand;
import org.bukkit.Bukkit;

@Command("test")
public class TestCommand {

  @DefaultCommand
  public void defaultMethod() {
    Bukkit.broadcastMessage("hi");
  }

  @SubCommand("test")
  @CommandPermission("hi")
  public void test() {
    Bukkit.broadcastMessage("hi");
  }
}
