package me.untouchedodin0.privatemines.listener;

import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.config.Config;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.mine.MineTypeManager;
import me.untouchedodin0.privatemines.utils.world.MineWorldManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    PrivateMines privateMines = PrivateMines.getPrivateMines();
    MineFactory mineFactory = privateMines.getMineFactory();
    MineStorage mineStorage = privateMines.getMineStorage();
    MineTypeManager mineTypeManager = privateMines.getMineTypeManager();

    @EventHandler
    public void onJoin(PlayerJoinEvent playerJoinEvent) {
        Player player = playerJoinEvent.getPlayer();
        MineWorldManager mineWorldManager = privateMines.getMineWorldManager();
        Location location = mineWorldManager.getNextFreeLocation();
        MineType mineType = mineTypeManager.getDefaultMineType();

        if (Config.giveMineOnFirstJoin) {
            if (!mineStorage.hasMine(player.getUniqueId())) {
                mineFactory.create(player, location, mineType);
            }
        }
    }
}
