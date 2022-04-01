package me.untouchedodin0.privatemines.config;

import me.untouchedodin0.kotlin.mine.type.MineType;
import org.bukkit.Bukkit;
import redempt.redlib.config.annotations.ConfigMappable;
import redempt.redlib.config.annotations.ConfigPostInit;

import java.util.HashMap;
import java.util.Map;

@ConfigMappable
public class MineConfig {

    public static Map<String, MineType> mineTypes = new HashMap<>();

    public static Map<String, MineType> getMineTypes() {
        return mineTypes;
    }

    @ConfigPostInit
    public void postInit() {
        Bukkit.getLogger().info("Mine config post init test");
    }
}
