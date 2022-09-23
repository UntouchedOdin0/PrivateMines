package me.untouchedodin0.privatemines.utils.inventory;

import me.untouchedodin0.kotlin.mine.data.MineData;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import redempt.redlib.inventorygui.InventoryGUI;
import redempt.redlib.inventorygui.ItemButton;
import redempt.redlib.inventorygui.PaginationPanel;
import redempt.redlib.itemutils.ItemBuilder;

import java.util.*;

public class PublicMinesMenu {

    public void open(Player player) {

        PrivateMines privateMines = PrivateMines.getPrivateMines();
        MineStorage mineStorage = privateMines.getMineStorage();


        List<UUID> uuidList = new ArrayList<>();

        for (int i = 0; i < 100; i++) {
            uuidList.add(UUID.randomUUID());
        }

        InventoryGUI inventoryGUI = new InventoryGUI(27, "Debug");
        PaginationPanel paginationPanel = new PaginationPanel(inventoryGUI);

        int inventorySize = inventoryGUI.getInventory().getSize();
        int previousSlot = inventorySize - 6;
        int nextSlot = inventorySize - 4;
        int maxSlots = inventorySize - 9;

        ItemButton previousPage = ItemButton.create(new ItemBuilder(Material.PAPER).setName("Previous Page"), event -> {
            paginationPanel.prevPage();
        });

        ItemButton nextPage = ItemButton.create(new ItemBuilder(Material.PAPER).setName("Next Page"), event -> {
            paginationPanel.nextPage();
        });

        inventoryGUI.addButton(previousSlot, previousPage);
        inventoryGUI.addButton(nextSlot, nextPage);

        paginationPanel.addSlots(0, maxSlots);

        Random random = new Random();
        Material[] materials = Material.values();
        int size = materials.length;

        mineStorage.getMines().forEach((uuid, mine) -> {
            String name = Bukkit.getOfflinePlayer(uuid).getName();
            List<String> lore = new ArrayList<>();

            MineData mineData = mine.getMineData();

            if (!mineData.getMaterials().isEmpty()) {
                mineData.getMaterials().forEach((material, aDouble) -> {
                    lore.add(String.format(Utils.colorBukkit("&a%s %f%"), material.name(), aDouble));
                });
            } else {
                Objects.requireNonNull(mineData.getMineType().getMaterials()).forEach((material, aDouble) -> {
                    lore.add(String.format(Utils.colorBukkit("&a%s %f"), Utils.format(material), aDouble) + "%");
                });
            }
            lore.add("lore");
            lore.add("lore");

            ItemButton mineButton = ItemButton.create(new ItemBuilder(Material.PAPER).setName(name).addLore(lore), event -> {
                player.closeInventory();
               player.sendMessage("mine: " + mine);
            });
            paginationPanel.addPagedButton(mineButton);
        });
//        uuidList.forEach(uuid -> {
//            paginationPanel.addPagedItem(new ItemBuilder(Material.PAPER).setName(uuid.toString()));
//        });
        inventoryGUI.open(player);
    }
}
