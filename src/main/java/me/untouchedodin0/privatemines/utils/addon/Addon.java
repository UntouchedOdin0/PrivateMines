package me.untouchedodin0.privatemines.utils.addon;

import lombok.NonNull;
import me.untouchedodin0.privatemines.PrivateMines;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public abstract class Addon {

    private PrivateMines privateMines;
    private File addonJarFile;
    private String name;
    private String version;
    private String author;
    private String description;
    private File configFile;
    private YamlConfiguration config;
    private boolean enabled;

    public abstract void onEnable();

    public abstract void onDisable();

    public abstract void onReload();

    public File getAddonJarFile() {
        return addonJarFile;
    }

    public void setAddonJarFile(File addonJarFile) {
        this.addonJarFile = addonJarFile;
    }

    public final String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public final String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public final String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public final String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public final File getConfigFile() {
        return configFile;
    }

    public void setConfigFile(File configFile) {
        this.configFile = configFile;
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    public final boolean isEnabled() {
        return enabled;
    }

    public final File getDataFolder() {
        return new File(privateMines.getDataFolder(), "addons/" + name);
    }

    private InputStream getResource(String resource) {
        try {
            final ZipFile zipFile = new ZipFile(this.addonJarFile.getAbsoluteFile());
            final ZipEntry zipEntry = zipFile.getEntry(resource);
            if (zipEntry == null) throw new ZipException("Resource not found: " + resource);
            return zipFile.getInputStream(zipEntry);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveResource(@NonNull String pathToFile, final boolean shouldReplace) {
        pathToFile = pathToFile.replace('\\', '/');
        final InputStream inputStream = getResource(pathToFile);
        if (inputStream == null) throw new RuntimeException("The resource " + pathToFile + " for addon " + name + " does not exist");
        final File out = new File(getDataFolder(), pathToFile);
        final int lastPath = pathToFile.lastIndexOf('/');
        final File outDir = new File(getDataFolder(), pathToFile.substring(0, Math.max(lastPath, 0)));
        boolean makeDir;
        if (!outDir.exists()) makeDir = outDir.mkdirs();

        if (!out.exists() && shouldReplace) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            BufferedWriter writer;
            String line;
            try {
                writer = new BufferedWriter(new FileWriter(out));
                while ((line = reader.readLine()) != null) {
                    writer.write(line + "\n");
                }
                reader.close();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else throw new RuntimeException("The resource " + pathToFile + " for addon " + name + " already exists");
    }

    public void saveDefaultConfig() {
        final File file = new File(getDataFolder(), "config.yml");
        if (!file.exists()) {
            saveResource("config.yml", true);
            this.configFile = new File(getDataFolder(), "config.yml");
            this.config = YamlConfiguration.loadConfiguration(configFile);
        }
    }

    public YamlConfiguration getDefaultConfig() {
        if (this.config == null) {
            this.reloadConfig();
        }
        return this.config;
    }

    public void reloadConfig() {
        this.config = YamlConfiguration.loadConfiguration(configFile);
    }

    public void saveConfig() {
        try {
            this.config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
