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
