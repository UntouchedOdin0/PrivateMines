package me.untouchedodin0.privatemines.utils.world;

import me.untouchedodin0.privatemines.config.Config;
import me.untouchedodin0.privatemines.utils.world.utils.Direction;
import org.bukkit.*;

import static me.untouchedodin0.privatemines.utils.world.utils.Direction.NORTH;

public class MineWorldManager {

    private final Location defaultLocation;
    private final int borderDistance;
    private int distance = 0;
    private Direction direction;
    private final World minesWorld;

    public MineWorldManager() {
        WorldCreator worldCreator = WorldCreator.name("privatemines");
        minesWorld = Bukkit.createWorld(
                new WorldCreator("privatemines")
                        .type(WorldType.FLAT)
                        .generator(new EmptyWorldGenerator()));
        this.borderDistance = Config.mineDistance;
        this.direction = NORTH;
        defaultLocation = new Location(minesWorld, 0, 50, 0); // may need to raise the Y sometime?
    }

    public synchronized Location getNextFreeLocation() {
        if (distance == 0) {
            distance++;
            return defaultLocation;
        }

        if (direction == null) direction = NORTH;
        Location location = direction.addTo(defaultLocation, distance * borderDistance);
        direction = direction.next();
        if (direction == NORTH) distance++;
        return location;
    }

    public World getMinesWorld() {
        return minesWorld;
    }
}
