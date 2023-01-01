/**
 * MIT License
 * <p>
 * Copyright (c) 2021 - 2023 Kyle Hicks
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package me.untouchedodin0.privatemines.mine.quarries;

import me.untouchedodin0.privatemines.mine.Mine;
import org.bukkit.Location;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class QuarryBuilder {

    private UUID owner;
    private Mine mine;
    private Location location;
    private int intervalSeconds;
    private AtomicInteger efficiencyLevel;
    private AtomicInteger fortuneLevel;
    private AtomicInteger lightningLevel;
    private AtomicInteger tornadoLevel;
    private AtomicInteger hailLevel;
    private AtomicInteger acidRainLevel;
    private AtomicInteger jackhammerLevel;
    private AtomicInteger explosiveLevel;
    private AtomicInteger laserLevel;

    public QuarryBuilder setOwner(UUID owner) {
        this.owner = owner;
        return this;
    }

    public QuarryBuilder setMine(Mine mine) {
        this.mine = mine;
        return this;
    }

    public QuarryBuilder setLocation(Location location) {
        this.location = location;
        return this;
    }

    public QuarryBuilder setInterval(int interval) {
        this.intervalSeconds = interval;
        return this;
    }

    public QuarryBuilder setEfficiencyLevel(AtomicInteger efficiencyLevel) {
        this.efficiencyLevel = efficiencyLevel;
        return this;
    }

    public QuarryBuilder setFortuneLevel(AtomicInteger fortuneLevel) {
        this.fortuneLevel = fortuneLevel;
        return this;
    }

    public QuarryBuilder setLightningLevel(AtomicInteger lightningLevel) {
        this.lightningLevel = lightningLevel;
        return this;
    }

    public QuarryBuilder setTornadoLevel(AtomicInteger tornadoLevel) {
        this.tornadoLevel = tornadoLevel;
        return this;
    }

    public QuarryBuilder setHailLevel(AtomicInteger hailLevel) {
        this.hailLevel = hailLevel;
        return this;
    }

    public QuarryBuilder setAcidRainLevel(AtomicInteger acidRainLevel) {
        this.acidRainLevel = acidRainLevel;
        return this;
    }

    public QuarryBuilder setJackhammerLevel(AtomicInteger jackhammerLevel) {
        this.jackhammerLevel = jackhammerLevel;
        return this;
    }

    public QuarryBuilder setExplosiveLevel(AtomicInteger explosiveLevel) {
        this.explosiveLevel = explosiveLevel;
        return this;
    }

    public QuarryBuilder setLaserLevel(AtomicInteger laserLevel) {
        this.laserLevel = laserLevel;
        return this;
    }

    public Quarry build() {
        // Create the quarry object and return it
        Quarry quarry = new Quarry();
        quarry.owner = owner;
        quarry.mine = mine;
        quarry.intervalSeconds = intervalSeconds;
        quarry.efficiencyLevel = efficiencyLevel;
        quarry.fortuneLevel = fortuneLevel;
        quarry.lightningLevel = lightningLevel;
        quarry.tornadoLevel = tornadoLevel;
        quarry.hailLevel = hailLevel;
        quarry.acidRainLevel = acidRainLevel;
        quarry.jackhammerLevel = jackhammerLevel;
        quarry.explosiveLevel = explosiveLevel;
        quarry.laserLevel = laserLevel;
        return quarry;
    }
}
