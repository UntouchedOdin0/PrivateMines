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

package me.untouchedodin0.privatemines.utils.conversion;

import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mine.MineTypeManager;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import redempt.redlib.misc.LocationUtils;

import java.util.Objects;
import java.util.UUID;

public class ConversionSQLUtils {

    private static final PrivateMines privateMines = PrivateMines.getPrivateMines();
    private static final MineTypeManager mineTypeManager = privateMines.getMineTypeManager();

    public static String generateCommand(YamlConfiguration yml) {

        UUID owner = UUID.fromString(Objects.requireNonNull(yml.getString("mineOwner")));
        String mineTypeName = yml.getString("mineType");
        MineType mineType = mineTypeManager.getMineType(mineTypeName);
        Location corner1 = LocationUtils.fromString(yml.getString("corner1"));
        Location corner2 = LocationUtils.fromString(yml.getString("corner2"));
        Location fullRegionMin = LocationUtils.fromString(yml.getString("fullRegionMin"));
        Location fullRegionMax = LocationUtils.fromString(yml.getString("fullRegionMax"));
        Location spawn = LocationUtils.fromString(yml.getString("spawn"));
        Location mineLocation = LocationUtils.fromString(yml.getString("mineLocation"));
        boolean isOpen = yml.getBoolean("isOpen");
        double tax = yml.getDouble("tax");
        String materialsString = yml.getString("materials");

        return String.format("INSERT INTO privatemines (mineOwner) VALUES(%s);", "test");
    }
}
