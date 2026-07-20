package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.BillingPurchaseSnapshot;

public interface ProPurchaseStatusCallback {
    void onCurrentSnapshot(BillingPurchaseSnapshot snapshot);
}
