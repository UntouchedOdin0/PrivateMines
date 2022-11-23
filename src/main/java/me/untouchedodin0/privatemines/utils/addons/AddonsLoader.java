package me.untouchedodin0.privatemines.utils.addons;

import me.untouchedodin0.privatemines.PrivateMines;
import org.apache.commons.lang.Validate;

import java.io.File;

public class AddonsLoader implements AddonLoader {

    PrivateMines privateMines = PrivateMines.getPrivateMines();

    @Override
    public Addon load(File file) {
        Validate.notNull(file, String.format("Cannot find file %s!", file.getName()));

        if (!file.exists()) {
            privateMines.getLogger().warning(String.format("File %s does not exist!", file.getName()));
        }


        return null;
    }
}
