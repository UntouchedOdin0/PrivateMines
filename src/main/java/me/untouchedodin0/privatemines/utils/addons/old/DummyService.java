package me.untouchedodin0.privatemines.utils.addons.old;

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