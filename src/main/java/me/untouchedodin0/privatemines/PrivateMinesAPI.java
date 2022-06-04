package me.untouchedodin0.privatemines;

import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.mine.Mine;
import org.bukkit.Location;

import java.util.Map;
import java.util.UUID;

public class PrivateMinesAPI {

    PrivateMines privateMines = PrivateMines.getPrivateMines();
    MineStorage mineStorage = privateMines.getMineStorage();
    MineFactory mineFactory = privateMines.getMineFactory();

    public Mine getMine(UUID uuid) {
        if (!mineStorage.hasMine(uuid)) return null;
        return mineStorage.get(uuid);
    }
    public Mine getAtLocation(Location location) {
        return mineStorage.getClosest(location);
    }
    public Map<UUID, Mine> getMines() {
        return mineStorage.getMines();
    }
    public boolean hasMine(UUID uuid) {
        return mineStorage.hasMine(uuid);
    }
    public void createMine(UUID uuid, Location location, MineType mineType) {
        mineFactory.createUpgraded(uuid, location, mineType);
    }
}
