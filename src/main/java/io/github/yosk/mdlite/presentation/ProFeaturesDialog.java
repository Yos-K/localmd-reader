package io.github.yosk.mdlite.presentation;

import android.app.AlertDialog;
import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import io.github.yosk.mdlite.domain.BillingPurchaseSnapshot;
import io.github.yosk.mdlite.domain.ProFeatureCatalog;
import io.github.yosk.mdlite.domain.ProFeaturePresentationItem;
import io.github.yosk.mdlite.domain.ProFeaturesPresentation;
import io.github.yosk.mdlite.domain.ProPurchaseFlowCallback;
import io.github.yosk.mdlite.domain.ProProduct;
import io.github.yosk.mdlite.domain.ProPurchasePresentation;
import io.github.yosk.mdlite.domain.ProPurchaseStartResult;
import io.github.yosk.mdlite.domain.ProPurchaseUiState;
import io.github.yosk.mdlite.infrastructure.ProPurchaseStatusCallback;

final class ProFeaturesDialog {
    private final MainActivity activity;

    ProFeaturesDialog(MainActivity activity) {
        this.activity = activity;
    }

    void show() {
        new AlertDialog.Builder(activity)
                .setTitle(activity.viewerText.proFeatures())
                // interaction-surface: pro-features-dialog
                .setView(proFeaturesView())
                .setNegativeButton("OK", null)
                .show();
    }

    void startPurchase() {
        ProPurchaseStartResult result = activity.proPurchaseFlow.start(
                ProProduct.pro(),
                new StartPurchaseCallback(activity, this));
        activity.proPurchaseUiState = ProPurchaseUiState.safe(result.uiState());
        activity.showInfoDialog(activity.viewerText.proFeatures(),
                activity.viewerText.purchaseMessage(currentProFeaturesPresentation().purchase().messageCode()));
    }

    void restorePurchase() {
        activity.proPurchaseStatusRefresh.refreshAt(
                System.currentTimeMillis(),
                new RestorePurchaseCallback(activity, this));
    }

