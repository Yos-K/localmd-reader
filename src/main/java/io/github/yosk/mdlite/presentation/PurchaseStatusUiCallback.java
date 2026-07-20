package io.github.yosk.mdlite.presentation;

import io.github.yosk.mdlite.domain.BillingPurchaseSnapshot;
import io.github.yosk.mdlite.domain.ProPurchaseUiState;
import io.github.yosk.mdlite.infrastructure.ProPurchaseStatusCallback;

final class PurchaseStatusUiCallback implements ProPurchaseStatusCallback {
    private final MainActivity activity;

    PurchaseStatusUiCallback(MainActivity activity) {
        if (activity == null) {
            throw new IllegalArgumentException("Activity must not be null.");
        }
        this.activity = activity;
    }

    @Override
    public void onCurrentSnapshot(BillingPurchaseSnapshot snapshot) {
        activity.runOnUiThread(new PurchaseStatusUiUpdate(activity, snapshot));
    }

    private static final class PurchaseStatusUiUpdate implements Runnable {
        private final MainActivity activity;
        private final BillingPurchaseSnapshot snapshot;

        private PurchaseStatusUiUpdate(MainActivity activity, BillingPurchaseSnapshot snapshot) {
            this.activity = activity;
            this.snapshot = snapshot;
        }

        @Override
        public void run() {
            activity.proPurchaseUiState = ProPurchaseUiState.fromPurchaseState(snapshot.proPurchaseState());
            activity.reloadFeatureEntitlement();
        }
    }
}
