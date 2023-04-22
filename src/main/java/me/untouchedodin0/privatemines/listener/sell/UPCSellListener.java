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

package me.untouchedodin0.privatemines.listener.sell;

import dev.drawethree.xprison.autosell.api.events.XPrisonAutoSellEvent;
import dev.drawethree.xprison.autosell.api.events.XPrisonSellAllEvent;
import dev.drawethree.xprison.autosell.model.AutoSellItemStack;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import me.untouchedodin0.kotlin.mine.data.MineData;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.config.Config;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.utils.world.MineWorldManager;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class UPCSellListener implements Listener {

  PrivateMines privateMines = PrivateMines.getPrivateMines();
  MineStorage mineStorage = privateMines.getMineStorage();
  MineWorldManager mineWorldManager = privateMines.getMineWorldManager();
  double taxForOwner = 0;

  @EventHandler
  public void onSellAll(XPrisonSellAllEvent sellAllEvent) {
    Player player = sellAllEvent.getPlayer();
    Location playerLocation = player.getLocation();
    World playerWorld = player.getWorld();
    World minesWorld = mineWorldManager.getMinesWorld();

    if (playerWorld != minesWorld) {
      return;
    }

    Mine mine = mineStorage.getClosest(playerLocation);
    if (mine == null) {
      return;
    }

    MineData mineData = mine.getMineData();
    Economy economy = PrivateMines.getEconomy();
    UUID owner = mineData.getMineOwner();
    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(owner);
    String sellerName = player.getDisplayName();

    Map<AutoSellItemStack, Double> items = sellAllEvent.getItemsToSell();

    items.forEach((autoSellItemStack, aDouble) -> {
      double tax = aDouble / 100.0 * mineData.getTax();
      double removed = aDouble - tax;
      items.put(autoSellItemStack, removed);
      taxForOwner = taxForOwner + tax;
    });
    sellAllEvent.setItemsToSell(items);
    economy.depositPlayer(offlinePlayer, taxForOwner);

    if (Config.sendTaxMessages) {
      player.sendMessage(ChatColor.GREEN + String.format(
          ChatColor.GREEN + "Deducted $%f for the owner of the mine!", taxForOwner));
      if (offlinePlayer.getPlayer() != null) {
        offlinePlayer.getPlayer().sendMessage(ChatColor.GREEN + String.format(
            ChatColor.GREEN + "You've received $%f from %s" + ChatColor.GREEN + "!", taxForOwner,
            sellerName));
      }
    }
    taxForOwner = 0;
  }

  @EventHandler
  public void onAutoSell(XPrisonAutoSellEvent autoSellEvent) {
    Player player = autoSellEvent.getPlayer();
    Location playerLocation = player.getLocation();
    World playerWorld = player.getWorld();
    World minesWorld = mineWorldManager.getMinesWorld();

    if (playerWorld != minesWorld) {
      return;
    }

    Mine mine = mineStorage.getClosest(playerLocation);

    if (mine != null) {
      MineData mineData = mine.getMineData();
      Economy economy = PrivateMines.getEconomy();
      UUID owner = mineData.getMineOwner();
      OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(owner);
      String sellerName = player.getDisplayName();

      Map<AutoSellItemStack, Double> items = autoSellEvent.getItemsToSell();
      Map<AutoSellItemStack, Double> itemsToSell = new HashMap<>();

      items.forEach((autoSellItemStack, aDouble) -> {
        double tax = aDouble / 100.0 * mineData.getTax();
        double removed = aDouble - tax;
        itemsToSell.put(autoSellItemStack, removed);
        taxForOwner = taxForOwner + tax;
      });
      autoSellEvent.setItemsToSell(itemsToSell);
      economy.depositPlayer(offlinePlayer, taxForOwner);
      if (Config.sendTaxMessages) {
        player.sendMessage(ChatColor.GREEN + String.format(
            ChatColor.GREEN + "Deducted $%f for the owner of the mine!", taxForOwner));
        if (offlinePlayer.getPlayer() != null) {
          offlinePlayer.getPlayer().sendMessage(ChatColor.GREEN + String.format(
              ChatColor.GREEN + "You've received $%f from %s" + ChatColor.GREEN + "!", taxForOwner,
              sellerName));
        }
      }
      taxForOwner = 0;
    }
  }
}

