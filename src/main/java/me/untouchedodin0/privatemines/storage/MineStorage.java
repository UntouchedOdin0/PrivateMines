package me.untouchedodin0.privatemines.storage;

import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mine.Mine;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
}
