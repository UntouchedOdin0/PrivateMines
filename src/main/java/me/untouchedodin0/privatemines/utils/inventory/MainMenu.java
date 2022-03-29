package me.untouchedodin0.privatemines.utils.inventory;

import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.privatemines.mine.Mine;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import redempt.redlib.inventorygui.InventoryGUI;
import redempt.redlib.inventorygui.ItemButton;
import redempt.redlib.itemutils.ItemBuilder;

public class MainMenu {

    private final MineStorage mineStorage;

    public MainMenu(MineStorage mineStorage) {
        this.mineStorage = mineStorage;
    }

    public void openMainMenu(Player player) {
        if (!mineStorage.hasMine(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You don't own a mine");
        } else {
            InventoryGUI yourMine = new InventoryGUI(Bukkit.createInventory(null, 9, "Your Mine"));
            Mine mine = mineStorage.get(player.getUniqueId());
            ItemBuilder filler = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setName(" ");
            ItemButton fillerButton = ItemButton.create(filler, e -> e.setCancelled(true));

            if (mine != null) {
                ItemButton teleport = ItemButton.create(new ItemBuilder(Material.DIAMOND_BOOTS).setName(ChatColor.GREEN + "Click to teleport").addLore(ChatColor.GRAY + "to your mine"), clickEvent -> {
                    clickEvent.setCancelled(true);
                    player.sendMessage(ChatColor.GREEN + "Teleporting you to the mine");
                    mine.teleport(player);
                });
                ItemButton reset = ItemButton.create(new ItemBuilder(Material.STONE_BUTTON).setName(ChatColor.GREEN + "Click to reset").addLore(ChatColor.GRAY + "your mine"), clickEvent -> {
                    clickEvent.setCancelled(true);
                    player.sendMessage(ChatColor.GREEN + "Resetting your mine");
                    mine.reset();
                });
                yourMine.addButton(0, fillerButton);
                yourMine.addButton(1, fillerButton);
                yourMine.addButton(2, fillerButton);
                yourMine.addButton(3, teleport);
                yourMine.addButton(4, fillerButton);
                yourMine.addButton(5, reset);
                yourMine.addButton(6, fillerButton);
                yourMine.addButton(7, fillerButton);
                yourMine.addButton(8, fillerButton);
                yourMine.open(player);
            }
        }
    }
}
