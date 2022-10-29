/**
 * MIT License
 *
 * Copyright (c) 2021 - 2022 Kyle Hicks
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package me.untouchedodin0.privatemines.listener.sell;

import me.clip.autosell.events.AutoSellEvent;
import me.clip.autosell.events.SellAllEvent;
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
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AutoSellListener implements Listener {
    PrivateMines privateMines = PrivateMines.getPrivateMines();
    MineStorage mineStorage = privateMines.getMineStorage();
    MineWorldManager mineWorldManager = privateMines.getMineWorldManager();

    @EventHandler
    public void sellAll(SellAllEvent sellAllEvent) {
        Economy economy = PrivateMines.getEconomy();
        Player player = sellAllEvent.getPlayer();
        Location location = player.getLocation();
        World playerWorld = player.getWorld();
        World minesWorld = mineWorldManager.getMinesWorld();

        if (playerWorld != minesWorld) return;

        Mine mine = mineStorage.getClosest(location);

        MineData mineData = mine.getMineData();
        Player owner = Bukkit.getOfflinePlayer(mineData.getMineOwner()).getPlayer();
        if (player.equals(owner)) return;

        double tax = sellAllEvent.getTotalCost() / 100.0 * mineData.getTax();
        double sellPrice = sellAllEvent.getTotalCost();
        double afterTax = sellPrice - tax;
        sellAllEvent.setTotalCost(afterTax);
        economy.depositPlayer(owner, tax);

        if (Config.sendTaxMessages) {
            if (owner != null) {
                owner.sendMessage(ChatColor.GREEN + "You've received $" + tax + " in taxes from " + player.getDisplayName() + ChatColor.GREEN + "!");
            }
        }
    }

    @EventHandler
    public void onAutoSell(AutoSellEvent autoSellEvent) {
        Economy economy = PrivateMines.getEconomy();
        Player player = autoSellEvent.getPlayer();
        Location location = player.getLocation();

        World playerWorld = player.getWorld();
        World minesWorld = mineWorldManager.getMinesWorld();

        if (playerWorld != minesWorld) return;

        Mine mine = mineStorage.getClosest(location);
        if (mine == null) return;

        MineData mineData = mine.getMineData();
        Player owner = Bukkit.getOfflinePlayer(mineData.getMineOwner()).getPlayer();
        if (player.equals(owner)) return;

        double tax = autoSellEvent.getPrice() / 100.0 * mineData.getTax();
        double sellPrice = autoSellEvent.getPrice();
        double afterTax = sellPrice - tax;
        autoSellEvent.setMultiplier(afterTax);
        economy.depositPlayer(owner, tax);

        if (Config.sendTaxMessages) {
            if (owner != null) {
                owner.sendMessage(ChatColor.GREEN + "You've received $" + tax + " in taxes from " + player.getDisplayName() + ChatColor.GREEN + "!");
            }
        }
    }
}
