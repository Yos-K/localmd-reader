package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.ProPurchaseFlow;
import io.github.yosk.mdlite.domain.UnavailableProPurchaseFlow;

public final class BuildProPurchaseFlow {
    private BuildProPurchaseFlow() {
    }

    public static ProPurchaseFlow current() {
        return UnavailableProPurchaseFlow.instance();
    }

    public static ProPurchaseFlow current(ProPurchaseFlow billingFlow) {
        if (billingFlow == null) {
            throw new IllegalArgumentException("Billing purchase flow must not be null.");
        }
        if (BuildConfig.PLAY_BILLING_ENABLED) {
            return billingFlow;
        }
        return UnavailableProPurchaseFlow.instance();
    }
}
