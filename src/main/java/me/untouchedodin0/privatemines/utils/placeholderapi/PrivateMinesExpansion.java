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

package me.untouchedodin0.privatemines.utils.placeholderapi;

import java.util.Objects;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.untouchedodin0.kotlin.mine.data.MineData;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mine.Mine;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PrivateMinesExpansion extends PlaceholderExpansion {

  private final PrivateMines plugin;

  public PrivateMinesExpansion(PrivateMines plugin) {
    this.plugin = plugin;
  }

  @Override
  public String getAuthor() {
    return "UntouchedOdin0";
  }

  @Override
  public String getIdentifier() {
    return "privatemines";
  }

  @Override
  public String getVersion() {
    return "1.0.0";
  }

  @Override
  public boolean persist() {
    return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
  }

  @Override
  public String onRequest(OfflinePlayer player, @NotNull String params) {

    PrivateMines privateMines = PrivateMines.getPrivateMines();
    MineStorage mineStorage = privateMines.getMineStorage();
    Mine mine = mineStorage.get(Objects.requireNonNull(player.getPlayer()).getUniqueId());
    MineData mineData;

    if (params.equalsIgnoreCase("size")) {
      if (mine != null) {
        mineData = mine.getMineData();
        Location minimum = mineData.getMinimumMining();
        Location maximum = mineData.getMaximumMining();
        double distance = maximum.distance(minimum);
        int distanceInt = (int) distance;

        return Integer.toString(distanceInt);
      }
      return "";
    }

    if (params.equalsIgnoreCase("placeholder2")) {
      return "test2";
    }

    return null; // Placeholder is unknown by the Expansion
  }
}