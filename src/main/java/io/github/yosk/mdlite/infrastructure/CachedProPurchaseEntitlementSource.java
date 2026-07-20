package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.EntitlementSource;
import io.github.yosk.mdlite.domain.FeatureEntitlement;

public final class CachedProPurchaseEntitlementSource implements EntitlementSource {
    private final ProPurchaseCacheStore store;

    public CachedProPurchaseEntitlementSource(ProPurchaseCacheStore store) {
        if (store == null) {
            throw new IllegalArgumentException("Purchase cache store must not be null.");
        }
        this.store = store;
    }

    @Override
    public FeatureEntitlement currentEntitlement() {
        return store.load().purchaseState().entitlement();
    }
}
