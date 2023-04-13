/*
 * This file is part of PlaceholderAPI
 *
 * PlaceholderAPI
 * Copyright (c) 2015 - 2021 PlaceholderAPI Team
 *
 * PlaceholderAPI free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PlaceholderAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package me.untouchedodin0.privatemines.utils.addon;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import org.bukkit.Bukkit;

public class FileUtil {

  public static <T> Class<? extends T> findClass(final File file,
      final Class<T> clazz) throws IOException {
    if (!file.exists()) {
      return null;
    }

    final URL jar = file.toURI().toURL();
    final URLClassLoader loader = new URLClassLoader(new URL[]{jar}, clazz.getClassLoader());
    final List<String> matches = new ArrayList<>();
    final List<Class<? extends T>> classes = new ArrayList<>();

    try (final JarInputStream stream = new JarInputStream(jar.openStream())) {
      JarEntry jarEntry;

      Bukkit.getLogger().info("loader " + loader);
      Bukkit.getLogger().info("matches " + matches);
      Bukkit.getLogger().info("classes " + classes);
      Bukkit.getLogger().info("stream " + stream);

      while ((jarEntry = stream.getNextJarEntry()) != null) {
        String name = jarEntry.getName();
        if (name.isEmpty() | !name.endsWith("class")) {
          continue;
        }
        Bukkit.getLogger().info("jarEntry " + jarEntry);
        Bukkit.getLogger().info("matches " + matches);
        matches.add(name.substring(0, name.lastIndexOf('.')).replace('/', '.'));
        Bukkit.getLogger().info("matches " + matches);
      }
    }
    return null;
  }

  public static Addon createInstance(Class<? extends Addon> clazz) throws LinkageError {
    try {
      return clazz.getDeclaredConstructor().newInstance();
    } catch (Exception exception) {
      Bukkit.getLogger().info("Error loading expansion.");
      exception.printStackTrace();
      return null;
    }
  }

  public static Optional<Addon> register(final Class<? extends Addon> clazz) {
    try {
      {
        Bukkit.getLogger().info("clazz " + clazz);
        final Addon addon = createInstance(clazz);

        if (addon == null) {
          return Optional.empty();
        }

        Objects.requireNonNull(addon.getName(), "The addon name is null!");
        Objects.requireNonNull(addon.getAuthor(), "The addon author is null!");
        Objects.requireNonNull(addon.getVersion(), "The addon version is null!");

        return Optional.of(addon);
      }
    } catch (LinkageError e) {
      throw new RuntimeException(e);
    }
  }
}
