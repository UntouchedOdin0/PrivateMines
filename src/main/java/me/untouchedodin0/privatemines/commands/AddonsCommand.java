package me.untouchedodin0.privatemines.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.utils.addon.Addon;
import me.untouchedodin0.privatemines.utils.addon.AddonsManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@CommandAlias("addons")
public class AddonsCommand extends BaseCommand {
  PrivateMines privateMines = PrivateMines.getPrivateMines();
  AddonsManager addonsManager = privateMines.getAddonsManager();
  Component component;

  @Default
  public void send(Player player) {
    MiniMessage miniMessage = MiniMessage.miniMessage();
    Component two = miniMessage.deserialize("<yellow>Test");
    Component comma = miniMessage.deserialize(", ");
//    Component added = component.append(two);
    this.component = miniMessage.deserialize("<green>Addons: ");

    Map<String, Addon> addons = addonsManager.getAddons();
    List<Addon> addonList = new ArrayList<>();

    addons.forEach((string, addon) -> {
      addonList.add(addon);
//      Component addonComponent = miniMessage.deserialize(String.format("<hover:show_text:'<red>test'><yellow>Hi"));
//      this.component = component.append(addonComponent);
    });

    Bukkit.broadcastMessage("addon list " + addonList);
    for (Addon addon : addonList) {
      Component test = miniMessage.deserialize(String.format("<hover:show_text:'<red>v%s'><yellow>%s", addon.version(), addon.name()));
      component = component.append(test);
      component = component.append(comma);
    }

//    for (int i = 0; i < 5; i++) {
//      Component test = miniMessage.deserialize(String.format("<hover:show_text:'<red>test'><yellow>Hi"));
//      component = component.append(test);
//      component = component.append(comma);
//    }

    BukkitAudiences bukkitAudiences = privateMines.getAdventure();
    Audience audience = bukkitAudiences.player(player);
    audience.sendMessage(component);
  }
}
