package me.untouchedodin0.privatemines.config;

import me.untouchedodin0.kotlin.menu.Menu;
import redempt.redlib.config.annotations.ConfigMappable;

import java.util.LinkedHashMap;
import java.util.Map;

@ConfigMappable
public class MenuConfig {

    public static Map<String, Menu> menus = new LinkedHashMap<>();

    public static Map<String, Menu> getMenus() {
        return menus;
    }
}
