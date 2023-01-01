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

package me.untouchedodin0.privatemines.utils.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
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

    ItemButton previousPage = ItemButton.create(
        new ItemBuilder(Material.PAPER).setName("Previous Page"), event -> {
          paginationPanel.prevPage();
        });

    ItemButton nextPage = ItemButton.create(new ItemBuilder(Material.PAPER).setName("Next Page"),
        event -> {
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
        Objects.requireNonNull(mineData.getMineType().getMaterials())
            .forEach((material, aDouble) -> {
              lore.add(String.format(Utils.colorBukkit("&a%s %f"), Utils.format(material), aDouble)
                  + "%");
            });
      }
      lore.add("lore");
      lore.add("lore");

      ItemButton mineButton = ItemButton.create(
          new ItemBuilder(Material.PAPER).setName(name).addLore(lore), event -> {
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
