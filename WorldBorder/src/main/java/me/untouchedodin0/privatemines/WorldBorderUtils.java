package me.untouchedodin0.privatemines;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

public class WorldBorderUtils {

    public void sendWorldBorder(Server server, Player player, Location location, double size) {
        WorldBorder worldBorder = server.createWorldBorder();

        worldBorder.setCenter(location);
        worldBorder.setSize(size);


    }
}
