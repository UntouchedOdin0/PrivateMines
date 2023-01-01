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

import java.util.Objects;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.utils.SkullCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import redempt.redlib.inventorygui.InventoryGUI;
import redempt.redlib.inventorygui.ItemButton;
import redempt.redlib.itemutils.ItemBuilder;

public class LocaleInventory {

  /**
   * Opens the locale inventory for the player Get the heads from <a
   * href="https://minecraft-heads.com/"</a> To get the Base64 of the head, go into the head and
   * find the Base64 under the "Other" thing at the bottom
   */
  static PrivateMines privateMines = PrivateMines.getPrivateMines();

  public static void openLocaleMenu(Player player) {

    InventoryGUI changeLocale = new InventoryGUI(Bukkit.createInventory(null, 9, "Change locale"));
    ItemStack englishSkull = SkullCreator.itemFromBase64(
        "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODc5ZDk5ZDljNDY0NzRlMjcxM2E3ZTg0YTk1ZTRjZTdlOGZmOGVhNGQxNjQ0MTNhNTkyZTQ0MzVkMmM2ZjlkYyJ9fX0=");
    ItemBuilder english = new ItemBuilder(englishSkull)
        .setName(ChatColor.YELLOW + "English")
        .setLore(ChatColor.GRAY + "Click to change to " + ChatColor.GREEN + "English");
    ItemButton englishButton = ItemButton.create(english, event -> {
      event.setCancelled(true);
      player.sendMessage(String.format(ChatColor.GREEN + "You have changed your locale to %s",
          Objects.requireNonNull(Objects.requireNonNull(event.getCurrentItem()).getItemMeta())
              .getDisplayName()));
      player.closeInventory();

    });
    changeLocale.addButton(0, englishButton);
    changeLocale.open(player);
  }
}
