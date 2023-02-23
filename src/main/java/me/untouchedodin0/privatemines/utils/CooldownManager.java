package me.untouchedodin0.privatemines.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

  private final Map<UUID, Integer> cooldowns = new HashMap<>();

  public static final int DEFAULT_COOLDOWN = 15;

  public void setCooldown(UUID player, int time){
    if(time < 1) {
      cooldowns.remove(player);
    } else {
      cooldowns.put(player, time);
    }
  }

  public int getCooldown(UUID player){
    return cooldowns.getOrDefault(player, 0);
  }
}