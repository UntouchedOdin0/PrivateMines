package me.untouchedodin0.privatemines.pregen;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Location;

public class PregenMine {

  private BlockVector3 location;
  private Location spawn;
  private Location minimumMining;
  private Location maximumMining;
  private Location minimumFull;
  private Location maximumFull;

  public BlockVector3 getLocation() {
    return location;
  }

  public void setLocation(BlockVector3 location) {
    this.location = location;
  }

  public Location getSpawn() {
    return spawn;
  }

  public void setSpawn(Location spawn) {
    this.spawn = spawn;
  }

  public Location getMinimumMining() {
    return minimumMining;
  }

  public void setMinimumMining(Location minimumMining) {
    this.minimumMining = minimumMining;
  }

  public Location getMaximumMining() {
    return maximumMining;
  }

  public void setMaximumMining(Location maximumMining) {
    this.maximumMining = maximumMining;
  }

  public Location getMinimumFull() {
    return minimumFull;
  }

  public void setMinimumFull(Location minimumFull) {
    this.minimumFull = minimumFull;
  }

  public Location getMaximumFull() {
    return maximumFull;
  }

  public void setMaximumFull(Location maximumFull) {
    this.maximumFull = maximumFull;
  }
}
