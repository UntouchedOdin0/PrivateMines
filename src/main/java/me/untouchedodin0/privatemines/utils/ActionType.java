package me.untouchedodin0.privatemines.utils;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.jetbrains.annotations.NotNull;

public enum ActionType {

  RESET("reset"), RESET_TELEPORT("resetteleport"), TELEPORT("teleport"), ADD_FRIEND(
      "addfriend"), EXPAND("expand"), OWNMINE("ownmine"), PUBLICMINES("publicmines");

  private static final Map<String, ActionType> BY_NAME;
  private final String identifier;

  static {
    BY_NAME = Arrays.stream(values())
        .collect(Collectors.toMap(e -> e.name().toUpperCase(Locale.ROOT), Function.identity()));
  }

  ActionType(@NotNull String identifier) {
    this.identifier = identifier;
  }

  public static ActionType getByIdentifier(String identifier) {
    return BY_NAME.values().stream().filter(e -> e.identifier.equals(identifier)).findFirst()
        .orElse(null);
  }

  public static ActionType getByStart(String string) {
    Objects.requireNonNull(string);
    return BY_NAME.values().stream().map(ActionType::getIdentifier).filter(string::startsWith)
        .findFirst().map(ActionType::getByIdentifier).orElse(null);
  }

  public String getIdentifier() {
    return identifier;
  }
}
