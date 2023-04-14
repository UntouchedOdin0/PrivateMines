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
  public CompletableFuture<@Nullable Class<? extends Addon>> findExpansionInFile(
      @NotNull final File file) {
    return CompletableFuture.supplyAsync(() -> {
      final PrivateMines privateMines = PrivateMines.getPrivateMines();

      try {
        final Class<? extends Addon> expansionClass = FileUtil.findClass(file, Addon.class);

        if (expansionClass == null) {
          privateMines.getLogger().warning(String.format("Failed to load addon %s, as it does not have a class which extends Addon",
              file.getName()));
          privateMines.getLogger().warning(String.format("Failed to load addon %s, as it does not have a class which extends Addon", file.getName()));
          return null;
        }

        return expansionClass;
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
      final Addon expansion = createAddonInstance(clazz);

      if (expansion == null) {
        return Optional.empty();
      }
      addons.put(expansion.getName(), expansion);
      return Optional.of(expansion);
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
}
