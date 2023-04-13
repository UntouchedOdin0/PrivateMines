package me.untouchedodin0.privatemines.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import java.util.Map;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.utils.addon.Addon;
import me.untouchedodin0.privatemines.utils.addon.AddonManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

@CommandAlias("addons")
public class AddonsCommand extends BaseCommand {
  PrivateMines privateMines = PrivateMines.getPrivateMines();
  Component component;

  @Default
  public void send(Player player) {
    MiniMessage miniMessage = MiniMessage.miniMessage();
    Component two = miniMessage.deserialize("<yellow>Test");
    Component comma = miniMessage.deserialize(", ");
    this.component = miniMessage.deserialize("<green>Addons: ");
    Component singularAddon;
    BukkitAudiences bukkitAudiences = privateMines.getAdventure();
    Audience audience = bukkitAudiences.player(player);

    Map<String, Addon> addons = AddonManager.getAddons();

    if (addons.isEmpty()) {
      Component noAddons = miniMessage.deserialize("<green>Addons: <red>None");
      audience.sendMessage(noAddons);
    } else {

      if (addons.size() < 2) {
        Addon addon = addons.values().stream().findFirst().get();
        Component singular = miniMessage.deserialize("<green>Addons: ");
        Component test = miniMessage.deserialize(String.format("<hover:show_text:'<red>%s\n"
            + "v%s'><yellow>%s", addon, addon.getVersion(), addon.getName()));
        singularAddon = singular.append(test);
        audience.sendMessage(singularAddon);
      } else {

        addons.forEach((s, addon) -> {
          Component addonComponent = miniMessage.deserialize(String.format("<hover:show_text:'<red>%s\n"
              + "v%s'>", addon.getName(), addon.getVersion()));
          component = component.append(addonComponent);
          component = component.append(comma);
        });
        audience.sendMessage(component);
      }
    }
  }
}
