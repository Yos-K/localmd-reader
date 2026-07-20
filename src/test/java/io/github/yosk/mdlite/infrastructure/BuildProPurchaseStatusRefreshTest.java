package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.ProPurchaseCacheEntry;
import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class BuildProPurchaseStatusRefreshTest {

    @Test
    void currentBuildStatusRefreshStoresUnavailableStateUntilBillingIsConnected() {
        ProPurchaseCacheStore store = new ProPurchaseCacheStore(new FakePurchaseCacheStorage());

        BuildProPurchaseStatusRefresh.current(store).refreshAt(8000L);
        ProPurchaseCacheEntry entry = store.load();

        TestAssertions.assertEquals(
                "billing_unavailable",
                entry.purchaseStateCode(),
                "Current build status refresh must store billing unavailable until Billing adapter is connected");
        TestAssertions.assertFalse(
                entry.purchaseState().entitlement().isPro(),
                "Current build status refresh must keep cached Free entitlement");
        TestAssertions.assertEquals(
                8000L,
                entry.verifiedAtMillis(),
                "Current build status refresh must preserve verification time");
    }

    @Test
    void currentBuildStatusRefreshCanUseAConnectedPurchaseStatusProvider() {
        ProPurchaseCacheStore store = new ProPurchaseCacheStore(new FakePurchaseCacheStorage());

        BuildProPurchaseStatusRefresh.current(store, new PurchasedProPurchaseStatusProvider()).refreshAt(9000L);
        ProPurchaseCacheEntry entry = store.load();

        TestAssertions.assertEquals(
                "purchased",
                entry.purchaseStateCode(),
                "Current build status refresh must store the snapshot returned by the connected provider");
        TestAssertions.assertTrue(
                entry.purchaseState().entitlement().isPro(),
                "Purchased provider snapshot must grant cached Pro entitlement");
        TestAssertions.assertEquals(
                9000L,
                entry.verifiedAtMillis(),
                "Connected provider refresh must preserve verification time");
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

    private static final class PurchasedProPurchaseStatusProvider implements ProPurchaseStatusProvider {
        @Override
        public void queryCurrentSnapshot(ProPurchaseStatusCallback callback) {
            callback.onCurrentSnapshot(io.github.yosk.mdlite.domain.BillingPurchaseSnapshot.purchased(true));
        }
    }
}
