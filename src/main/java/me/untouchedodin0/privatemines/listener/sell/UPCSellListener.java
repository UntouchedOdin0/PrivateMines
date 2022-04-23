package me.untouchedodin0.privatemines.listener.sell;

import dev.drawethree.ultraprisoncore.autosell.api.events.UltraPrisonSellAllEvent;
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

public class UPCSellListener implements Listener {

    PrivateMines privateMines = PrivateMines.getPrivateMines();
    MineStorage mineStorage = privateMines.getMineStorage();

    @EventHandler
    public void onSellAll(UltraPrisonSellAllEvent sellAllEvent) {
        Economy economy = PrivateMines.getEconomy();

        Player player = sellAllEvent.getPlayer();
        Location playerLocation = player.getLocation();
        Mine mine = mineStorage.getClosest(playerLocation);
        MineData mineData = mine.getMineData();

        Player owner = Bukkit.getOfflinePlayer(mineData.getMineOwner()).getPlayer();

        double tax = sellAllEvent.getSellPrice() / 100.0 * mineData.getTax();
        double sellPrice = sellAllEvent.getSellPrice();
        double afterTax = sellPrice - tax;
        sellAllEvent.setSellPrice(afterTax);
        economy.depositPlayer(owner, tax);
        if (owner != null) {
            owner.sendMessage(ChatColor.GREEN + "You've received $" + tax + " in taxes from " + player.getDisplayName() + "!");
        }
    }
}
