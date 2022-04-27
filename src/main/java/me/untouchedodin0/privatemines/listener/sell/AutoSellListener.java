package me.untouchedodin0.privatemines.listener.sell;

import me.clip.autosell.events.AutoSellEvent;
import me.clip.autosell.events.SellAllEvent;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.mine.data.MineData;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AutoSellListener implements Listener {

    PrivateMines privateMines = PrivateMines.getPrivateMines();
    MineStorage mineStorage = privateMines.getMineStorage();

    @EventHandler
    public void sellAll(SellAllEvent sellAllEvent) {
        Economy economy = PrivateMines.getEconomy();
        Player player = sellAllEvent.getPlayer();
        Location location = player.getLocation();
        Mine mine = mineStorage.getClosest(location);
        MineData mineData = mine.getMineData();
        Player owner = Bukkit.getOfflinePlayer(mineData.getMineOwner()).getPlayer();
        if (player.equals(owner)) return;

        double tax = sellAllEvent.getTotalCost() / 100.0 * mineData.getTax();
        double sellPrice = sellAllEvent.getTotalCost();
        double afterTax = sellPrice - tax;
        sellAllEvent.setTotalCost(afterTax);
        economy.depositPlayer(owner, tax);
        if (owner != null) {
            owner.sendMessage(ChatColor.GREEN + "You've received $" + tax + " in taxes from " + player.getDisplayName() + ChatColor.GREEN + "!");
        }
    }

    @EventHandler
    public void onAutoSell(AutoSellEvent autoSellEvent) {
        Economy economy = PrivateMines.getEconomy();
        Player player = autoSellEvent.getPlayer();
        Location location = player.getLocation();
        Mine mine = mineStorage.getClosest(location);
        MineData mineData = mine.getMineData();
        Player owner = Bukkit.getOfflinePlayer(mineData.getMineOwner()).getPlayer();
        if (player.equals(owner)) return;

        double tax = autoSellEvent.getPrice() / 100.0 * mineData.getTax();
        double sellPrice = autoSellEvent.getPrice();
        double afterTax = sellPrice - tax;
        autoSellEvent.setMultiplier(afterTax);
        economy.depositPlayer(owner, tax);
        if (owner != null) {
            owner.sendMessage(ChatColor.GREEN + "You've received $" + tax + " in taxes from " + player.getDisplayName() + ChatColor.GREEN + "!");
        }
    }
}
