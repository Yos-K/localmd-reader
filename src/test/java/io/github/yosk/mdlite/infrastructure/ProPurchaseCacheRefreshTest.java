package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.BillingPurchaseSnapshot;
import io.github.yosk.mdlite.domain.ProPurchaseCacheEntry;
import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class ProPurchaseCacheRefreshTest {

    @Test
    void acknowledgedPurchasedSnapshotRefreshesCacheAsPurchasedAtVerificationTime() {
        ProPurchaseCacheStore store = new ProPurchaseCacheStore(new FakePurchaseCacheStorage());
        ProPurchaseCacheRefresh refresh = new ProPurchaseCacheRefresh(store);

        refresh.saveSnapshot(BillingPurchaseSnapshot.purchased(true), 2000L);
        ProPurchaseCacheEntry entry = store.load();

        TestAssertions.assertEquals("purchased", entry.purchaseStateCode(), "Acknowledged purchased snapshot must be cached as purchased");
        TestAssertions.assertTrue(entry.purchaseState().entitlement().isPro(), "Cached acknowledged purchased snapshot must grant Pro entitlement");
        TestAssertions.assertEquals(2000L, entry.verifiedAtMillis(), "Cached acknowledged purchased snapshot must preserve verification time");
    }

    @Test
    void pendingSnapshotRefreshesCacheAsPendingAndKeepsFreeEntitlement() {
        ProPurchaseCacheStore store = new ProPurchaseCacheStore(new FakePurchaseCacheStorage());
        ProPurchaseCacheRefresh refresh = new ProPurchaseCacheRefresh(store);

        refresh.saveSnapshot(BillingPurchaseSnapshot.pending(), 3000L);
        ProPurchaseCacheEntry entry = store.load();

        TestAssertions.assertEquals("pending", entry.purchaseStateCode(), "Pending snapshot must be cached as pending");
        TestAssertions.assertFalse(entry.purchaseState().entitlement().isPro(), "Cached pending snapshot must keep Free entitlement");
    }

    @Test
    void notPurchasedSnapshotRefreshesCacheAsNotPurchasedAndClearsProEntitlement() {
        FakePurchaseCacheStorage storage = new FakePurchaseCacheStorage();
        ProPurchaseCacheStore store = new ProPurchaseCacheStore(storage);
        ProPurchaseCacheRefresh refresh = new ProPurchaseCacheRefresh(store);

        refresh.saveSnapshot(BillingPurchaseSnapshot.purchased(true), 1000L);
        refresh.saveSnapshot(BillingPurchaseSnapshot.notPurchased(), 4000L);
        ProPurchaseCacheEntry entry = store.load();

        TestAssertions.assertEquals("not_purchased", entry.purchaseStateCode(), "Not purchased snapshot must overwrite stale purchased cache");
        TestAssertions.assertFalse(entry.purchaseState().entitlement().isPro(), "Cached not purchased snapshot must clear Pro entitlement");
        TestAssertions.assertEquals(4000L, entry.verifiedAtMillis(), "Cached not purchased snapshot must preserve latest verification time");
    }

    @Test
    void billingUnavailableSnapshotRefreshesCacheAsUnavailableAndKeepsFreeEntitlement() {
        ProPurchaseCacheStore store = new ProPurchaseCacheStore(new FakePurchaseCacheStorage());
        ProPurchaseCacheRefresh refresh = new ProPurchaseCacheRefresh(store);

        refresh.saveSnapshot(BillingPurchaseSnapshot.billingUnavailable(), 5000L);
        ProPurchaseCacheEntry entry = store.load();

        TestAssertions.assertEquals("billing_unavailable", entry.purchaseStateCode(), "Billing unavailable snapshot must be cached as unavailable");
        TestAssertions.assertFalse(entry.purchaseState().entitlement().isPro(), "Cached billing unavailable snapshot must keep Free entitlement");
    }

    private static final class FakePurchaseCacheStorage implements ProPurchaseCacheStorage {
        private String stateValue;
        private long verifiedAtValue;

        @Override
        public String getString(String key, String defaultValue) {
            return stateValue == null ? defaultValue : stateValue;
        }

        @Override
        public long getLong(String key, long defaultValue) {
            return verifiedAtValue;
        }

        @Override
        public void putString(String key, String value) {
            stateValue = value;
        }

        @Override
        public void putLong(String key, long value) {
            verifiedAtValue = value;
        }
    }
}
