package me.untouchedodin0.privatemines.utils.version;

import org.bukkit.Bukkit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * {@link VersionUtils} is utilized to retrieve necessary information for proper
 * version compatibility.
 * @author Redempt
 */
public class VersionUtils {

    private static int getMidVersion() {
        Pattern pattern = Pattern.compile("1\\.([0-9]+)");
        Matcher matcher = pattern.matcher(Bukkit.getBukkitVersion());
        boolean found = matcher.find();
        if (!found) return 0;
        return Integer.parseInt(matcher.group(1));
    }

    public static final int MID_VERSION = getMidVersion();

}
