package me.untouchedodin0.privatemines.utils.addons.old;

import com.google.auto.service.AutoService;

@AutoService(MyServiceDepricated.class)
public class DummyService implements MyServiceDepricated {

    @Override
    public String getName() {
        return "dummy";
    }

    @Override
    public int doManyThings(int limit) {
        return 0;
    }
}