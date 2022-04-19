package me.untouchedodin0.privatemines.utils.addon;

import me.untouchedodin0.privatemines.PrivateMines;

public class AddonLoader {

    PrivateMines privateMines = PrivateMines.getPrivateMines();

    //todo implement this

//    public List<Class> loadAddons() {
//        File[] addons = new File("./addons").listFiles(file -> file.getName().endsWith(".jar"));
//        if (addons != null) {
//            List<URL> totalAddons = new ArrayList<>(addons.length);
//            for (File addon : addons) {
//                try {
//                    totalAddons.add(addon.toURI().toURL());
//                } catch (MalformedURLException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//            URLClassLoader urlClassLoader = new URLClassLoader(totalAddons.toArray(new URL[0]));
//        }
//    }
}
