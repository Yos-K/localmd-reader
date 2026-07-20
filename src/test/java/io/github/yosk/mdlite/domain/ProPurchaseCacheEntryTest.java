package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class ProPurchaseCacheEntryTest {

    @Test
    void purchasedCacheEntryRestoresProEntitlement() {
        ProPurchaseCacheEntry entry = ProPurchaseCacheEntry.verifiedAt(ProPurchaseState.purchased(), 1000L);

        TestAssertions.assertTrue(entry.purchaseState().entitlement().isPro(), "Cached purchased state must restore Pro entitlement");
    }

    @Test
    void pendingCacheEntryRestoresFreeEntitlement() {
        ProPurchaseCacheEntry entry = ProPurchaseCacheEntry.verifiedAt(ProPurchaseState.pending(), 1000L);

        TestAssertions.assertFalse(entry.purchaseState().entitlement().isPro(), "Cached pending state must not restore Pro entitlement");
    }

    @Test
    void unavailableCacheEntryRestoresFreeEntitlement() {
        ProPurchaseCacheEntry entry = ProPurchaseCacheEntry.verifiedAt(ProPurchaseState.billingUnavailable(), 1000L);

        TestAssertions.assertFalse(entry.purchaseState().entitlement().isPro(), "Cached billing-unavailable state must fail safe as Free");
    }

    @Test
    void cacheEntryKeepsTheVerificationTimestamp() {
        ProPurchaseCacheEntry entry = ProPurchaseCacheEntry.verifiedAt(ProPurchaseState.notPurchased(), 123456789L);

        TestAssertions.assertEquals(123456789L, entry.verifiedAtMillis(), "Cached purchase state must keep the verification timestamp");
    }

    @Test
    void cacheEntryExposesOnlySafePersistenceValues() {
        ProPurchaseCacheEntry entry = ProPurchaseCacheEntry.verifiedAt(ProPurchaseState.purchased(), 123456789L);

        TestAssertions.assertEquals("purchased", entry.purchaseStateCode(), "Cache persistence must store the normalized purchase state code only");
        TestAssertions.assertEquals(123456789L, entry.verifiedAtMillis(), "Cache persistence must store the verification timestamp");
    }

    @Test
    void cacheEntryRestoresFromSafePersistenceValues() {
        ProPurchaseCacheEntry entry = ProPurchaseCacheEntry.restore("purchased", 123456789L);

        TestAssertions.assertTrue(entry.purchaseState().entitlement().isPro(), "Safe persisted purchase state must restore Pro entitlement");
        TestAssertions.assertEquals(123456789L, entry.verifiedAtMillis(), "Restored cache entry must keep the persisted timestamp");
    }

    @Test
    void cacheEntryRestoresInvalidPersistenceStateAsUnknown() {
        ProPurchaseCacheEntry entry = ProPurchaseCacheEntry.restore("raw-token-or-receipt", 123456789L);

        TestAssertions.assertEquals("unknown", entry.purchaseStateCode(), "Invalid persisted state must be normalized to unknown");
        TestAssertions.assertFalse(entry.purchaseState().entitlement().isPro(), "Invalid persisted state must fail safe as Free");
    }

    @Test
    void missingPurchaseStateIsRejectedBeforeCachePersistence() {
        TestAssertions.assertThrows(
                IllegalArgumentException.class,
                () -> ProPurchaseCacheEntry.verifiedAt(null, 1000L));
    }

    @Test
    void negativeVerificationTimeIsRejectedBeforeCachePersistence() {
        TestAssertions.assertThrows(
                IllegalArgumentException.class,
                () -> ProPurchaseCacheEntry.verifiedAt(ProPurchaseState.unknown(), -1L));
    }

    // The entry records WHEN it was verified but enforces no expiry itself: a purchased
    // state restores Pro regardless of how old the timestamp is (here the Unix epoch).
    // Freshness / re-verification, if any, is a caller policy — not the entry's job.
    // This pins the age-independence so adding a TTL later is a deliberate, visible change.
    // (exploration 2026-06-13 P12)
    @Test
    void purchasedCacheEntryRestoresProRegardlessOfAge() {
        ProPurchaseCacheEntry ancient = ProPurchaseCacheEntry.restore("purchased", 0L);

        TestAssertions.assertTrue(ancient.purchaseState().entitlement().isPro(),
                "Cache entry enforces no TTL: an old purchased state still restores Pro");
        TestAssertions.assertEquals(0L, ancient.verifiedAtMillis(),
                "The verification timestamp is kept verbatim, even at the epoch");
    }
}
