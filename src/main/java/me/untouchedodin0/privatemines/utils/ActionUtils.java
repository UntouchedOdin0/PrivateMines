package me.untouchedodin0.privatemines.utils;

import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mine.Mine;
import org.bukkit.entity.Player;

public class ActionUtils {

    public static void handleAction(Player player, String action) {
        MineStorage mineStorage = PrivateMines.getPrivateMines().getMineStorage();
        Mine mine = mineStorage.get(player.getUniqueId());

        if (mine != null) {
            switch (action.toLowerCase()) {
                case "reset" -> mine.reset();
                case "teleport" -> mine.teleport(player);
                case "expand" -> mine.expand();
                case "upgrade" -> mine.upgrade();
            }
        }
    }
}
