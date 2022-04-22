package me.untouchedodin0.privatemines.storage;

import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.mine.data.MineData;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Deprecated
public class MineStorage {

    public Map<UUID, Mine> mines = new HashMap<>();
    public PrivateMines privateMines = PrivateMines.getPrivateMines();

    public void addMine(UUID uuid, Mine mine) {
        if (mines.containsKey(uuid)) {
            privateMines.getLogger().info(String.format("Player %s already has a mine!!", uuid.toString()));
        } else {
            mines.put(uuid, mine);
        }
    }

    public void removeMine(UUID uuid) {
        if (!mines.containsKey(uuid)) {
            privateMines.getLogger().warning(String.format("Player %s doesn't a mine!!", uuid.toString()));
        } else {
            mines.remove(uuid);
        }
    }

    public boolean hasMine(UUID uuid) {
        return mines.containsKey(uuid);
    }

    public Mine get(UUID uuid) {
        return mines.get(uuid);
    }

    public Map<UUID, Mine> getMines() {
        return mines;
    }

    public Mine getClosest(Location location) {
        Map<Mine, Double> distances = new HashMap<>();
        Map.Entry<Mine, Double> min = null;

        mines.forEach((uuid, mine) -> {
            MineData mineData = mine.getMineData();
            Location mineLocation = mineData.getMineLocation();
            double distance = location.distance(mineLocation);
            distances.putIfAbsent(mine, distance);
        });

        for (Map.Entry<Mine, Double> entry : distances.entrySet()) {
            if (min == null || min.getValue() > entry.getValue()) {
                min = entry;
            }
        }
        return Objects.requireNonNull(min).getKey();
    }
}