    private View proFeaturesView() {
        ProFeaturesPresentation presentation = currentProFeaturesPresentation();
        ScrollView scroller = new ScrollView(activity);
        LinearLayout list = new LinearLayout(activity);
        list.setOrientation(LinearLayout.VERTICAL);
        list.setPadding(activity.dp(10), activity.dp(6), activity.dp(10), activity.dp(6));

        TextView status = new TextView(activity);
        status.setText(activity.viewerText.proStatus(presentation.isPro()));
        status.setTextColor(activity.textColor());
        status.setTextSize(15);
        status.setTypeface(Typeface.DEFAULT_BOLD);
        status.setPadding(activity.dp(4), 0, activity.dp(4), activity.dp(12));
        list.addView(status, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView intro = new TextView(activity);
        intro.setText(activity.viewerText.proFeaturesIntro());
        intro.setTextColor(activity.mutedColor());
        intro.setTextSize(14);
        intro.setPadding(activity.dp(4), 0, activity.dp(4), activity.dp(14));
        list.addView(intro, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        ProFeaturePresentationItem[] items = presentation.features();
        for (int i = 0; i < items.length; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.bottomMargin = activity.dp(10);
            list.addView(proFeatureRow(items[i]), params);
        }
        addProPurchasePresentation(list, presentation);
        scroller.addView(list);
        return scroller;
    }

    private void addProPurchasePresentation(LinearLayout list, ProFeaturesPresentation presentation) {
        ProPurchasePresentation purchase = presentation.purchase();

        TextView message = new TextView(activity);
        message.setText(activity.viewerText.purchaseMessage(purchase.messageCode()));
        message.setTextColor(activity.mutedColor());
        message.setTextSize(13);
        message.setPadding(activity.dp(4), activity.dp(4), activity.dp(4), 0);
        list.addView(message, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        if (purchase.shouldShowAction()) {
            activity.purchaseProButton = purchaseButton();
            list.addView(activity.purchaseProButton, new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
        }

        if (purchase.shouldShowRestoreAction()) {
            activity.restorePurchaseButton = restoreButton();
            LinearLayout.LayoutParams restoreParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            restoreParams.topMargin = activity.dp(8);
            list.addView(activity.restorePurchaseButton, restoreParams);
        }
    }

    private Button purchaseButton() {
        Button button = new Button(activity);
        button.setText(activity.viewerText.purchaseProAction());
        button.setAllCaps(false);
        button.setTextColor(activity.onPrimaryColor());
        button.setBackground(activity.makeRoundedBackground(activity.primaryColor(), activity.primaryColor(), 8));
        button.setOnClickListener(activity);
        return button;
    }

    private Button restoreButton() {
        Button button = new Button(activity);
        button.setText(activity.viewerText.restorePurchaseAction());
        button.setAllCaps(false);
        button.setTextColor(activity.primaryColor());
        button.setBackground(activity.makeRoundedBackground(activity.surfaceColor(), activity.borderColor(), 8));
        button.setOnClickListener(activity);
        return button;
    }

    private ProFeatureStatusRow proFeatureRow(ProFeaturePresentationItem item) {
        return new ProFeatureStatusRow(
                activity, item.title(), item.description(),
                item.isAvailable() ? activity.viewerText.featureAvailable() : activity.viewerText.featureLocked(),
                item.isAvailable(),
                activity.surfaceColor(), activity.borderColor(),
                activity.textColor(), activity.mutedColor(), activity.primaryStrongColor());
    }

    private ProFeaturesPresentation currentProFeaturesPresentation() {
        return ProFeaturesPresentation.from(
                activity.featureEntitlement,
                activity.proPurchaseUiState,
                ProFeatureCatalog.initialFeatures());
    }

    private static final class StartPurchaseCallback implements ProPurchaseFlowCallback {
        private final MainActivity activity;
        private final ProFeaturesDialog dialog;

        private StartPurchaseCallback(MainActivity activity, ProFeaturesDialog dialog) {
            this.activity = activity;
            this.dialog = dialog;
        }

        @Override
        public void onPurchaseStartResolved(ProPurchaseStartResult result) {
            activity.runOnUiThread(new StartPurchaseUiUpdate(activity, dialog, result));
        }
    }

    private static final class StartPurchaseUiUpdate implements Runnable {
        private final MainActivity activity;
        private final ProFeaturesDialog dialog;
        private final ProPurchaseStartResult result;

        private StartPurchaseUiUpdate(
                MainActivity activity,
                ProFeaturesDialog dialog,
                ProPurchaseStartResult result) {
            this.activity = activity;
            this.dialog = dialog;
            this.result = result;
        }

        @Override
        public void run() {
            activity.proPurchaseUiState = ProPurchaseUiState.safe(result.uiState());
            activity.showInfoDialog(
                    activity.viewerText.proFeatures(),
                    activity.viewerText.purchaseMessage(dialog.currentProFeaturesPresentation().purchase().messageCode()));
        }
    }

    private static final class RestorePurchaseCallback implements ProPurchaseStatusCallback {
        private final MainActivity activity;
        private final ProFeaturesDialog dialog;

        private RestorePurchaseCallback(MainActivity activity, ProFeaturesDialog dialog) {
            this.activity = activity;
            this.dialog = dialog;
        }

        @Override
        public void onCurrentSnapshot(BillingPurchaseSnapshot snapshot) {
            activity.runOnUiThread(new RestorePurchaseUiUpdate(activity, dialog, snapshot));
        }
    }

    private static final class RestorePurchaseUiUpdate implements Runnable {
        private final MainActivity activity;
        private final ProFeaturesDialog dialog;
        private final BillingPurchaseSnapshot snapshot;

        private RestorePurchaseUiUpdate(
                MainActivity activity,
                ProFeaturesDialog dialog,
                BillingPurchaseSnapshot snapshot) {
            this.activity = activity;
            this.dialog = dialog;
            this.snapshot = snapshot;
        }

        @Override
        public void run() {
            activity.proPurchaseUiState = ProPurchaseUiState.fromPurchaseState(snapshot.proPurchaseState());
            activity.reloadFeatureEntitlement();
            activity.showInfoDialog(
                    activity.viewerText.proFeatures(),
                    activity.viewerText.purchaseMessage(dialog.currentProFeaturesPresentation().purchase().messageCode()));
        }
    }
}
