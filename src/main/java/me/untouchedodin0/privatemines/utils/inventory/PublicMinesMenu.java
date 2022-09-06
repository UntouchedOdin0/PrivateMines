package me.untouchedodin0.privatemines.utils.inventory;

import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.privatemines.PrivateMines;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import redempt.redlib.inventorygui.InventoryGUI;
import redempt.redlib.inventorygui.ItemButton;
import redempt.redlib.inventorygui.PaginationPanel;
import redempt.redlib.itemutils.ItemBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PublicMinesMenu {

    public void open(Player player) {

        PrivateMines privateMines = PrivateMines.getPrivateMines();
        MineStorage mineStorage = privateMines.getMineStorage();

        player.sendMessage("" + privateMines);
        player.sendMessage("" + mineStorage);

        List<UUID> uuidList = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            uuidList.add(UUID.randomUUID());
        }

        InventoryGUI inventoryGUI = new InventoryGUI(27, "Debug");
        PaginationPanel paginationPanel = new PaginationPanel(inventoryGUI);

        int previousSlot = inventoryGUI.getInventory().getSize() - 6;
        int currentPageSlot = inventoryGUI.getInventory().getSize() - 5;
        int nextSlot = inventoryGUI.getInventory().getSize() - 4;
        int maxSlots = inventoryGUI.getInventory().getSize() - 9;
        int currentPage = paginationPanel.getPage();

        ItemButton previousPage = ItemButton.create(new ItemBuilder(Material.PAPER).setName("Previous Page"), event -> {
            paginationPanel.prevPage();
            player.sendMessage("prev page ");
            player.sendMessage("" + paginationPanel.getPage());
        });

        ItemButton nextPage = ItemButton.create(new ItemBuilder(Material.PAPER).setName("Next Page"), event -> {
            paginationPanel.nextPage();
            player.sendMessage("next page ");
            player.sendMessage("" + paginationPanel.getPage());
        });

        ItemButton pageNumber = ItemButton.create(new ItemBuilder(Material.PAPER).setName("Current Page: " + currentPage), event -> event.setCancelled(true));

        inventoryGUI.addButton(previousSlot, previousPage);
        inventoryGUI.addButton(currentPageSlot, pageNumber);
        inventoryGUI.addButton(nextSlot, nextPage);

        paginationPanel.addSlots(0, maxSlots);

        uuidList.forEach(uuid -> {
            paginationPanel.addPagedItem(new ItemBuilder(Material.PAPER).setName(uuid.toString()));
        });
        inventoryGUI.open(player);
    }
}
