package io.github.yosk.mdlite.infrastructure;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import io.github.yosk.mdlite.domain.ProProduct;
import java.util.List;

final class PlayBillingPurchaseCompletionListener implements PurchasesUpdatedListener {
    private final ProProduct product;
    private final ProPurchaseStatusRefresh statusRefresh;
    private final ProPurchaseStatusCallback statusCallback;
    private BillingClient billingClient;

    PlayBillingPurchaseCompletionListener(
            ProProduct product,
            ProPurchaseStatusRefresh statusRefresh,
            ProPurchaseStatusCallback statusCallback) {
        if (product == null) {
            throw new IllegalArgumentException("Product must not be null.");
        }
        if (statusRefresh == null) {
            throw new IllegalArgumentException("Purchase status refresh must not be null.");
        }
        if (statusCallback == null) {
            throw new IllegalArgumentException("Purchase status callback must not be null.");
        }
        this.product = product;
        this.statusRefresh = statusRefresh;
        this.statusCallback = statusCallback;
    }

    void attach(BillingClient billingClient) {
        if (billingClient == null) {
            throw new IllegalArgumentException("Billing client must not be null.");
        }
        this.billingClient = billingClient;
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult == null) {
            return;
        }
        if (purchases == null || purchases.isEmpty()) {
            handleEmptyPurchaseUpdate(billingResult.getResponseCode());
            return;
        }
        for (int i = 0; i < purchases.size(); i++) {
            handlePurchase(billingResult.getResponseCode(), purchases.get(i));
        }
    }

    private void handleEmptyPurchaseUpdate(int responseCode) {
        PlayBillingPurchaseCompletionAction action =
                PlayBillingPurchaseCompletionPolicy.fromEmptyPurchaseUpdate(responseCode);
        if (action.shouldRefresh()) {
            refreshNow();
        }
    }

    private void handlePurchase(int responseCode, Purchase purchase) {
        if (purchase == null) {
            return;
        }
        PlayBillingPurchaseCompletionAction action = PlayBillingPurchaseCompletionPolicy.from(
                responseCode,
                purchase.getProducts().contains(product.productId()),
                purchase.getPurchaseState(),
                purchase.isAcknowledged());
        if (action.shouldAcknowledgeThenRefresh()) {
            acknowledgeThenRefresh(purchase);
            return;
        }
        if (action.shouldRefresh()) {
            refreshNow();
        }
    }

    private void acknowledgeThenRefresh(Purchase purchase) {
        if (billingClient == null) {
            return;
        }
        AcknowledgePurchaseParams params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();
        billingClient.acknowledgePurchase(params, new RefreshAfterAcknowledge());
    }

    private void refreshNow() {
        statusRefresh.refreshAt(System.currentTimeMillis(), statusCallback);
    }

    private final class RefreshAfterAcknowledge implements AcknowledgePurchaseResponseListener {
        @Override
        public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
            if (billingResult == null || billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                return;
            }
            refreshNow();
        }
    }
}
