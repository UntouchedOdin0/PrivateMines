/**
 * MIT License
 *
 * Copyright (c) 2021 - 2022 Kyle Hicks
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.untouchedodin0.privatemines.utils.world;

import me.untouchedodin0.privatemines.PrivateMines;
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
        minesWorld = Bukkit.createWorld(
                new WorldCreator("privatemines")
                        .type(WorldType.FLAT)
                        .generator(new EmptyWorldGenerator()));
        int yLevel = PrivateMines.getPrivateMines().getConfig().getInt("mineYLevel");
        this.borderDistance = PrivateMines.getPrivateMines().getConfig().getInt("mineDistance");
        this.defaultLocation = new Location(minesWorld, 0, yLevel, 0);
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
