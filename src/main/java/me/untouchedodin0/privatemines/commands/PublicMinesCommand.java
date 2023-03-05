package me.untouchedodin0.privatemines.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.ScrollType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.ScrollingGui;
import java.util.UUID;
import me.untouchedodin0.kotlin.mine.data.MineData;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.privatemines.PrivateMines;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@CommandAlias("publicmines")
public class PublicMinesCommand extends BaseCommand {

  PrivateMines privateMines = PrivateMines.getPrivateMines();
  MineStorage mineStorage = privateMines.getMineStorage();

  //todo make this work!
  @Default
  public void defaultCommand(Player player) {
    ScrollingGui scrollingGui = Gui.scrolling().title(Component.text("Public Mines")).rows(6)
        .pageSize(45).scrollType(ScrollType.VERTICAL).create();

    GuiItem back = ItemBuilder.from(Material.ARROW).asGuiItem(event -> {
      event.setCancelled(true);
      scrollingGui.previous();
    });

    GuiItem forward = ItemBuilder.from(Material.ARROW).asGuiItem(event -> {
      event.setCancelled(true);
      scrollingGui.next();
    });

    mineStorage.getMines().forEach((uuid, mine) -> {
      MineData mineData = mine.getMineData();
      boolean isOpen = mineData.isOpen();

      if (isOpen) {
        UUID owner = mineData.getMineOwner();
        String name = Bukkit.getOfflinePlayer(owner).getName();

        GuiItem guiItem;
        if (name != null) {
          guiItem = ItemBuilder.from(Material.EMERALD).name(Component.text(name))
              .lore(Component.text(ChatColor.GREEN + "Click to teleport")).asGuiItem(event -> {
                event.setCancelled(true);
                mine.teleport(player);
              });
          scrollingGui.addItem(guiItem);
        }
      }
    });
//    GuiItem guiItem = ItemBuilder.from(Material.EMERALD).asGuiItem(event -> {
//      event.setCancelled(true);
//    });
//
//    for (int i = 0; i < 60; i++) {
//      scrollingGui.addItem(guiItem);
//    }

    scrollingGui.setItem(48, back);
    scrollingGui.setItem(50, forward);
    scrollingGui.open(player);
  }
}
