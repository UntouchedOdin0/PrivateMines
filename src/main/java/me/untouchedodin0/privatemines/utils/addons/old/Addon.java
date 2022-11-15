package me.untouchedodin0.privatemines.utils.addons.old;

import java.util.List;

public interface Addon {

    String getName();

    String getVersion();

    List<String> getDependencies();

    void onLoad();

    void onDisable();
}
