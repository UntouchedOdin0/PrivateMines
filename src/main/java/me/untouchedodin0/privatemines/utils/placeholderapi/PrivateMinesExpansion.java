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