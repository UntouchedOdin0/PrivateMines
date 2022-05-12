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

package me.untouchedodin0.privatemines.utils.world.utils;

import org.bukkit.Location;

public enum Direction {

    NORTH(0, -1), NORTH_EAST(1, -1),
    EAST(1, 0), SOUTH_EAST(1, 1),
    SOUTH(0, 1), SOUTH_WEST(-1, 1),
    WEST(-1, 0), NORTH_WEST(-1, -1);

    private final double xMulti;
    private final double zMulti;

    Direction(double xMulti, double zMulti) {
        this.xMulti = xMulti;
        this.zMulti = zMulti;
    }

    /**
     * Gets the direction for the next Private Mine
     *
     * @return Direction
     */

    public Direction next() {
        return values()[(ordinal() + 1) % (values().length)];
    }

    /**
     * @param location - The start location to be added onto
     * @param value    - The distance in blocks to add to the location
     * @return Location
     */

    public Location addTo(Location location, int value) {
        return location.clone().add(value * xMulti, 0, value * zMulti);
    }
}