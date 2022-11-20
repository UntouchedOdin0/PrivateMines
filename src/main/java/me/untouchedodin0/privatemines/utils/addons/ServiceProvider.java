package me.untouchedodin0.privatemines.utils.addons;

import java.util.ServiceLoader;

public abstract class ServiceProvider {

    public static ServiceProvider getDefault() {

        // load our addon
        ServiceLoader<ServiceProvider> serviceLoader =
                ServiceLoader.load(ServiceProvider.class);

        System.out.println("service loader " + serviceLoader);
        return new ServiceProvider() {
            @Override
            public String getMessage() {
                return null;
            }

            @Override
            public void onLoad() {
            }
        };
    }

        // checking if load was successful
//        for (ServiceProvider provider : serviceLoader) {
//            System.out.println("provider " + provider);
//            return provider;
//        }
//        throw new Error("Something is wrong with registering the addon");


    public abstract String getMessage();

    public abstract void onLoad();
}
