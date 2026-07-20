package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.BillingPurchaseSnapshot;

public final class UnavailableProPurchaseStatusProvider implements ProPurchaseStatusProvider {
    private static final UnavailableProPurchaseStatusProvider INSTANCE = new UnavailableProPurchaseStatusProvider();

    private UnavailableProPurchaseStatusProvider() {
    }

    public static UnavailableProPurchaseStatusProvider instance() {
        return INSTANCE;
    }

    @Override
    public void queryCurrentSnapshot(ProPurchaseStatusCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Purchase status callback must not be null.");
        }
        callback.onCurrentSnapshot(BillingPurchaseSnapshot.billingUnavailable());
    }
}
