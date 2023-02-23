package me.untouchedodin0.privatemines.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.fasterxml.jackson.databind.ser.Serializers.Base;
import dev.triumphteam.gui.builder.gui.ScrollingBuilder;
import dev.triumphteam.gui.components.ScrollType;
import dev.triumphteam.gui.guis.Gui;
import dev.triumphteam.gui.guis.ScrollingGui;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.privatemines.PrivateMines;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import redempt.redlib.inventorygui.InventoryGUI;

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
        .scrollType(ScrollType.HORIZONTAL)
        .create();
    scrollingGui.open(player);
  }
}
