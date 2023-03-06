package me.untouchedodin0.privatemines.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import me.untouchedodin0.privatemines.PrivateMines;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;

@CommandAlias("addons")
public class AddonsCommand extends BaseCommand {

  PrivateMines privateMines = PrivateMines.getPrivateMines();
  @Default
  public void send(Player player) {
    MiniMessage miniMessage = MiniMessage.miniMessage();
    Component component = miniMessage.deserialize("<green>Addons: ");
    Component two = miniMessage.deserialize("<yellow>Test");
    Component added = component.append(two);

    BukkitAudiences bukkitAudiences = privateMines.getAdventure();
    Audience audience = bukkitAudiences.player(player);
    audience.sendMessage(added);
  }
}
