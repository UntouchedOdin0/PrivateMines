package me.untouchedodin0.privatemines.utils.version;

import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionUtils {

    private static int getMidVersion() {
        // Credits to Redempt for this
        Pattern pattern = Pattern.compile("1\\\\.([0-9]+)");
        Matcher matcher = pattern.matcher(Bukkit.getBukkitVersion());
        matcher.find();
        return Integer.parseInt(matcher.group(1));
    }
}
