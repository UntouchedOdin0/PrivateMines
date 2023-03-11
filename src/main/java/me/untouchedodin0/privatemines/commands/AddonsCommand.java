package me.untouchedodin0.privatemines.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.utils.addon.Addon;
import me.untouchedodin0.privatemines.utils.addon.AddonAPI;
import me.untouchedodin0.privatemines.utils.addon.AddonsManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
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
    Component singularAddon;
    BukkitAudiences bukkitAudiences = privateMines.getAdventure();
    Audience audience = bukkitAudiences.player(player);

    Map<String, Addon> addons = addonsManager.getAddons();
    List<Addon> addonList = new ArrayList<>();

    addons.forEach((string, addon) -> {
      addonList.add(addon);
//      Component addonComponent = miniMessage.deserialize(String.format("<hover:show_text:'<red>test'><yellow>Hi"));
//      this.component = component.append(addonComponent);
    });


    if (addonList.isEmpty()) {
      Component noAddons = miniMessage.deserialize("<green>Addons: <red>None");
      audience.sendMessage(noAddons);
    } else {

      if (addonList.size() < 2) {
        Addon addon = addonList.get(0);
        Component singular = miniMessage.deserialize("<green>Addons: ");
        Component test = miniMessage.deserialize(String.format("<hover:show_text:'<red>%s\n"
            + "v%s'><yellow>%s", addon.description(), addon.version(), addon.name()));
        singularAddon = singular.append(test);
        audience.sendMessage(singularAddon);
      } else {
        for (Addon addon : addonList) {
          Component test = miniMessage.deserialize(String.format("<hover:show_text:'<red>%s\n"
              + "v%s'><yellow>%s", addon.description(), addon.version(), addon.name()));
          component = component.append(test);
          component = component.append(comma);
        }
        audience.sendMessage(component);
      }
    }
  }

  @Subcommand("reload")
  public void reload(CommandSender sender, String string) {
    sender.sendMessage("reloading " + string);
    AddonAPI.reload(sender, string);
  }
}
