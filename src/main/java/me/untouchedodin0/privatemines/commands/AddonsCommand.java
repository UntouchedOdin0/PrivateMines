/**
 * MIT License
 * <p>
 * Copyright (c) 2021 - 2023 Kyle Hicks
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package me.untouchedodin0.privatemines.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Single;
import co.aikar.commands.annotation.Subcommand;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
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
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
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

      int count = 0;
      Builder message = Component.text().content(String.format("Addons: (%d): ", addons.size()));
      for (Map.Entry<String, Addon> entry : addons.entrySet()) {
        String name = entry.getKey();
        Addon addon = entry.getValue();

        TextComponent addonComponent = Component.text().content(name).color(NamedTextColor.GREEN)
            .hoverEvent(HoverEvent.showText(
                Component.text().content("Name: " + addon.getName() + "\n")
                    .color(NamedTextColor.AQUA).append(
                        Component.text("Version: " + addon.getVersion() + "\n")
                            .color(NamedTextColor.AQUA)).append(
                        Component.text("Description: " + addon.getDescription())
                            .color(NamedTextColor.AQUA)))).build();
        message.append(addonComponent);
        if (++count < addons.size()) {
          message.append(Component.text(", "));
        }
      }
      audience.sendMessage(message);
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

  @Subcommand("load")
  public void load(Player player, String string) {
    final PathMatcher jarMatcher = FileSystems.getDefault()
        .getPathMatcher("glob:**/*.jar"); // Credits to Brister Mitten
    Path addonsFolder = privateMines.getAddonsDirectory();
    List<Path> matchingFiles = new ArrayList<>();

    try {
      Files.walkFileTree(addonsFolder, new SimpleFileVisitor<>() {
        @Override
        public FileVisitResult visitFile(Path filePath, BasicFileAttributes attributes)
            throws IOException {
          String fileName = filePath.getFileName().toString().toLowerCase(); // Convert to lowercase
          String search = string.toLowerCase(); // Convert search string to lowercase

          if (fileName.contains(search)) {
            matchingFiles.add(filePath);
          }
          return FileVisitResult.CONTINUE;
        }
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    Bukkit.broadcastMessage("matching files? " + matchingFiles);

//    AddonManager addonManager = privateMines.getAddonManager();
//    Path path = addonsFolder.resolve(string);
//    File file = path.toFile();
//    CompletableFuture<Class<? extends Addon>> addon =
//        AddonManager.findExpansionInFile(file);
//
//    Bukkit.broadcastMessage("" + path);
//    Bukkit.broadcastMessage("" + file.exists());
//    Bukkit.broadcastMessage("" + addon);
//
//    Optional<Addon> loadedAddon = addonManager.register(addon);
//    if (loadedAddon.isPresent()) {
//      Addon loaded = loadedAddon.get();
//
//    }

//    try (Stream<Path> paths = Files.walk(addonsFolder).filter(jarMatcher::matches)) {
//      paths.forEach(jar -> {
//        File file = jar.toFile();
//        CompletableFuture<Class<? extends Addon>> addon = AddonManager.findExpansionInFile(
//            file
//        );
//        Optional<Addon> loaded = addonManager.register(addon);
//
//        if (loaded.isPresent()) {
//          Addon addonLoaded = loaded.get();
//          addonLoaded.onEnable();
//        }
//      });
//    } catch (IOException e) {
//      throw new RuntimeException(e);
//    }
  }
}
