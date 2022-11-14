package me.untouchedodin0.privatemines.listener;

import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mine.Mine;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

import static org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.ALLOWED;

public class MineResetListener implements Listener {

    PrivateMines privateMines = PrivateMines.getPrivateMines();
    MineStorage mineStorage = privateMines.getMineStorage();

    @EventHandler
    public void onJoin(AsyncPlayerPreLoginEvent event) {
        if (event.getLoginResult().equals(ALLOWED)) {
            UUID uuid = event.getUniqueId();
            Mine mine = mineStorage.get(uuid);

            if (mine != null) {
                mine.startResetTask();
                mine.startPercentageTask();
            }
        }
    }

    @EventHandler
    public void onJoin(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        Mine mine = mineStorage.get(uuid);

        if (mine != null) {
            mine.stopTasks();
        }
    }
}
