package io.github.yosk.mdlite.presentation;

import android.content.Context;
import android.content.SharedPreferences;
import io.github.yosk.mdlite.infrastructure.ProPurchaseCacheStorage;

final class SharedPreferencesProPurchaseCacheStorage implements ProPurchaseCacheStorage {
    private static final String PREFS = "pro_purchase_cache";

    private final Context context;

    SharedPreferencesProPurchaseCacheStorage(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null.");
        }
        this.context = context;
    }

    @Override
    public String getString(String key, String defaultValue) {
        return prefs().getString(key, defaultValue);
    }

    @Override
    public long getLong(String key, long defaultValue) {
        return prefs().getLong(key, defaultValue);
    }

    @Override
    public void putString(String key, String value) {
        prefs().edit().putString(key, value).apply();
    }

    @Override
    public void putLong(String key, long value) {
        prefs().edit().putLong(key, value).apply();
    }

    private SharedPreferences prefs() {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }
}
