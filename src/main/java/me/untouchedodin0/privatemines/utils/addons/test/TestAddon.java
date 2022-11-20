package me.untouchedodin0.privatemines.utils.addons.test;

import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.utils.addons.ServiceProvider;

public class TestAddon {

    PrivateMines privateMines = PrivateMines.getPrivateMines();
    ServiceProvider serviceProvider = new ServiceProvider() {
        @Override
        public String getMessage() {
            return "hi";
        }

        @Override
        public void onLoad() {
            System.out.println("Loading Test Addon, message: " + getMessage());
        }
    };

    public ServiceProvider getServiceProvider() {
        return serviceProvider;
    }

    //    @Override
//    public void print() {
//        System.out.println("Print Test.");
//    }


//    @Override
//    public void print() {
//        System.out.println("Print Test.");
//    }


//    @Override
//    public String getName() {
//        return "Test";
//    }
//
//    @Override
//    public String getVersion() {
//        return "1.0";
//    }
//
//    @Override
//    public List<String> getDependencies() {
//        return List.of();
//    }
//
//    @Override
//    public void onLoad() {
//        privateMines.getLogger().info(String.format("%s onLoad", getName()));
//    }
//
//    @Override
//    public void onDisable() {
//        privateMines.getLogger().info(String.format("%s onDisable", getName()));
//    }
}
