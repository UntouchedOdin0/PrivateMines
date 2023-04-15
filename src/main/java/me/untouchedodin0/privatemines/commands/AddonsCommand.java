package me.untouchedodin0.privatemines.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import java.util.Map;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.utils.addon.Addon;
import me.untouchedodin0.privatemines.utils.addon.AddonManager;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

@CommandAlias("addons")
public class AddonsCommand extends BaseCommand {

  PrivateMines privateMines = PrivateMines.getPrivateMines();
  Component component;

  @Default
  public void send(Player player) {
    MiniMessage miniMessage = MiniMessage.miniMessage();
    this.component = miniMessage.deserialize("<green>Addons: ");
    BukkitAudiences bukkitAudiences = privateMines.getAdventure();
    Audience audience = bukkitAudiences.player(player);

    Map<String, Addon> addons = AddonManager.getAddons();

    if (addons.isEmpty()) {
      Component noAddons = miniMessage.deserialize("<green>Addons: <red>None");
      audience.sendMessage(noAddons);
    } else {

      if (addons.size() < 2) {
        int count = 0;
        Builder message = Component.text().content(String.format("Addons: (%d): ", addons.size()));
        for (Map.Entry<String, Addon> entry : addons.entrySet()) {
          String name = entry.getKey();
          Addon addon = entry.getValue();

          TextComponent addonComponent = Component.text().content(name).color(NamedTextColor.GREEN)
              .hoverEvent(HoverEvent.showText(
                  Component.text().content("Name: " + addon.getName() + "\n").color(NamedTextColor.AQUA)
                      .append(Component.text("Version: " + addon.getVersion() + "\n").color(NamedTextColor.AQUA))
                      .append(Component.text("Description: " + addon.getDescription()).color(NamedTextColor.AQUA)))).build();
          message.append(addonComponent);
          if (++count < addons.size()) {
            message.append(Component.text(", "));
          }
        }
        audience.sendMessage(message);
      }
    }
  }

  @Subcommand("reload")
  @CommandCompletion("@addons")
  public void reload(Player player, @Single String addonList) {
    String[] addons = addonList.split(",");
    for (String add : addons) {
      String trimmed = add.trim();

      try {
        Addon addon = AddonManager.get(add);
        player.sendMessage(ChatColor.GOLD + String.format("Reloading %s..", trimmed));
        addon.onReload();
      } finally {
        player.sendMessage(ChatColor.GREEN + String.format("Successfully reloaded %s!", trimmed));
      }
    }
  }

  @Subcommand("disable")
  @CommandCompletion("@addons")
  public void disable(Player player, @Single String addonList) {
    String[] addons = addonList.split(",");
    for (String add : addons) {
      String trimmed = add.trim();

      try {
        Addon addon = AddonManager.get(add);
        player.sendMessage(ChatColor.GOLD + String.format("Disabling %s..", trimmed));
        addon.onDisable();
        AddonManager.remove(add);
      } finally {
        player.sendMessage(ChatColor.GREEN + String.format("Successfully disabled %s!", trimmed));
      }
    }
  }
}
