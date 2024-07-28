package me.untouchedodin0.privatemines.listener.sell;

import java.util.Map;
import java.util.Objects;
import me.untouchedodin0.kotlin.mine.data.MineData;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.playershops.Shop;
import me.untouchedodin0.privatemines.playershops.ShopItem;
import me.untouchedodin0.privatemines.playershops.ShopUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class PlayerShopListener implements Listener {

  PrivateMines privateMines = PrivateMines.getPrivateMines();
  MineStorage mineStorage = privateMines.getMineStorage();

  @EventHandler(priority = EventPriority.LOWEST)
  public void onBlockBreak(BlockBreakEvent event) {
    Player player = event.getPlayer();
    Location location = event.getBlock().getLocation();
    Mine mine = mineStorage.getClosest(location);
    if (mine != null) {
      MineData mineData = mine.getMineData();
      Shop shop = mineData.getShop();
      ShopUtils shopUtils = new ShopUtils();
      Material material = location.getBlock().getType();
      int quantity = event.getBlock().getDrops().size();
      double price = shop != null ? shop.getPrice(material) : 0;
      ShopItem shopItem = new ShopItem(material, quantity, price, 0);
      ShopUtils.addItem(mineData.getMineOwner(), shopItem);
//      ShopUtils.addItem(player.getUniqueId(), material, quantity, price);
      //shopUtils.sellItems(player.getUniqueId(), true);
    }
  }
}
