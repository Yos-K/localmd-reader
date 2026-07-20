package io.github.yosk.mdlite.infrastructure;

public interface ProPurchaseCacheStorage {
    String getString(String key, String defaultValue);

    long getLong(String key, long defaultValue);

    void putString(String key, String value);

    void putLong(String key, long value);
}
