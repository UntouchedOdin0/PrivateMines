package me.untouchedodin0.privatemines.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import javax.net.ssl.HttpsURLConnection;
import me.untouchedodin0.privatemines.PrivateMines;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateChecker implements Listener {

  private final int RESOURCE_ID = 90890;
  private final PrivateMines plugin;
  private final String pluginVersion;
  private String spigotVersion;
  private boolean updateAvailable;

  public UpdateChecker(PrivateMines privateMines) {
    plugin = privateMines;
    pluginVersion = privateMines.getDescription().getVersion();
  }

  public boolean hasUpdateAvailable() {
    return updateAvailable;
  }

  public String getSpigotVersion() {
    return spigotVersion;
  }

  public void fetch() {
    Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
      try {
        HttpsURLConnection con = (HttpsURLConnection) new URL(
            "https://api.spigotmc.org/legacy/update.php?resource=" + RESOURCE_ID).openConnection();
        con.setRequestMethod("GET");
        spigotVersion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
      } catch (Exception ex) {
        plugin.getLogger().info("Failed to check for updates on spigot.");
        return;
      }

      if (spigotVersion == null || spigotVersion.isEmpty()) {
        return;
      }

      updateAvailable = spigotIsNewer();

      if (!updateAvailable) {
        return;
      }

      Bukkit.getScheduler().runTask(plugin, () -> {
        plugin.getLogger()
            .info("An update for Private Mines (v" + getSpigotVersion() + ") is available at:");
        plugin.getLogger()
            .info("https://www.spigotmc.org/resources/" + RESOURCE_ID + "/");
        Bukkit.getPluginManager().registerEvents(this, plugin);
      });
    });
  }

  private boolean spigotIsNewer() {
    if (spigotVersion == null || spigotVersion.isEmpty() || !spigotVersion.matches("[0-9].[0-9].[0-9]")) {
      return false;
    }

    int[] plV = toReadable(pluginVersion);
    int[] spV = toReadable(spigotVersion);

    if (plV == null || spV == null) return false;

    if (plV[0] < spV[0]) {
      return true;
    } else if ((plV[1] < spV[1])) {
      return true;
    } else {
      return plV[2] < spV[2];
    }
  }

  private int[] toReadable(String version) {
    if (version.contains("-DEV")) {
      version = version.split("-DEV")[0];
    }

    return Arrays.stream(version.split("\\.")).mapToInt(Integer::parseInt).toArray();
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onJoin(PlayerJoinEvent e) {
    if (e.getPlayer().hasPermission("privatemines.updatenotify")) {
      Player player = e.getPlayer();
      player.sendMessage(ChatColor.GREEN + String.format("An update is available for Private Mines at %s",
          "https://www.spigotmc.org/resources/90890/"));
    }
  }
}