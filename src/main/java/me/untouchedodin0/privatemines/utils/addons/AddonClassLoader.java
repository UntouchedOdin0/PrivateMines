package me.untouchedodin0.privatemines.utils.addons;

import org.apache.commons.lang.Validate;
import org.bukkit.plugin.InvalidPluginException;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A ClassLoader for addons, to allow shared classes across multiple plugins
 */

final class AddonClassLoader extends URLClassLoader {
    private final AddonLoader loader;
    private final Map<String, Class<?>> classes = new ConcurrentHashMap<String, Class<?>>(); // Spigot
    private final AddonDescriptionFile description;
    private final File dataFolder;
    private final File file;
    final Addon addon;
    private Addon addonInit;
    private IllegalStateException pluginState;

    // Spigot Start
    static
    {
        try
        {
            java.lang.reflect.Method method = ClassLoader.class.getDeclaredMethod( "registerAsParallelCapable" );
            if ( method != null )
            {
                boolean oldAccessible = method.isAccessible();
                method.setAccessible( true );
                method.invoke( null );
                method.setAccessible( oldAccessible );
                org.bukkit.Bukkit.getLogger().log( java.util.logging.Level.INFO, "Set PluginClassLoader as parallel capable" );
            }
        } catch ( NoSuchMethodException ex )
        {
            // Ignore
        } catch ( Exception ex )
        {
            org.bukkit.Bukkit.getLogger().log( java.util.logging.Level.WARNING, "Error setting PluginClassLoader as parallel capable", ex );
        }
    }
    // Spigot End

    AddonClassLoader(final AddonsLoader loader, final ClassLoader parent, final AddonDescriptionFile description, final File dataFolder, final File file) throws InvalidPluginException, MalformedURLException {
        super(new URL[] {file.toURI().toURL()}, parent);
        Validate.notNull(loader, "Loader cannot be null");

        this.loader = loader;
        this.description = description;
        this.dataFolder = dataFolder;
        this.file = file;

        try {
            Class<?> jarClass;
            try {
                jarClass = Class.forName(description.getMain(), true, this);
            } catch (ClassNotFoundException ex) {
                throw new InvalidPluginException("Cannot find main class `" + description.getMain() + "'", ex);
            }

            Class<? extends Addon> addonClass;
            try {
                addonClass = jarClass.asSubclass(Addon.class);
            } catch (ClassCastException ex) {
                try {
                    throw new InvalidAddonException("main class `" + description.getMain() + "' does not extend Addon", ex);
                } catch (InvalidAddonException e) {
                    throw new RuntimeException(e);
                }
            }

            addon = addonClass.newInstance();
        } catch (IllegalAccessException ex) {
            throw new InvalidPluginException("No public constructor", ex);
        } catch (InstantiationException ex) {
            throw new InvalidPluginException("Abnormal plugin type", ex);
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, true);
    }

    Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
        if (name.startsWith("org.bukkit.") || name.startsWith("net.minecraft.")) {
            throw new ClassNotFoundException(name);
        }
        Class<?> result = classes.get(name);

        if (result == null) {
            if (checkGlobal) {
                result = loader.getClassByName(name);
            }

            if (result == null) {
                result = super.findClass(name);

                if (result != null) {
                    loader.setClass(name, result);
                }
            }

            classes.put(name, result);
        }

        return result;
    }

    Set<String> getClasses() {
        return classes.keySet();
    }

    synchronized void initialize(Addon addon) {
        Validate.notNull(addon, "Initializing addon cannot be null");
        Validate.isTrue(addon.getClass().getClassLoader() == this, "Cannot initialize addon outside of this class loader");
        if (this.addon != null || this.addonInit != null) {
            throw new IllegalArgumentException("Addon already initialized!", pluginState);
        }

        pluginState = new IllegalStateException("Initial initialization");
    }
}