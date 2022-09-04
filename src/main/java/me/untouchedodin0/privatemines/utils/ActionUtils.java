package me.untouchedodin0.privatemines.utils;

import me.untouchedodin0.kotlin.menu.Menu;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.config.MenuConfig;
import me.untouchedodin0.privatemines.mine.Mine;
import org.bukkit.entity.Player;

public class ActionUtils {

    public static void handleClick(Player player, String clickAction) {
        ActionType actionType = ActionType.getByStart(clickAction);
        MineStorage mineStorage = PrivateMines.getPrivateMines().getMineStorage();
        Mine mine = mineStorage.get(player.getUniqueId());
        Menu ownMine = MenuConfig.getMenus().get("personalMenu");
        Menu publicMines = MenuConfig.getMenus().get("publicMines");

        if (mine != null && actionType != null) {
            switch (actionType) {
                case RESET -> mine.reset();
                case RESET_TELEPORT -> {
                    mine.reset();
                    mine.teleport(player);
                }
                case TELEPORT -> mine.teleport(player);
                case OWNMINE -> {
                    player.closeInventory();
                    ownMine.open(player);
                }
                case PUBLICMINES -> {
                    publicMines.open(player);
                }
            }
        }
    }
}
