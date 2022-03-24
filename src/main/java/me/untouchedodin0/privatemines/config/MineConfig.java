package me.untouchedodin0.privatemines.config;

import me.untouchedodin0.kotlin.mine.type.MineType;
import me.untouchedodin0.privatemines.configmanager.annotations.ConfigMappable;

import java.util.HashMap;
import java.util.Map;

@ConfigMappable
public class MineConfig {

    public static Map<String, MineType> mineTypes = new HashMap<>();
}
