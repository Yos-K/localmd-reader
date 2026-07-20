package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.ProPurchaseCacheEntry;

public final class ProPurchaseCachePreferences {
    private static final String STATE_KEY = "pro_purchase_state";
    private static final String VERIFIED_AT_KEY = "pro_purchase_verified_at_millis";

    private final String stateValue;
    private final long verifiedAtValue;

    private ProPurchaseCachePreferences(String stateValue, long verifiedAtValue) {
        this.stateValue = stateValue;
        this.verifiedAtValue = verifiedAtValue;
    }

    public static ProPurchaseCachePreferences fromEntry(ProPurchaseCacheEntry entry) {
        if (entry == null) {
            throw new IllegalArgumentException("Purchase cache entry must not be null.");
        }
        return new ProPurchaseCachePreferences(entry.purchaseStateCode(), entry.verifiedAtMillis());
    }

    public static ProPurchaseCacheEntry restoreEntry(String stateValue, long verifiedAtValue) {
        return ProPurchaseCacheEntry.restore(stateValue, verifiedAtValue);
    }

    public String stateKey() {
        return STATE_KEY;
    }

    public String verifiedAtKey() {
        return VERIFIED_AT_KEY;
    }

    public String stateValue() {
        return stateValue;
    }

    public long verifiedAtValue() {
        return verifiedAtValue;
    }
}
