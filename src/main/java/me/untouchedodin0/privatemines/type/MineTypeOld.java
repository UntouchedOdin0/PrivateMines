package me.untouchedodin0.privatemines.type;

import me.untouchedodin0.privatemines.configmanager.annotations.ConfigMappable;
import me.untouchedodin0.privatemines.configmanager.annotations.ConfigPath;
import org.bukkit.Material;

import java.util.Map;

@ConfigMappable
public class MineTypeOld {

    @ConfigPath
    private String name;
    private String file;
    private int resetTime;
    private double resetPercentage;
    private Map<Material, Double> materials;

    public String getName() {
        return name;
    }

    public String getFile() {
        return file;
    }

    public int getResetTime() {
        return resetTime;
    }

    public double getResetPercentage() {
        return resetPercentage;
    }

    public Map<Material, Double> getMaterials() {
        return materials;
    }
}
