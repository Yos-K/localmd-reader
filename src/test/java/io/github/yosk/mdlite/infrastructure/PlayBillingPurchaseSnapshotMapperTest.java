package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.BillingPurchaseSnapshot;
import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class PlayBillingPurchaseSnapshotMapperTest {
    private static final int PLAY_BILLING_PURCHASED = 1;
    private static final int PLAY_BILLING_PENDING = 2;
    private static final int PLAY_BILLING_UNSPECIFIED = 0;


    @Test
    void acknowledgedPurchasedPlayBillingStateMapsToProSnapshot() {
        BillingPurchaseSnapshot snapshot = PlayBillingPurchaseSnapshotMapper.fromPurchaseState(
                PLAY_BILLING_PURCHASED,
                true);

        TestAssertions.assertTrue(
                snapshot.proPurchaseState().entitlement().isPro(),
                "Acknowledged Play Billing PURCHASED state must grant Pro entitlement");
    }

    @Test
    void unacknowledgedPurchasedPlayBillingStateMapsToNonProSnapshot() {
        BillingPurchaseSnapshot snapshot = PlayBillingPurchaseSnapshotMapper.fromPurchaseState(
                PLAY_BILLING_PURCHASED,
                false);

        TestAssertions.assertFalse(
                snapshot.proPurchaseState().entitlement().isPro(),
                "Unacknowledged Play Billing PURCHASED state must not grant Pro entitlement");
    }

    @Test
    void pendingPlayBillingStateMapsToNonProSnapshot() {
        BillingPurchaseSnapshot snapshot = PlayBillingPurchaseSnapshotMapper.fromPurchaseState(
                PLAY_BILLING_PENDING,
                false);

        TestAssertions.assertFalse(
                snapshot.proPurchaseState().entitlement().isPro(),
                "Play Billing PENDING state must keep Free entitlement");
    }

    @Test
    void unspecifiedPlayBillingStateMapsToNotPurchasedSnapshot() {
        BillingPurchaseSnapshot snapshot = PlayBillingPurchaseSnapshotMapper.fromPurchaseState(
                PLAY_BILLING_UNSPECIFIED,
                false);

        TestAssertions.assertEquals(
                "not_purchased",
                snapshot.proPurchaseState().persistenceCode(),
                "Unspecified Play Billing purchase state must safely map to not purchased");
    }

    @Test
    void missingPlayBillingProductMapsToNotPurchasedSnapshot() {
        BillingPurchaseSnapshot snapshot = PlayBillingPurchaseSnapshotMapper.notPurchased();

        TestAssertions.assertEquals(
                "not_purchased",
                snapshot.proPurchaseState().persistenceCode(),
                "Missing Play Billing product must map to not purchased");
    }

    @Test
    void unavailablePlayBillingClientMapsToBillingUnavailableSnapshot() {
        BillingPurchaseSnapshot snapshot = PlayBillingPurchaseSnapshotMapper.billingUnavailable();

        TestAssertions.assertEquals(
                "billing_unavailable",
                snapshot.proPurchaseState().persistenceCode(),
                "Unavailable Play Billing client must map to billing unavailable");
    }
}
