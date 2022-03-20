package me.untouchedodin0.privatemines.config;

import me.untouchedodin0.privatemines.configmanager.annotations.ConfigMappable;
import me.untouchedodin0.privatemines.type.MineType;

import java.util.HashMap;
import java.util.Map;

@ConfigMappable
public class MineConfig {

    public static Map<String, MineType> mineTypes = new HashMap<>();

}
