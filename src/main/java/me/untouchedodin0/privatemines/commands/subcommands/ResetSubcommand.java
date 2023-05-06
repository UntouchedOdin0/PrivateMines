package me.untouchedodin0.privatemines.commands.subcommands;

import java.nio.Buffer;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.commandmanager.SubCommand;
import me.untouchedodin0.privatemines.mine.Mine;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ResetSubcommand {

  private static final PrivateMines privateMines = PrivateMines.getPrivateMines();

  public static final SubCommand subCommand = new SubCommand() {
    @Override
    public void onCommand(CommandSender commandSender, String[] args) {


      if (!(commandSender instanceof Player)) {
        commandSender.sendMessage("only players");
        return;
      }

      final Player player = (Player) commandSender;

      if (args.length == 0) {
        Mine mine = privateMines.getMineStorage().get(player);
        if (mine == null) {
          player.sendMessage("no mine");
          return;
        }
        player.sendMessage("mine " + mine);
      }

//      if (args.length == 1) {
//        final OfflinePlayer mineOwner = Bukkit.getOfflinePlayer(args[0]);
//        PrivateMine privateMine = plugin.getPrivateMines().get(mineOwner.getUniqueId());
//        if (privateMine == null) {
//          player.sendMessage(plugin.getMessages().getMessage("OTHER_DONT_HAVE_MINE"));
//          return;
//        }
//
//
//        if (privateMine.isVisitAllowed(player)) {
//          privateMine.resetMine(true);
//        } else {
//          player.sendMessage("You have to be whitelisted to reset someones mine");
//        }
//      }

    }


    @Override
    public String name() {
      return "resetmine";
    }

    @Override
    public String info() {
      return null;
    }

    @Override
    public String[] aliases() {
      return new String[]{
          "reset",
          "rm"

      };
    }

    @Override
    public String permission() {
      return "privatemines.use.reset";
    }
  };
}