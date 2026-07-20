package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.FeatureEntitlement;
import io.github.yosk.mdlite.domain.ProPurchaseCacheEntry;
import io.github.yosk.mdlite.domain.ProPurchaseState;
import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class CachedProPurchaseEntitlementSourceTest {

    @Test
    void cachedPurchasedStateGrantsProEntitlement() {
        FakePurchaseCacheStorage storage = new FakePurchaseCacheStorage();
        ProPurchaseCacheStore store = new ProPurchaseCacheStore(storage);
        CachedProPurchaseEntitlementSource source = new CachedProPurchaseEntitlementSource(store);

        store.save(ProPurchaseCacheEntry.verifiedAt(ProPurchaseState.purchased(), 1000L));
        FeatureEntitlement entitlement = source.currentEntitlement();

        TestAssertions.assertTrue(entitlement.isPro(), "Cached purchased Pro state must grant Pro entitlement");
    }

    @Test
    void missingCachedStateKeepsFreeEntitlement() {
        ProPurchaseCacheStore store = new ProPurchaseCacheStore(new FakePurchaseCacheStorage());
        CachedProPurchaseEntitlementSource source = new CachedProPurchaseEntitlementSource(store);

        FeatureEntitlement entitlement = source.currentEntitlement();

        TestAssertions.assertFalse(entitlement.isPro(), "Missing cached Pro state must keep Free entitlement");
    }

    @Test
    void invalidCachedStateKeepsFreeEntitlement() {
        FakePurchaseCacheStorage storage = new FakePurchaseCacheStorage();
        ProPurchaseCacheStore store = new ProPurchaseCacheStore(storage);
        CachedProPurchaseEntitlementSource source = new CachedProPurchaseEntitlementSource(store);

        storage.putString("pro_purchase_state", "raw-token-or-receipt");
        storage.putLong("pro_purchase_verified_at_millis", 1000L);
        FeatureEntitlement entitlement = source.currentEntitlement();

        TestAssertions.assertFalse(entitlement.isPro(), "Invalid cached Pro state must fail safe as Free");
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
