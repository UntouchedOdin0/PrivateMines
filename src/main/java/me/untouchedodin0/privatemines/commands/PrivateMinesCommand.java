package me.untouchedodin0.privatemines.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Flags;
import co.aikar.commands.annotation.Subcommand;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;
import com.sk89q.worldedit.world.World;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.config.MineConfig;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.type.MineType;
import me.untouchedodin0.privatemines.utils.files.StructureFile;
import org.bukkit.entity.Player;

@CommandAlias("privatemines|pmines|pmine")
public class PrivateMinesCommand extends BaseCommand {

    PrivateMines privateMines = PrivateMines.getPrivateMines();

    @Subcommand("give")
    @CommandCompletion("@players")
    public void give(Player player, Player target) {
        player.sendMessage("Giving " + target.getName() + " a private mine!");
        MineFactory mineFactory = new MineFactory();
        MineType mineType = MineConfig.mineTypes.get("Test");
        player.sendMessage("mine factory: " + mineFactory);
        mineFactory.create(player, player.getLocation(), mineType);
    }

    @Subcommand("delete")
    @CommandCompletion("@players")
    public void delete(Player player, Player target) {
        if (target != null) {
            player.sendMessage("Deleting " + target.getName() + "'s private mine!");
        }
    }

    @Subcommand("create")
    public void create(Player player, String fileName) {
        StructureFile structureFile = new StructureFile();
        com.sk89q.worldedit.entity.Player actor = BukkitAdapter.adapt(player); // WorldEdit's native Player class extends Actor
        SessionManager manager = WorldEdit.getInstance().getSessionManager();
        LocalSession localSession = manager.get(actor);
        World world = localSession.getSelectionWorld();
        Region region;

        try {
            if (world == null) throw new IncompleteRegionException();
            region = localSession.getSelection(world);
            player.sendMessage("region: " + region);
            player.sendMessage("min: " + region.getMinimumPoint());
            player.sendMessage("max: " + region.getMaximumPoint());
        } catch (IncompleteRegionException e) {
            e.printStackTrace();
        }
    }
}
