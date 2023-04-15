package me.untouchedodin0.privatemines.utils.addon;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import me.untouchedodin0.privatemines.PrivateMines;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AddonManager {

  public static Map<String, Addon> addons = new HashMap<>();

  @NotNull
  public static CompletableFuture<@Nullable Class<? extends Addon>> findExpansionInFile(
      @NotNull final File file) {
    return CompletableFuture.supplyAsync(() -> {
      final PrivateMines privateMines = PrivateMines.getPrivateMines();

      try {
        final Class<? extends Addon> addonClass = FileUtil.findClass(file, Addon.class);

        if (addonClass == null) {
          privateMines.getLogger().warning(String.format("Failed to load addon %s, as it does not have a class which extends Addon",
              file.getName()));
          privateMines.getLogger().warning(String.format("Failed to load addon %s, as it does not have a class which extends Addon", file.getName()));
          return null;
        }

        return addonClass;
      } catch (VerifyError | NoClassDefFoundError e) {
        privateMines.getLogger().warning(
            String.format("Failed to load addon %s (is a dependency missing?)", file.getName()));
        return null;
      } catch (Exception e) {
        throw new CompletionException(
            e.getMessage() + " (addon file: " + file.getAbsolutePath() + ")", e);
      }
    });
  }

  public Optional<Addon> register(final CompletableFuture<@Nullable Class<? extends Addon>> clazz) {
    try {
      final Addon addon = createAddonInstance(clazz);

      if (addon == null) {
        return Optional.empty();
      }
      addons.put(addon.getName(), addon);
      return Optional.of(addon);
    } catch (LinkageError | NullPointerException ex) {
      final String reason;

      if (ex instanceof LinkageError) {
        reason = " (Is a dependency missing?)";
      } else {
        reason = " - One of its properties is null which is not allowed!";
      }
      final PrivateMines privateMines = PrivateMines.getPrivateMines();

      try {
        privateMines.getLogger().warning(
            String.format("Failed to load addon class %s %s", clazz.get().getSimpleName(),
                reason));
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }

    return Optional.empty();
  }

  public Addon createAddonInstance(final CompletableFuture<@Nullable Class<? extends Addon>> clazz)
      throws LinkageError {
    final PrivateMines privateMines = PrivateMines.getPrivateMines();

    try {
      return clazz.get().getDeclaredConstructor().newInstance();
    } catch (final Exception ex) {
      if (ex.getCause() instanceof LinkageError) {
        throw ((LinkageError) ex.getCause());
      }

      privateMines.getLogger().warning("There was an issue with loading an addon.");
      return null;
    }
  }

  public static Map<String, Addon> getAddons() {
    return addons;
  }

  public static Addon get(String name) {
    return addons.get(name);
  }

  public static void remove(String name) {
    addons.remove(name);
  }
}
