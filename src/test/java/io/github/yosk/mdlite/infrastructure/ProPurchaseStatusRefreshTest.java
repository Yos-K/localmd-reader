package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.BillingPurchaseSnapshot;
import io.github.yosk.mdlite.domain.ProPurchaseCacheEntry;
import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class ProPurchaseStatusRefreshTest {

    @Test
    void purchasedStatusProviderRefreshesCacheAsPurchased() {
        ProPurchaseCacheStore store = new ProPurchaseCacheStore(new FakePurchaseCacheStorage());
        ProPurchaseStatusRefresh refresh = new ProPurchaseStatusRefresh(
                new ProPurchaseCacheRefresh(store),
                new FixedProPurchaseStatusProvider(BillingPurchaseSnapshot.purchased(true)));

        refresh.refreshAt(6000L);
        ProPurchaseCacheEntry entry = store.load();

        TestAssertions.assertEquals("purchased", entry.purchaseStateCode(), "Purchased status provider must refresh cache as purchased");
        TestAssertions.assertTrue(entry.purchaseState().entitlement().isPro(), "Purchased status provider must grant cached Pro entitlement");
        TestAssertions.assertEquals(6000L, entry.verifiedAtMillis(), "Purchase status refresh must use supplied verification time");
    }

    @Test
    void unavailableStatusProviderRefreshesCacheAsBillingUnavailable() {
        ProPurchaseCacheStore store = new ProPurchaseCacheStore(new FakePurchaseCacheStorage());
        ProPurchaseStatusRefresh refresh = new ProPurchaseStatusRefresh(
                new ProPurchaseCacheRefresh(store),
                UnavailableProPurchaseStatusProvider.instance());

        refresh.refreshAt(7000L);
        ProPurchaseCacheEntry entry = store.load();

        TestAssertions.assertEquals("billing_unavailable", entry.purchaseStateCode(), "Unavailable provider must refresh cache as billing unavailable");
        TestAssertions.assertFalse(entry.purchaseState().entitlement().isPro(), "Unavailable provider must keep cached Free entitlement");
    }

    @Test
    void refreshWithCallbackSavesSnapshotBeforeNotifyingCaller() {
        ProPurchaseCacheStore store = new ProPurchaseCacheStore(new FakePurchaseCacheStorage());
        RecordingProPurchaseStatusCallback callback = new RecordingProPurchaseStatusCallback();
        ProPurchaseStatusRefresh refresh = new ProPurchaseStatusRefresh(
                new ProPurchaseCacheRefresh(store),
                new FixedProPurchaseStatusProvider(BillingPurchaseSnapshot.purchased(true)));

        refresh.refreshAt(8000L, callback);
        ProPurchaseCacheEntry entry = store.load();

        TestAssertions.assertEquals("purchased", entry.purchaseStateCode(), "Callback refresh must save the purchased snapshot before UI reloads entitlement");
        TestAssertions.assertTrue(callback.snapshot().proPurchaseState().entitlement().isPro(), "Callback refresh must notify caller with the saved Pro snapshot");
    }

    @Test
    void negativeVerificationTimeIsRejectedBeforeQueryingPurchaseStatusProvider() {
        RecordingProPurchaseStatusProvider provider = new RecordingProPurchaseStatusProvider();
        ProPurchaseStatusRefresh refresh = new ProPurchaseStatusRefresh(
                new ProPurchaseCacheRefresh(new ProPurchaseCacheStore(new FakePurchaseCacheStorage())),
                provider);

        TestAssertions.assertThrows(
                IllegalArgumentException.class,
                new TestAssertions.ThrowingRunnable() {
                    @Override
                    public void run() {
                        refresh.refreshAt(-1L);
                    }
                });
        TestAssertions.assertFalse(provider.wasQueried(), "Negative verification time must be rejected before querying Billing status");
    }

    private static final class RecordingProPurchaseStatusProvider implements ProPurchaseStatusProvider {
        private boolean queried;

        @Override
        public void queryCurrentSnapshot(ProPurchaseStatusCallback callback) {
            queried = true;
            callback.onCurrentSnapshot(BillingPurchaseSnapshot.purchased(true));
        }

        private boolean wasQueried() {
            return queried;
        }
    }

    private static final class FixedProPurchaseStatusProvider implements ProPurchaseStatusProvider {
        private final BillingPurchaseSnapshot snapshot;

        private FixedProPurchaseStatusProvider(BillingPurchaseSnapshot snapshot) {
            this.snapshot = snapshot;
        }

        @Override
        public void queryCurrentSnapshot(ProPurchaseStatusCallback callback) {
            callback.onCurrentSnapshot(snapshot);
        }
    }

    private static final class RecordingProPurchaseStatusCallback implements ProPurchaseStatusCallback {
        private BillingPurchaseSnapshot snapshot;

        @Override
        public void onCurrentSnapshot(BillingPurchaseSnapshot snapshot) {
            this.snapshot = snapshot;
        }

        private BillingPurchaseSnapshot snapshot() {
            return snapshot;
        }
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
