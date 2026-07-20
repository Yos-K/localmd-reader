package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class PlayBillingPurchaseCompletionPolicyTest {
    private static final int BILLING_OK = 0;
    private static final int BILLING_USER_CANCELED = 1;
    private static final int PURCHASED = 1;
    private static final int PENDING = 2;

    @Test
    void successfulUnacknowledgedPurchasedProductMustBeAcknowledgedBeforeRefreshingEntitlement() {
        PlayBillingPurchaseCompletionAction action = PlayBillingPurchaseCompletionPolicy.from(
                BILLING_OK,
                true,
                PURCHASED,
                false);

        TestAssertions.assertEquals(
                "acknowledge_then_refresh",
                action.persistenceCode(),
                "Successful unacknowledged purchase must be acknowledged before entitlement refresh");
    }

    @Test
    void successfulAcknowledgedPurchasedProductCanRefreshEntitlementImmediately() {
        PlayBillingPurchaseCompletionAction action = PlayBillingPurchaseCompletionPolicy.from(
                BILLING_OK,
                true,
                PURCHASED,
                true);

        TestAssertions.assertEquals(
                "refresh",
                action.persistenceCode(),
                "Successful acknowledged purchase must refresh entitlement immediately");
    }

    @Test
    void successfulPendingProductRefreshesEntitlementAsPending() {
        PlayBillingPurchaseCompletionAction action = PlayBillingPurchaseCompletionPolicy.from(
                BILLING_OK,
                true,
                PENDING,
                false);

        TestAssertions.assertEquals(
                "refresh",
                action.persistenceCode(),
                "Successful pending purchase must refresh entitlement as pending");
    }

    @Test
    void successfulUpdateForAnotherProductMustBeIgnored() {
        PlayBillingPurchaseCompletionAction action = PlayBillingPurchaseCompletionPolicy.from(
                BILLING_OK,
                false,
                PURCHASED,
                false);

        TestAssertions.assertEquals(
                "ignore",
                action.persistenceCode(),
                "Purchase update for another product must not affect Pro entitlement");
    }

    @Test
    void canceledPurchaseUpdateMustBeIgnored() {
        PlayBillingPurchaseCompletionAction action = PlayBillingPurchaseCompletionPolicy.from(
                BILLING_USER_CANCELED,
                true,
                PURCHASED,
                false);

        TestAssertions.assertEquals(
                "ignore",
                action.persistenceCode(),
                "Canceled purchase update must not change entitlement");
    }

    @Test
    void canceledPurchaseWithoutPurchaseListMustRefreshUiOutOfInProgressState() {
        PlayBillingPurchaseCompletionAction action =
                PlayBillingPurchaseCompletionPolicy.fromEmptyPurchaseUpdate(BILLING_USER_CANCELED);

        TestAssertions.assertEquals(
                "refresh",
                action.persistenceCode(),
                "Canceled purchase without purchases must refresh current state so the UI leaves in-progress");
    }

    @Test
    void emptySuccessfulPurchaseUpdateMustBeIgnoredBecauseThereIsNoPurchaseToApply() {
        PlayBillingPurchaseCompletionAction action =
                PlayBillingPurchaseCompletionPolicy.fromEmptyPurchaseUpdate(BILLING_OK);

        TestAssertions.assertEquals(
                "ignore",
                action.persistenceCode(),
                "Successful empty purchase update must not refresh entitlement without a purchase");
    }
}
