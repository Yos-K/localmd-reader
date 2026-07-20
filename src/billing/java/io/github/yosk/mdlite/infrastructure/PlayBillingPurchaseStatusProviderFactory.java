package io.github.yosk.mdlite.infrastructure;

import android.content.Context;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import io.github.yosk.mdlite.domain.ProProduct;
import java.util.List;

public final class PlayBillingPurchaseStatusProviderFactory {
    private PlayBillingPurchaseStatusProviderFactory() {
    }

    public static ProPurchaseStatusProvider current(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null.");
        }
        BillingClient billingClient = BillingClient.newBuilder(context.getApplicationContext())
                .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
                .enableAutoServiceReconnection()
                .setListener(new NoOpPurchasesUpdatedListener())
                .build();
        return BuildProPurchaseStatusProvider.current(
                new PlayBillingPurchaseStatusProvider(billingClient, ProProduct.pro()));
    }

    private static final class NoOpPurchasesUpdatedListener implements PurchasesUpdatedListener {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        }
    }
}
