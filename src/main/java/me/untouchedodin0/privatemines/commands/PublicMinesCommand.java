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
import java.util.concurrent.atomic.AtomicInteger;
import me.untouchedodin0.kotlin.mine.data.MineData;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.privatemines.PrivateMines;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

@CommandAlias("publicmines")
public class PublicMinesCommand extends BaseCommand {

  PrivateMines privateMines = PrivateMines.getPrivateMines();
  MineStorage mineStorage = privateMines.getMineStorage();

  @Default
  public void defaultCommand(Player player) {
    AtomicInteger openAmount = new AtomicInteger();

    ScrollingGui scrollingGui = Gui.scrolling().title(Component.text("Public Mines")).rows(6)
        .pageSize(45).scrollType(ScrollType.VERTICAL).create();

    GuiItem back = ItemBuilder.from(Material.ARROW)
        .name(Component.text(ChatColor.GREEN + "<-"))
        .lore(Component.text(ChatColor.GRAY + "Go Back"))
        .asGuiItem(event -> {
      event.setCancelled(true);
      scrollingGui.previous();
    });

    GuiItem forward = ItemBuilder.from(Material.ARROW)
        .name(Component.text(ChatColor.GREEN + "->"))
        .lore(Component.text(ChatColor.GRAY + "Go Forward"))
        .asGuiItem(event -> {
      event.setCancelled(true);
      scrollingGui.next();
    });

    mineStorage.getMines().forEach((uuid, mine) -> {
      MineData mineData = mine.getMineData();
      boolean isOpen = mineData.isOpen();
      if (isOpen) {
        openAmount.getAndIncrement();
      }
    });

    mineStorage.getMines().forEach((uuid, mine) -> {
      MineData mineData = mine.getMineData();
      boolean isOpen = mineData.isOpen();
      if (isOpen) {
        UUID owner = mineData.getMineOwner();
        String name = Bukkit.getOfflinePlayer(owner).getName();

        GuiItem guiItem;
        if (name != null) {
          guiItem = ItemBuilder.from(Material.EMERALD).name(Component.text(name).color(NamedTextColor.GREEN))
              .lore(Component.text(ChatColor.GREEN + "Click to teleport")).asGuiItem(event -> {
                event.setCancelled(true);
                mine.teleport(player);
              });
          scrollingGui.addItem(guiItem);
        }
      }
    });

    if (openAmount.get() > 0) {
      scrollingGui.setItem(48, back);
      scrollingGui.setItem(50, forward);
      scrollingGui.open(player);
    } else {
      Gui gui = Gui.gui().title(Component.text("No Open Mines").color(NamedTextColor.DARK_RED))
          .create();
      gui.open(player);
    }
  }
}
