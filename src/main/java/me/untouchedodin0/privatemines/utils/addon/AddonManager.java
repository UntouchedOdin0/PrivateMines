package me.untouchedodin0.privatemines.utils.addon;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import me.clip.placeholderapi.util.FileUtil;
import me.clip.placeholderapi.util.Msg;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AddonManager {

  @NotNull
  public CompletableFuture<@Nullable Class<? extends Addon>> findExpansionInFile(
      @NotNull final File file) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        final Class<? extends Addon> expansionClass = FileUtil.findClass(file, Addon.class);

        if (expansionClass == null) {
          Msg.severe("Failed to load expansion %s, as it does not have a class which"
              + " extends Addon", file.getName());
          return null;
        }

        return expansionClass;
      } catch (VerifyError | NoClassDefFoundError e) {
        Msg.severe("Failed to load expansion %s (is a dependency missing?)", e, file.getName());
        return null;
      } catch (Exception e) {
        throw new CompletionException(e.getMessage() + " (expansion file: " + file.getAbsolutePath() + ")", e);
      }
    });
  }

  public Optional<Addon> register(
      final CompletableFuture<@Nullable Class<? extends Addon>> clazz) {
    try {
      final Addon expansion = createAddonInstance(clazz);

      if(expansion == null){
        return Optional.empty();
      }
      return Optional.of(expansion);
    } catch (LinkageError | NullPointerException ex) {
      final String reason;

      if (ex instanceof LinkageError) {
        reason = " (Is a dependency missing?)";
      } else {
        reason = " - One of its properties is null which is not allowed!";
      }

      try {
        Msg.severe("Failed to load expansion class %s%s", ex, clazz.get().getSimpleName(), reason);
      } catch (InterruptedException | ExecutionException e) {
        throw new RuntimeException(e);
      }
    }

    return Optional.empty();
  }

  public Addon createAddonInstance(
      final CompletableFuture<@Nullable Class<? extends Addon>> clazz) throws LinkageError {
    try {
      return clazz.get().getDeclaredConstructor().newInstance();
    } catch (final Exception ex) {
      if (ex.getCause() instanceof LinkageError) {
        throw ((LinkageError) ex.getCause());
      }

      Msg.warn("There was an issue with loading an expansion.");
      return null;
    }
  }
}
