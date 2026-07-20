package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.ProPurchaseCacheEntry;
import io.github.yosk.mdlite.domain.ProPurchaseState;
import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class ProPurchaseCachePreferencesTest {

    @Test
    void cacheEntryConvertsToSharedPreferenceValues() {
        ProPurchaseCachePreferences preferences =
                ProPurchaseCachePreferences.fromEntry(ProPurchaseCacheEntry.verifiedAt(ProPurchaseState.purchased(), 1000L));

        TestAssertions.assertEquals("pro_purchase_state", preferences.stateKey(), "Purchase cache state key must be stable");
        TestAssertions.assertEquals("pro_purchase_verified_at_millis", preferences.verifiedAtKey(), "Purchase cache timestamp key must be stable");
        TestAssertions.assertEquals("purchased", preferences.stateValue(), "Purchase cache state value must be normalized");
        TestAssertions.assertEquals(1000L, preferences.verifiedAtValue(), "Purchase cache timestamp value must be preserved");
    }

    @Test
    void sharedPreferenceValuesConvertToCacheEntry() {
        ProPurchaseCacheEntry entry = ProPurchaseCachePreferences.restoreEntry("purchased", 1000L);

        TestAssertions.assertTrue(entry.purchaseState().entitlement().isPro(), "Stored purchased value must restore Pro entitlement");
        TestAssertions.assertEquals(1000L, entry.verifiedAtMillis(), "Stored timestamp must restore verification time");
    }

    @Test
    void invalidSharedPreferenceStateRestoresUnknownFreeState() {
        ProPurchaseCacheEntry entry = ProPurchaseCachePreferences.restoreEntry("raw-token-or-receipt", 1000L);

        TestAssertions.assertEquals("unknown", entry.purchaseStateCode(), "Unexpected stored value must become unknown");
        TestAssertions.assertFalse(entry.purchaseState().entitlement().isPro(), "Unexpected stored value must fail safe as Free");
    }

    @Test
    void negativeSharedPreferenceTimestampIsRejected() {
        TestAssertions.assertThrows(
                IllegalArgumentException.class,
                () -> ProPurchaseCachePreferences.restoreEntry("purchased", -1L));
    }
}
