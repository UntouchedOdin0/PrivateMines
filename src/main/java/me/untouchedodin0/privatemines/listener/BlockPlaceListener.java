package me.untouchedodin0.privatemines.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockPlaceListener implements Listener {

  private final Player player;
  private final Map<Material, Integer> requiredAmounts = new HashMap<>();
  private final Map<Material, Integer> placedAmounts = new HashMap<>();
  private final List<Location> corners = new ArrayList<>();
  private boolean allBlocksPlaced = false;

  public BlockPlaceListener(Player player) {
    this.player = player;

    requiredAmounts.put(Material.COAL_BLOCK, 2);
    requiredAmounts.put(Material.COBBLESTONE, 1);
  }

  @EventHandler
  public void onBlockPlace(BlockPlaceEvent event) {
    Player player = event.getPlayer();
    Block placedBlock = event.getBlockPlaced();

    if (placedBlock.getType() == Material.COAL_BLOCK
        || placedBlock.getType() == Material.COBBLESTONE) {

      // Check if the block type is already in the map
      int currentAmount = placedAmounts.getOrDefault(placedBlock.getType(), 0);

      if (currentAmount < requiredAmounts.get(placedBlock.getType())) {
        // The player has placed the expected block, and they haven't placed the maximum allowed amount yet
        player.sendMessage("You've placed a " + placedBlock.getType().name() + " block.");

        // Increase the count for the placed block type
        placedAmounts.put(placedBlock.getType(), currentAmount + 1);

        // Check if the required amounts are met
        if (Objects.equals(placedAmounts.get(Material.COAL_BLOCK), requiredAmounts.get(Material.COAL_BLOCK))
            && Objects.equals(placedAmounts.get(Material.COBBLESTONE), requiredAmounts.get(Material.COBBLESTONE))) {

          // Store the corners if not already stored
          if (corners.size() < 2) {
            corners.add(placedBlock.getLocation());
          }

          // Set the flag to true when all blocks are placed
          allBlocksPlaced = true;
        }
      } else {
        // The player has placed the maximum allowed blocks, cancel the event
        player.sendMessage("You've placed the maximum allowed " + placedBlock.getType().name() + " blocks.");
        event.setCancelled(true);
      }

      // Check the flag and inform the player
      if (allBlocksPlaced) {
        player.sendMessage(ChatColor.RED + "You've placed all the required blocks for all materials!");
        player.sendMessage("Corner 1: " + corners.get(0).toString());
        player.sendMessage("Corner 2: " + corners.get(1).toString());
        Bukkit.broadcastMessage(ChatColor.RED + "You've placed all the required blocks for all materials!");
      }
    }
  }
}
