package me.untouchedodin0.privatemines.commands;

import java.util.Arrays;
import java.util.List;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.commandmanager.BaseCommand;
import me.untouchedodin0.privatemines.commandmanager.SubCommand;
import me.untouchedodin0.privatemines.commands.subcommands.ResetSubcommand;
import org.bukkit.command.CommandSender;

public class TestCommand {

  private final PrivateMines privateMines = PrivateMines.getPrivateMines();

  public final BaseCommand baseCommand = new BaseCommand("test") {
    @Override
    public void onCommand(CommandSender sender, String[] args) {
    }

    @Override
    public List<SubCommand> subCommandList() {
      return Arrays.asList(ResetSubcommand.subCommand);
    }

    @Override
    public String setDescription() {
      return null;
    }

    @Override
    public String[] aliases() {
      return new String[0];
    }

    @Override
    public String permission() {
      return null;
    }
  };

  public BaseCommand getBaseCommand() {
    return baseCommand;
  }
}
