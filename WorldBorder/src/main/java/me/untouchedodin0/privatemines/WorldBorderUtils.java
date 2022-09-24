package me.untouchedodin0.privatemines;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class WorldBorderUtils {

    public boolean setBorder = false;

    public Map<UUID, WorldBorder> worldBorders = new HashMap<>();

    public void sendWorldBorder(Server server, Player player, Location location, double size) {

        WorldBorder worldBorder = server.createWorldBorder();
        worldBorder.setCenter(location);
        worldBorder.setSize(size);
        player.setWorldBorder(worldBorder);

        worldBorders.put(player.getUniqueId(), worldBorder);
    }

    public void clearBorder(Player player) {
        Objects.requireNonNull(player.getWorldBorder()).reset();
        worldBorders.remove(player.getUniqueId());
    }

    public boolean isSetBorder() {
        return setBorder;
    }

    public WorldBorder getWorldBorder(Player player) {
        return worldBorders.get(player.getUniqueId());
    }
}
