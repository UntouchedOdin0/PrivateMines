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

import me.untouchedodin0.privatemines.PrivateMines;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import redempt.redlib.misc.LocationUtils;
import redempt.redlib.sql.SQLHelper;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class ConversionUtils {

    private static boolean isEmpty(Path path) {
        if (Files.isDirectory(path)) {
            try (DirectoryStream<Path> directory = Files.newDirectoryStream(path)) {
                return !directory.iterator().hasNext();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    public static void convertSQLDirectory(Path path) {
        if (!isEmpty(path)) {
            CompletableFuture.runAsync(() -> {
                try (Stream<Path> paths = Files.walk(path)) {
                    paths.forEach(streamPath -> {
                        File file = streamPath.toFile();
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public static PreparedStatement convertToSQL(File file) throws SQLException {
        final PrivateMines privateMines = PrivateMines.getPrivateMines();
        final PathMatcher yamlMatcher = FileSystems.getDefault().getPathMatcher("glob:**/*.yml"); // Credits to Brister Mitten
        final SQLHelper sqlHelper = privateMines.getSqlHelper();

        Connection connection = sqlHelper.getConnection();
        String query = "INSERT INTO privatemines values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(query);

        privateMines.getLogger().info(String.format("Converting file %s!", file.getName()));
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

        UUID owner = UUID.fromString(Objects.requireNonNull(yml.getString("mineOwner")));
        String mineTypeName = yml.getString("mineType");
//                        MineType mineType = mineTypeManager.getMineType(mineTypeName);
        Location corner1 = LocationUtils.fromString(yml.getString("corner1"));
        Location corner2 = LocationUtils.fromString(yml.getString("corner2"));
        Location fullRegionMin = LocationUtils.fromString(yml.getString("fullRegionMin"));
        Location fullRegionMax = LocationUtils.fromString(yml.getString("fullRegionMax"));
        Location spawn = LocationUtils.fromString(yml.getString("spawn"));
        Location mineLocation = LocationUtils.fromString(yml.getString("mineLocation"));
        boolean isOpen = yml.getBoolean("isOpen");
        double tax = yml.getDouble("tax");
        String materialsString = yml.getString("materials");

        preparedStatement.setObject(1, owner);
        preparedStatement.setString(2, mineTypeName);

        return preparedStatement;
    }
}

//                    YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);

//                    UUID owner = UUID.fromString(Objects.requireNonNull(yml.getString("mineOwner")));
//                    String mineTypeName = yml.getString("mineType");
////                    MineType mineType = mineTypeManager.getMineType(mineTypeName);
//                    Location corner1 = LocationUtils.fromString(yml.getString("corner1"));
//                    Location corner2 = LocationUtils.fromString(yml.getString("corner2"));
//                    Location fullRegionMin = LocationUtils.fromString(yml.getString("fullRegionMin"));
//                    Location fullRegionMax = LocationUtils.fromString(yml.getString("fullRegionMax"));
//                    Location spawn = LocationUtils.fromString(yml.getString("spawn"));
//                    Location mineLocation = LocationUtils.fromString(yml.getString("mineLocation"));
//                    boolean isOpen = yml.getBoolean("isOpen");
//                    double tax = yml.getDouble("tax");
//                    String materialsString = yml.getString("materials");

//                    Bukkit.broadcastMessage("stream path: " + streamPath);
//                    Bukkit.broadcastMessage("file: " + file);
//                    Bukkit.broadcastMessage("yml " + yml);

//                    String command = ConversionSQLUtils.generateCommand(yml);
//                    Bukkit.broadcastMessage("Generated SQL Command:");
//                    Bukkit.broadcastMessage(command);

//                    sqlHelper.execute("INSERT INTO privatemines(mineOwner,mineType,mineLocation,corner1,corner2,fullRegionMin,fullRegionMax,spawn,tax,isOpen,maxPlayers,maxMineSize);", UUID.randomUUID());
//                    sqlHelper.execute("INSERT INTO privatemines(mineOwner, mineType, mineLocation, corner1, corner2, fullRegionMin, fullRegionMax, spawn, tax, isOpen, maxPlayers, maxMineSize, materials) VALUES(mineOwner, mineType, mineLocation, corner1, corner2, fullRegionMin, fullRegionMax, spawn, tax, isOpen, maxPlayers, maxMineSize, materials);");

//                    sqlHelper.execute(command);
//                    sqlHelper.commit();
