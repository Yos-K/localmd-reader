package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public final class ProPurchaseCacheEntryTest {

    @Nested
    final class EntitlementRestoration {
        @Test
        void purchasedEntryRestoresProEntitlement() {
            ProPurchaseCacheEntry entry = ProPurchaseCacheEntry.verifiedAt(
                    ProPurchaseState.purchased(), 1000L);

            TestAssertions.assertTrue(entry.purchaseState().entitlement().isPro(),
                    "cached purchased state must restore Pro entitlement");
        }

        @Test
        void pendingEntryRestoresFreeEntitlement() {
            ProPurchaseCacheEntry entry = ProPurchaseCacheEntry.verifiedAt(
                    ProPurchaseState.pending(), 1000L);

            TestAssertions.assertFalse(entry.purchaseState().entitlement().isPro(),
                    "cached pending state must not restore Pro entitlement");
        }

        @Test
        void billingUnavailableEntryRestoresFreeEntitlement() {
            ProPurchaseCacheEntry entry = ProPurchaseCacheEntry.verifiedAt(
                    ProPurchaseState.billingUnavailable(), 1000L);

            TestAssertions.assertFalse(entry.purchaseState().entitlement().isPro(),
                    "cached billing-unavailable state must fail safe as Free");
        }
    }

    @Nested
    final class SafePersistence {
        @Test
        void entryKeepsTheVerificationTimestamp() {
            ProPurchaseCacheEntry entry = ProPurchaseCacheEntry.verifiedAt(
                    ProPurchaseState.notPurchased(), 123456789L);

            TestAssertions.assertEquals(123456789L, entry.verifiedAtMillis(),
                    "cached purchase state must keep the verification timestamp");
        }

        @Test
        void entryExposesOnlyTheNormalizedPurchaseStateCode() {
            ProPurchaseCacheEntry entry = ProPurchaseCacheEntry.verifiedAt(
                    ProPurchaseState.purchased(), 123456789L);

            TestAssertions.assertEquals("purchased", entry.purchaseStateCode(),
                    "cache persistence must store the normalized purchase state code only");
        }

        @Test
        void safeValuesRestoreProEntitlement() {
            ProPurchaseCacheEntry entry = ProPurchaseCacheEntry.restore(
                    "purchased", 123456789L);

            TestAssertions.assertTrue(entry.purchaseState().entitlement().isPro(),
                    "safe persisted purchase state must restore Pro entitlement");
        }

        @Test
        void safeValuesRestoreTheVerificationTimestamp() {
            ProPurchaseCacheEntry entry = ProPurchaseCacheEntry.restore(
                    "purchased", 123456789L);

            TestAssertions.assertEquals(123456789L, entry.verifiedAtMillis(),
                    "restored cache entry must keep the persisted timestamp");
        }

        @Test
        void invalidStateCodeIsNormalizedToUnknown() {
            ProPurchaseCacheEntry entry = ProPurchaseCacheEntry.restore(
                    "raw-token-or-receipt", 123456789L);

            TestAssertions.assertEquals("unknown", entry.purchaseStateCode(),
                    "invalid persisted state must be normalized to unknown");
        }

        @Test
        void invalidStateCodeFailsSafeAsFree() {
            ProPurchaseCacheEntry entry = ProPurchaseCacheEntry.restore(
                    "raw-token-or-receipt", 123456789L);

            TestAssertions.assertFalse(entry.purchaseState().entitlement().isPro(),
                    "invalid persisted state must fail safe as Free");
        }
    }

    @Nested
    final class CreationValidity {
        @Test
        void missingPurchaseStateIsRejectedBeforePersistence() {
            TestAssertions.assertThrows(
                    IllegalArgumentException.class,
                    () -> ProPurchaseCacheEntry.verifiedAt(null, 1000L));
        }

        @Test
        void negativeVerificationTimeIsRejectedBeforePersistence() {
            TestAssertions.assertThrows(
                    IllegalArgumentException.class,
                    () -> ProPurchaseCacheEntry.verifiedAt(ProPurchaseState.unknown(), -1L));
        }
    }

    @Nested
    final class AgeIndependence {
        @Test
        void purchasedEntryRestoresProRegardlessOfAge() {
            ProPurchaseCacheEntry ancient = ProPurchaseCacheEntry.restore("purchased", 0L);

            TestAssertions.assertTrue(ancient.purchaseState().entitlement().isPro(),
                    "cache entry has no TTL, so an old purchased state still restores Pro");
        }

        @Test
        void epochVerificationTimestampIsKeptVerbatim() {
            ProPurchaseCacheEntry ancient = ProPurchaseCacheEntry.restore("purchased", 0L);

            TestAssertions.assertEquals(0L, ancient.verifiedAtMillis(),
                    "verification timestamp must remain unchanged even at the epoch");
        }
    }
}
