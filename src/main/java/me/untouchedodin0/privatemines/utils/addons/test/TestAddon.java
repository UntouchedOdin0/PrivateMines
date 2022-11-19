package me.untouchedodin0.privatemines.utils.addons.test;

import com.google.auto.service.AutoService;
import me.untouchedodin0.privatemines.PrivateMines;
import me.untouchedodin0.privatemines.utils.addons.Service;

@AutoService(Service.class)
public class TestAddon implements Service {

    PrivateMines privateMines = PrivateMines.getPrivateMines();

    @Override
    public void print() {
        System.out.println("Print Test.");
    }

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
