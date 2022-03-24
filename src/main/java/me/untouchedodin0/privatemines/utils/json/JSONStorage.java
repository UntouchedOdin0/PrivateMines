package me.untouchedodin0.privatemines.utils.json;

/**
 * @author Redempt
 */

interface JSONStorage {

    JSONStorage getParent();
    void setParent(JSONStorage obj);
    void add(String key, Object value);
    String getTempKey();
    void setTempKey(String value);
}