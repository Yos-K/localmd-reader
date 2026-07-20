package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.BillingPurchaseSnapshot;

public final class ProPurchaseStatusRefresh {
    private final ProPurchaseCacheRefresh cacheRefresh;
    private final ProPurchaseStatusProvider statusProvider;

    public ProPurchaseStatusRefresh(
            ProPurchaseCacheRefresh cacheRefresh,
            ProPurchaseStatusProvider statusProvider) {
        if (cacheRefresh == null) {
            throw new IllegalArgumentException("Purchase cache refresh must not be null.");
        }
        if (statusProvider == null) {
            throw new IllegalArgumentException("Purchase status provider must not be null.");
        }
        this.cacheRefresh = cacheRefresh;
        this.statusProvider = statusProvider;
    }

    public void refreshAt(long verifiedAtMillis) {
        refreshAt(verifiedAtMillis, null);
    }

    public void refreshAt(long verifiedAtMillis, ProPurchaseStatusCallback afterSaveCallback) {
        if (verifiedAtMillis < 0L) {
            throw new IllegalArgumentException("Verification time must not be negative.");
        }
        statusProvider.queryCurrentSnapshot(new SnapshotSavingCallback(cacheRefresh, verifiedAtMillis, afterSaveCallback));
    }

    private static final class SnapshotSavingCallback implements ProPurchaseStatusCallback {
        private final ProPurchaseCacheRefresh cacheRefresh;
        private final long verifiedAtMillis;
        private final ProPurchaseStatusCallback afterSaveCallback;

        private SnapshotSavingCallback(
                ProPurchaseCacheRefresh cacheRefresh,
                long verifiedAtMillis,
                ProPurchaseStatusCallback afterSaveCallback) {
            this.cacheRefresh = cacheRefresh;
            this.verifiedAtMillis = verifiedAtMillis;
            this.afterSaveCallback = afterSaveCallback;
        }

        @Override
        public void onCurrentSnapshot(BillingPurchaseSnapshot snapshot) {
            cacheRefresh.saveSnapshot(snapshot, verifiedAtMillis);
            if (afterSaveCallback != null) {
                afterSaveCallback.onCurrentSnapshot(snapshot);
            }
        }
    }
}
