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

package me.untouchedodin0.privatemines;

import java.io.File;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import me.untouchedodin0.kotlin.mine.storage.MineStorage;
import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.factory.MineFactory;
import me.untouchedodin0.privatemines.mine.Mine;
import me.untouchedodin0.privatemines.utils.addon.Addon;
import me.untouchedodin0.privatemines.utils.addon.AddonManager;
import org.bukkit.Location;
import org.jetbrains.annotations.Nullable;

public class PrivateMinesAPI {

  PrivateMines privateMines = PrivateMines.getPrivateMines();
  MineStorage mineStorage = privateMines.getMineStorage();
  MineFactory mineFactory = privateMines.getMineFactory();

  public Mine getMine(UUID uuid) {
    if (!mineStorage.hasMine(uuid)) {
      return null;
    }
    return mineStorage.get(uuid);
  }

  public Mine getAtLocation(Location location) {
    return mineStorage.getClosest(location);
  }

  public Map<UUID, Mine> getMines() {
    return mineStorage.getMines();
  }

  public boolean hasMine(UUID uuid) {
    return mineStorage.hasMine(uuid);
  }

  public void createMine(UUID uuid, Location location, MineType mineType) {
    mineFactory.createUpgraded(uuid, location, mineType);
  }

  public void loadAddon(File file) {
    AddonManager addonManager = PrivateMines.getPrivateMines().getAddonManager();
    CompletableFuture<@Nullable Class<? extends Addon>> addon = AddonManager.findExpansionInFile(
        file);

    Optional<Addon> loaded = addonManager.register(addon);

    if (loaded.isPresent()) {
      Addon addonLoaded = loaded.get();
      addonLoaded.onEnable();
    }
  }
}
