package me.untouchedodin0.privatemines.utils.addon;

import lombok.NonNull;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.utils.exceptions.InvalidAddonException;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AddonLoader {

    private final PrivateMines privateMines = PrivateMines.getPrivateMines();
    private final AddonManager addonManager = privateMines.getAddonManager();

    public AddonLoader() {
        final File addonFolder = new File(privateMines.getDataFolder(), "addons");
        final PathMatcher jarMatcher = FileSystems.getDefault().getPathMatcher("glob:**/*.jar"); // Credits to Brister Mitten
        Path path = privateMines.getAddonsDirectory();

        privateMines.getLogger().info("Loading addons...");

        CompletableFuture.runAsync(() -> {
            try (Stream<Path> paths = Files.walk(path).filter(jarMatcher::matches)) {
                paths.forEach(streamPath -> {
                    File file = streamPath.toFile();
                    privateMines.getLogger().info("Loading addon file " + file.getName() + "....");
                    try {
                        URL url = file.toURI().toURL();
                        URLClassLoader classLoader = (URLClassLoader)ClassLoader.getSystemClassLoader();
                        Method method = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                        method.setAccessible(true);
                        method.invoke(classLoader, url);

                        privateMines.getLogger().info("URL: " + url);
                        privateMines.getLogger().info("ClassLoader: " + classLoader);
                        privateMines.getLogger().info("Method: " + method);

                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
//                    try (ZipFile zipFile = new ZipFile(file)) {
//                        final InputStream inputStream = zipFile.getInputStream(zipFile.getEntry("addon.yml"));
//                        if (inputStream == null) {
//                            privateMines.getLogger().warning("Could not find addon.yml in " + file.getName());
//                        } else {
//                            final YamlConfiguration addonConfig = this.getYamlFromStream(inputStream);
//                        }
//                    } catch (IOException e) {
//                        privateMines.getLogger().warning("Could not load addon.yml in " + file.getName());
//                    }
//                    Addon addon = new Addon() {
//                        @Override
//                        public void onEnable() {
//                            privateMines.getLogger().info("Addon: " + getName() + " has been enabled!");
//                        }
//
//                        @Override
//                        public void onDisable() {
//
//                        }
//
//                        @Override
//                        public void onReload() {
//
//                        }
//                    };
//                });
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
                });
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

//        for (File addonFile : addonFiles) {
//            if (!addonFile.getName().endsWith(".jar")) return;
//            privateMines.getLogger().info("Loading addon file: " + addonFile);
//
//            try (ZipFile zipFile = new ZipFile(addonFile)) {
//                final InputStream inputStream = zipFile.getInputStream(zipFile.getEntry("addon.yml"));
//                if (inputStream == null) {
//                    privateMines.getLogger().warning("Could not find addon.yml in " + addonFile.getName());
//                } else {
//                    final YamlConfiguration addonConfig = this.getYamlFromStream(inputStream);
//                    final Addon addon = this.loadAddon(addonFile, addonConfig);
//                    addonManager.addAddon(addon);
//                }
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
    }

    /**
     * Gets a File (InputStream) of a ZipFile
     * @param jarFile ZipFile of file (Addon.JAR)
     * @param name Name of file to find
     * @return null when ot found
     */
    private InputStream getZipFile(final ZipFile jarFile, final String name) {
        final ZipEntry zipEntry = jarFile.getEntry(name);
        if (zipEntry == null) return null;
        try {
            return jarFile.getInputStream(zipEntry);
        } catch (IOException e) {
            throw new RuntimeException("File not found in jar: " + name);
        }
    }

    /**
     * Gets a YamlConfiguration from the contents of a InputStream
     * @param stream InputStream to use
     * @return YamlConfiguration
     */
    private YamlConfiguration getYamlFromStream(InputStream stream) {
        final StringBuilder contents = new StringBuilder();
        final YamlConfiguration config = new YamlConfiguration();

        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            String line = bufferedReader.readLine();
            while (line != null) {
                contents.append(line).append("\n");
                line = bufferedReader.readLine();
            }
            config.loadFromString(contents.toString());
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
        return config;
    }

    private Addon loadAddon(@NonNull final File file, @NonNull final YamlConfiguration config) {
        final String name = config.getString("name");
        final String mainClass = config.getString("main");
        final String version = config.getString("version");
        final String description = config.getString("description");
        final String author = config.getString("author");

        if (name == null)           throw new InvalidAddonException(String.format("Could not find name for addon %s", file.getName()));
        if (mainClass == null)           throw new InvalidAddonException(String.format("Could not find main class for addon %s", file.getName()));
        if (version == null)        throw new InvalidAddonException(String.format("Could not find version for addon %s", file.getName()));
        if (description == null)    throw new InvalidAddonException(String.format("Could not find description for addon %s", file.getName()));
        if (author == null)         throw new InvalidAddonException(String.format("Could not find author for addon %s", file.getName()));
        final ClassLoader classLoader = getClass().getClassLoader();
        Addon addon;
        try {
            final URLClassLoader urlClassLoader = new URLClassLoader(new URL[]{file.toURI().toURL()}, classLoader);
            final Class<?> clazz = Class.forName(mainClass, true, urlClassLoader);
            final Class<? extends Addon> extendedClass = clazz.asSubclass(Addon.class);
            addon = extendedClass.getDeclaredConstructor().newInstance();
        } catch (MalformedURLException | ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        privateMines.getLogger().info(String.format("Loaded addon %s", name));
        privateMines.getLogger().info(String.format("main class %s", mainClass));
        privateMines.getLogger().info(String.format("version %s", version));
        privateMines.getLogger().info(String.format("description %s", description));
        privateMines.getLogger().info(String.format("author %s", author));

        addon.setName(name);
        addon.setVersion(version);
        addon.setDescription(description);
        addon.setAuthor(author);
        return addon;
    }
}
