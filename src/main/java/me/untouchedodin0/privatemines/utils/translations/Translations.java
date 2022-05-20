package me.untouchedodin0.privatemines.utils.translations;

import com.convallyria.languagy.api.language.Language;
import com.convallyria.languagy.api.language.key.TranslationKey;
import com.convallyria.languagy.api.language.translation.Translation;
import me.untouchedodin0.privatemines.PrivateMines;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;

public enum Translations {

    MINE_RESET(TranslationKey.of("minereset"));

    private final TranslationKey key;

    Translations(TranslationKey key) {
        this.key = key;
    }

    public TranslationKey getKey() {
        return key;
    }

    public void send(Player player, Object... values) {
        Translation message = getTranslation(player);
        message.format(values);
        message.send();
    }

    public String get(Player player) {
        return getTranslation(player).colour().get(0);
    }

    public Translation getTranslation(Player player) {
        return PrivateMines.getPrivateMines().getTranslator().getTranslationFor(player, key);
    }

    public static void generateLanguageFiles(PrivateMines privateMines) {
        File languageFile = new File(privateMines.getDataFolder() + "/locales/");
        languageFile.mkdirs();

        for (Language language : Language.values()){
            try {
                privateMines.saveResource("locales/" + language.getKey().getCode() + ".yml", false);
                privateMines.getLogger().info("Generated language file for " + language.getKey().getCode());
            } catch (IllegalArgumentException e){
                e.printStackTrace();
            }
            File file = new File(privateMines.getDataFolder() + "/locales/"  + language.getKey().getCode() + ".yml");
            if (file.exists()) {
                FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(file);
                for (Translations key : Translations.values()) {
                    if (fileConfiguration.get(key.toString().toLowerCase()) == null) {
                        privateMines.getLogger().info("No translation for " + key.toString().toLowerCase() + " in " + language.getKey().getCode() + ".yml");
                    }
                }
            }
        }
    }
}
