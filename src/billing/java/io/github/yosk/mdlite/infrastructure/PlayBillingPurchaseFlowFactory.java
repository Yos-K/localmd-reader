package io.github.yosk.mdlite.infrastructure;

import android.app.Activity;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.PendingPurchasesParams;
import io.github.yosk.mdlite.domain.ProProduct;
import io.github.yosk.mdlite.domain.ProPurchaseFlow;

public final class PlayBillingPurchaseFlowFactory {
    private PlayBillingPurchaseFlowFactory() {
    }

    public static ProPurchaseFlow current(
            Activity activity,
            ProPurchaseStatusRefresh statusRefresh,
            ProPurchaseStatusCallback statusCallback) {
        if (activity == null) {
            throw new IllegalArgumentException("Activity must not be null.");
        }
        PlayBillingPurchaseCompletionListener listener =
                new PlayBillingPurchaseCompletionListener(ProProduct.pro(), statusRefresh, statusCallback);
        BillingClient billingClient = BillingClient.newBuilder(activity.getApplicationContext())
                .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
                .enableAutoServiceReconnection()
                .setListener(listener)
                .build();
        listener.attach(billingClient);
        return BuildProPurchaseFlow.current(new PlayBillingPurchaseFlow(activity, billingClient));
    }
}
