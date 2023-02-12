package me.untouchedodin0.privatemines.storage;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum StorageType {
  YAML("YAML"),
  SQLite("SQLite");

  private final String name;

  private static final Map<String, StorageType> ENUM_MAP;

  StorageType(String name) {
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  // Build an immutable map of String name to enum pairs.
  // Any Map impl can be used.

  static {
    Map<String, StorageType> map = new ConcurrentHashMap<>();
    for (StorageType instance : StorageType.values()) {
      map.put(instance.getName().toLowerCase(), instance);
    }
    ENUM_MAP = Collections.unmodifiableMap(map);
  }

  public static StorageType get(String name) {
    return ENUM_MAP.get(name.toLowerCase());
  }
}
