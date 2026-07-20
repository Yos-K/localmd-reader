package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.ProPurchaseCacheEntry;

public final class ProPurchaseCacheStore {
    private final ProPurchaseCacheStorage storage;

    public ProPurchaseCacheStore(ProPurchaseCacheStorage storage) {
        if (storage == null) {
            throw new IllegalArgumentException("Purchase cache storage must not be null.");
        }
        this.storage = storage;
    }

    public void save(ProPurchaseCacheEntry entry) {
        ProPurchaseCachePreferences preferences = ProPurchaseCachePreferences.fromEntry(entry);
        storage.putString(preferences.stateKey(), preferences.stateValue());
        storage.putLong(preferences.verifiedAtKey(), preferences.verifiedAtValue());
    }

    public ProPurchaseCacheEntry load() {
        ProPurchaseCachePreferences defaults =
                ProPurchaseCachePreferences.fromEntry(ProPurchaseCacheEntry.restore("unknown", 0L));
        return ProPurchaseCachePreferences.restoreEntry(
                storage.getString(defaults.stateKey(), defaults.stateValue()),
                storage.getLong(defaults.verifiedAtKey(), defaults.verifiedAtValue()));
    }
}
