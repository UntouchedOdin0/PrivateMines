package me.untouchedodin0.privatemines.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import dev.triumphteam.gui.builder.item.ItemBuilder;
import dev.triumphteam.gui.components.ScrollType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.GuiItem;
import dev.triumphteam.gui.guis.ScrollingGui;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.privatemines.PrivateMines;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@CommandAlias("publicmines")
public class PublicMinesCommand extends BaseCommand {
  PrivateMines privateMines = PrivateMines.getPrivateMines();
  MineStorage mineStorage = privateMines.getMineStorage();

  //todo make this work!
  @Default
  public void defaultCommand(Player player) {
    ScrollingGui scrollingGui = Gui.scrolling()
        .title(Component.text("Public Mines"))
        .rows(6)
        .pageSize(45)
        .scrollType(ScrollType.VERTICAL)
        .create();

    GuiItem back = ItemBuilder.from(Material.ARROW).asGuiItem(event -> {
      event.setCancelled(true);
      scrollingGui.previous();
    });

    GuiItem forward = ItemBuilder.from(Material.ARROW).asGuiItem(event -> {
      event.setCancelled(true);
      scrollingGui.next();
    });

    GuiItem guiItem = ItemBuilder.from(Material.EMERALD).asGuiItem(event -> {
      event.setCancelled(true);
    });

    for (int i = 0; i < 60; i++) {
      scrollingGui.addItem(guiItem);
    }

    scrollingGui.setItem(48, back);
    scrollingGui.setItem(50, forward);
    scrollingGui.open(player);
  }
}
