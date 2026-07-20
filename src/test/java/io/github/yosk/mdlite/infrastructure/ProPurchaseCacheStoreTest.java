package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.ProPurchaseCacheEntry;
import io.github.yosk.mdlite.domain.ProPurchaseState;
import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class ProPurchaseCacheStoreTest {

    @Test
    void savedPurchaseCacheEntryCanBeLoaded() {
        FakePurchaseCacheStorage storage = new FakePurchaseCacheStorage();
        ProPurchaseCacheStore store = new ProPurchaseCacheStore(storage);

        store.save(ProPurchaseCacheEntry.verifiedAt(ProPurchaseState.purchased(), 1000L));
        ProPurchaseCacheEntry loaded = store.load();

        TestAssertions.assertTrue(loaded.purchaseState().entitlement().isPro(), "Saved purchased cache entry must load Pro entitlement");
        TestAssertions.assertEquals(1000L, loaded.verifiedAtMillis(), "Saved purchase cache timestamp must be loaded");
    }

    @Test
    void missingPurchaseCacheEntryLoadsUnknownFreeState() {
        ProPurchaseCacheStore store = new ProPurchaseCacheStore(new FakePurchaseCacheStorage());

        ProPurchaseCacheEntry loaded = store.load();

        TestAssertions.assertEquals("unknown", loaded.purchaseStateCode(), "Missing purchase cache must load unknown state");
        TestAssertions.assertFalse(loaded.purchaseState().entitlement().isPro(), "Missing purchase cache must fail safe as Free");
    }

    @Test
    void invalidStoredPurchaseStateLoadsUnknownFreeState() {
        FakePurchaseCacheStorage storage = new FakePurchaseCacheStorage();
        ProPurchaseCacheStore store = new ProPurchaseCacheStore(storage);

        storage.putString("pro_purchase_state", "raw-token-or-receipt");
        storage.putLong("pro_purchase_verified_at_millis", 1000L);
        ProPurchaseCacheEntry loaded = store.load();

        TestAssertions.assertEquals("unknown", loaded.purchaseStateCode(), "Invalid stored purchase state must load unknown state");
        TestAssertions.assertFalse(loaded.purchaseState().entitlement().isPro(), "Invalid stored purchase state must fail safe as Free");
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
