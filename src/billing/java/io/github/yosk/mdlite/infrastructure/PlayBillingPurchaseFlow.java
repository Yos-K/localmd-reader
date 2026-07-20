package io.github.yosk.mdlite.infrastructure;

import android.app.Activity;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryProductDetailsResult;
import io.github.yosk.mdlite.domain.ProProduct;
import io.github.yosk.mdlite.domain.ProPurchaseFlow;
import io.github.yosk.mdlite.domain.ProPurchaseFlowCallback;
import io.github.yosk.mdlite.domain.ProPurchaseStartResult;
import java.util.Collections;
import java.util.List;

public final class PlayBillingPurchaseFlow implements ProPurchaseFlow {
    private final Activity activity;
    private final BillingClient billingClient;

    public PlayBillingPurchaseFlow(Activity activity, BillingClient billingClient) {
        if (activity == null) {
            throw new IllegalArgumentException("Activity must not be null.");
        }
        if (billingClient == null) {
            throw new IllegalArgumentException("Billing client must not be null.");
        }
        this.activity = activity;
        this.billingClient = billingClient;
    }

    @Override
    public ProPurchaseStartResult start(ProProduct product, ProPurchaseFlowCallback callback) {
        if (product == null) {
            throw new IllegalArgumentException("Product must not be null.");
        }
        if (callback == null) {
            throw new IllegalArgumentException("Purchase flow callback must not be null.");
        }
        if (!billingClient.isReady()) {
            billingClient.startConnection(new QueryProductDetailsAfterConnection(product, callback));
            return ProPurchaseStartResult.started();
        }
        queryProductDetails(product, callback);
        return ProPurchaseStartResult.started();
    }

    private void queryProductDetails(ProProduct product, ProPurchaseFlowCallback callback) {
        QueryProductDetailsParams.Product billingProduct = QueryProductDetailsParams.Product.newBuilder()
                .setProductId(product.productId())
                .setProductType(BillingClient.ProductType.INAPP)
                .build();
        QueryProductDetailsParams params = QueryProductDetailsParams.newBuilder()
                .setProductList(Collections.singletonList(billingProduct))
                .build();
        billingClient.queryProductDetailsAsync(params, new LaunchBillingFlowAfterProductDetails(callback));
    }

    private final class QueryProductDetailsAfterConnection implements BillingClientStateListener {
        private final ProProduct product;
        private final ProPurchaseFlowCallback callback;
        private final PlayBillingPendingSetupResolution setupResolution = PlayBillingPendingSetupResolution.pending();

        private QueryProductDetailsAfterConnection(ProProduct product, ProPurchaseFlowCallback callback) {
            this.product = product;
            this.callback = callback;
        }

        @Override
        public void onBillingSetupFinished(BillingResult billingResult) {
            if (billingResult == null
                    || !PlayBillingPurchaseStartConnectionPolicy.shouldQueryProductDetailsAfterSetup(
                            billingResult.getResponseCode())) {
                setupResolution.complete();
                callback.onPurchaseStartResolved(ProPurchaseStartResult.unavailable());
                return;
            }
            setupResolution.complete();
            queryProductDetails(product, callback);
        }

        @Override
        public void onBillingServiceDisconnected() {
            if (setupResolution.resolveIfPending()) {
                callback.onPurchaseStartResolved(
                        PlayBillingPurchaseStartConnectionPolicy.startResultAfterDisconnectedSetup());
            }
        }
    }

    private final class LaunchBillingFlowAfterProductDetails implements ProductDetailsResponseListener {
        private final ProPurchaseFlowCallback callback;

        private LaunchBillingFlowAfterProductDetails(ProPurchaseFlowCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onProductDetailsResponse(BillingResult billingResult, QueryProductDetailsResult result) {
            if (billingResult == null || billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                callback.onPurchaseStartResolved(ProPurchaseStartResult.unavailable());
                return;
            }
            if (result == null) {
                callback.onPurchaseStartResolved(ProPurchaseStartResult.unavailable());
                return;
            }
            List<ProductDetails> productDetailsList = result.getProductDetailsList();
            if (productDetailsList == null || productDetailsList.isEmpty()) {
                callback.onPurchaseStartResolved(ProPurchaseStartResult.unavailable());
                return;
            }
            BillingFlowParams.ProductDetailsParams detailsParams =
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                            .setProductDetails(productDetailsList.get(0))
                            .build();
            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                    .setProductDetailsParamsList(Collections.singletonList(detailsParams))
                    .build();
            BillingResult launchResult = billingClient.launchBillingFlow(activity, flowParams);
            if (launchResult == null || launchResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                callback.onPurchaseStartResolved(ProPurchaseStartResult.unavailable());
            }
        }
    }
}
