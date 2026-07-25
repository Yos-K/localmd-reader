package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public final class ProPurchaseStateTest {

    @Nested
    final class Entitlement {
        @Test
        void purchasedStateGrantsProEntitlementAndConvenienceFeatures() {
            FeatureEntitlement entitlement = ProPurchaseState.purchased().entitlement();

            TestAssertions.assertTrue(entitlement.isPro(),
                    "purchased state must grant Pro entitlement");
            TestAssertions.assertTrue(entitlement.allows(ViewerFeature.EXTRA_THEMES),
                    "purchased state must unlock convenience features");
        }

        @Test
        void notPurchasedStateKeepsFreeEntitlementAndFeaturesLocked() {
            FeatureEntitlement entitlement = ProPurchaseState.notPurchased().entitlement();

            TestAssertions.assertFalse(entitlement.isPro(),
                    "not-purchased state must keep Free entitlement");
            TestAssertions.assertFalse(entitlement.allows(ViewerFeature.EXTRA_THEMES),
                    "not-purchased state must keep convenience features locked");
        }

        @Test
        void pendingStateKeepsFreeEntitlementUntilPurchaseCompletes() {
            FeatureEntitlement entitlement = ProPurchaseState.pending().entitlement();

            TestAssertions.assertFalse(entitlement.isPro(),
                    "pending state must keep Free entitlement");
            TestAssertions.assertFalse(entitlement.allows(ViewerFeature.EXTRA_THEMES),
                    "pending state must keep convenience features locked");
        }

        @Test
        void unknownStateFailsSafeAsFree() {
            FeatureEntitlement entitlement = ProPurchaseState.unknown().entitlement();

            TestAssertions.assertFalse(entitlement.isPro(),
                    "unknown state must keep Free entitlement");
            TestAssertions.assertFalse(entitlement.allows(ViewerFeature.EXTRA_THEMES),
                    "unknown state must keep convenience features locked");
        }

        @Test
        void billingUnavailableStateFailsSafeAsFree() {
            FeatureEntitlement entitlement = ProPurchaseState.billingUnavailable().entitlement();

            TestAssertions.assertFalse(entitlement.isPro(),
                    "billing-unavailable state must keep Free entitlement");
            TestAssertions.assertFalse(entitlement.allows(ViewerFeature.EXTRA_THEMES),
                    "billing-unavailable state must keep convenience features locked");
        }
    }

    @Nested
    final class PersistenceRoundTrip {
        @Test
        void purchasedCodeRestoresProEntitlement() {
            ProPurchaseState restored = roundTrip(ProPurchaseState.purchased());

            TestAssertions.assertTrue(restored.entitlement().isPro(),
                    "purchased persistence code must restore Pro entitlement");
        }

        @Test
        void notPurchasedCodeRestoresFreeEntitlement() {
            ProPurchaseState restored = roundTrip(ProPurchaseState.notPurchased());

            TestAssertions.assertFalse(restored.entitlement().isPro(),
                    "not-purchased persistence code must restore Free entitlement");
        }

        @Test
        void pendingCodeRestoresFreeEntitlement() {
            ProPurchaseState restored = roundTrip(ProPurchaseState.pending());

            TestAssertions.assertFalse(restored.entitlement().isPro(),
                    "pending persistence code must restore Free entitlement");
        }

        @Test
        void unknownCodeRestoresFreeEntitlement() {
            ProPurchaseState restored = roundTrip(ProPurchaseState.unknown());

            TestAssertions.assertFalse(restored.entitlement().isPro(),
                    "unknown persistence code must restore Free entitlement");
        }

        @Test
        void billingUnavailableCodeRestoresFreeEntitlement() {
            ProPurchaseState restored = roundTrip(ProPurchaseState.billingUnavailable());

            TestAssertions.assertFalse(restored.entitlement().isPro(),
                    "billing-unavailable persistence code must restore Free entitlement");
        }
    }

    @Nested
    final class InvalidPersistence {
        @Test
        void unrecognizedCodeBecomesUnknownAndFailsSafeAsFree() {
            ProPurchaseState state = ProPurchaseState.fromPersistenceCode("purchase-token-like-value");

            TestAssertions.assertEquals(ProPurchaseState.unknown().persistenceCode(), state.persistenceCode(),
                    "unrecognized persistence code must become unknown");
            TestAssertions.assertFalse(state.entitlement().isPro(),
                    "unrecognized persistence code must fail safe as Free");
        }

        @Test
        void emptyCodeBecomesUnknownAndFailsSafeAsFree() {
            ProPurchaseState state = ProPurchaseState.fromPersistenceCode("");

            TestAssertions.assertEquals(ProPurchaseState.unknown().persistenceCode(), state.persistenceCode(),
                    "empty persistence code must become unknown");
            TestAssertions.assertFalse(state.entitlement().isPro(),
                    "empty persistence code must fail safe as Free");
        }
    }

    private static ProPurchaseState roundTrip(ProPurchaseState state) {
        return ProPurchaseState.fromPersistenceCode(state.persistenceCode());
    }

}
