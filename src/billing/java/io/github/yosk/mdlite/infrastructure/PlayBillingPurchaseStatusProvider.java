package io.github.yosk.mdlite.infrastructure;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.QueryPurchasesParams;
import io.github.yosk.mdlite.domain.BillingPurchaseSnapshot;
import io.github.yosk.mdlite.domain.ProProduct;
import java.util.List;

public final class PlayBillingPurchaseStatusProvider implements ProPurchaseStatusProvider {
    private final BillingClient billingClient;
    private final ProProduct product;

    public PlayBillingPurchaseStatusProvider(BillingClient billingClient, ProProduct product) {
        if (billingClient == null) {
            throw new IllegalArgumentException("Billing client must not be null.");
        }
        if (product == null) {
            throw new IllegalArgumentException("Pro product must not be null.");
        }
        this.billingClient = billingClient;
        this.product = product;
    }

    @Override
    public void queryCurrentSnapshot(ProPurchaseStatusCallback callback) {
        if (callback == null) {
            throw new IllegalArgumentException("Purchase status callback must not be null.");
        }
        if (!billingClient.isReady()) {
            billingClient.startConnection(new QueryAfterConnectionCallback(callback));
            return;
        }
        queryReadyClient(callback);
    }

    private void queryReadyClient(ProPurchaseStatusCallback callback) {
        QueryPurchasesParams params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.INAPP)
                .build();
        billingClient.queryPurchasesAsync(params, new SnapshotPurchasesResponseListener(product, callback));
    }

    private final class QueryAfterConnectionCallback implements BillingClientStateListener {
        private final ProPurchaseStatusCallback callback;
        private final PlayBillingPendingSetupResolution setupResolution = PlayBillingPendingSetupResolution.pending();

        private QueryAfterConnectionCallback(ProPurchaseStatusCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onBillingSetupFinished(BillingResult billingResult) {
            if (billingResult == null
                    || !PlayBillingConnectionResultPolicy.shouldQueryPurchasesAfterSetup(billingResult.getResponseCode())) {
                setupResolution.complete();
                callback.onCurrentSnapshot(BillingPurchaseSnapshot.billingUnavailable());
                return;
            }
            setupResolution.complete();
            queryReadyClient(callback);
        }

        @Override
        public void onBillingServiceDisconnected() {
            if (setupResolution.resolveIfPending()) {
                callback.onCurrentSnapshot(PlayBillingConnectionResultPolicy.statusSnapshotAfterDisconnectedSetup());
            }
        }
    }

    private static final class SnapshotPurchasesResponseListener implements PurchasesResponseListener {
        private final ProProduct product;
        private final ProPurchaseStatusCallback callback;

        private SnapshotPurchasesResponseListener(ProProduct product, ProPurchaseStatusCallback callback) {
            this.product = product;
            this.callback = callback;
        }

        @Override
        public void onQueryPurchasesResponse(BillingResult billingResult, List<Purchase> purchases) {
            if (billingResult == null || billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                callback.onCurrentSnapshot(BillingPurchaseSnapshot.billingUnavailable());
                return;
            }
            callback.onCurrentSnapshot(snapshotFrom(purchases));
        }

        private BillingPurchaseSnapshot snapshotFrom(List<Purchase> purchases) {
            if (purchases == null) {
                return BillingPurchaseSnapshot.notPurchased();
            }
            for (int i = 0; i < purchases.size(); i++) {
                Purchase purchase = purchases.get(i);
                if (purchase != null && purchase.getProducts().contains(product.productId())) {
                    return PlayBillingPurchaseSnapshotMapper.fromPurchaseState(
                            purchase.getPurchaseState(),
                            purchase.isAcknowledged());
                }
            }
            return BillingPurchaseSnapshot.notPurchased();
        }
    }
}
