package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.BillingPurchaseSnapshot;
import io.github.yosk.mdlite.domain.ProPurchaseCacheEntry;

public final class ProPurchaseCacheRefresh {
    private final ProPurchaseCacheStore store;

    public ProPurchaseCacheRefresh(ProPurchaseCacheStore store) {
        if (store == null) {
            throw new IllegalArgumentException("Purchase cache store must not be null.");
        }
        this.store = store;
    }

    public void saveSnapshot(BillingPurchaseSnapshot snapshot, long verifiedAtMillis) {
        if (snapshot == null) {
            throw new IllegalArgumentException("Purchase snapshot must not be null.");
        }
        store.save(ProPurchaseCacheEntry.verifiedAt(snapshot.proPurchaseState(), verifiedAtMillis));
    }
}
