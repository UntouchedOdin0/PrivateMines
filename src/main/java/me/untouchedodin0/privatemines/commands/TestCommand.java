package me.untouchedodin0.privatemines.commands;


import me.untouchedodin0.privatemines.utils.commands.annotations.Command;
import me.untouchedodin0.privatemines.utils.commands.annotations.CommandPermission;
import me.untouchedodin0.privatemines.utils.commands.annotations.SubCommand;

@Command("test")
public class TestCommand {

  @SubCommand("test")
  @CommandPermission("hi")
  public void test() {

  }
}
