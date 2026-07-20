package io.github.yosk.mdlite.presentation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import io.github.yosk.mdlite.domain.FeatureEntitlement;
import io.github.yosk.mdlite.viewer.ViewerTheme;

final class SettingsDialogs {
    private final MainActivity activity;
    private final ProFeaturesDialog proFeaturesDialog;

    SettingsDialogs(MainActivity activity) {
        this.activity = activity;
        this.proFeaturesDialog = new ProFeaturesDialog(activity);
    }

    void showThemeDialog() {
        ViewerTheme[] themes = availableThemes();
        new AlertDialog.Builder(activity)
                .setTitle(activity.viewerText.themeLabel(activity.currentTheme))
                // interaction-surface: theme-dialog
                .setItems(themeLabels(themes), new ThemeClickListener(activity, themes))
                .setNegativeButton("OK", null)
                .show();
    }

    void showPrivacyPolicyDialog() {
        activity.showInfoDialog(activity.viewerText.privacy(), activity.viewerText.privacyMessage());
    }

    void showProFeaturesDialog() {
        proFeaturesDialog.show();
    }

    void showClipboardDiagnosticsDialog() {
        android.content.ClipData clip = clipboardClip();
        activity.showInfoDialog(activity.viewerText.clipboardDiagnostics(),
                AndroidTextDiagnostics.describe(activity, clip));
    }

    void startProPurchase() {
        proFeaturesDialog.startPurchase();
    }

    void restoreProPurchase() {
        proFeaturesDialog.restorePurchase();
    }

    private ViewerTheme[] availableThemes() {
        return ViewerTheme.availableThemes(activity.featureEntitlement);
    }

    private String[] themeLabels(ViewerTheme[] themes) {
        String[] labels = new String[themes.length];
        for (int i = 0; i < themes.length; i++) {
            labels[i] = activity.viewerText.themeLabel(themes[i]);
        }
        return labels;
    }

    private android.content.ClipData clipboardClip() {
        android.content.ClipboardManager clipboard =
                (android.content.ClipboardManager) activity.getSystemService(
                        android.content.Context.CLIPBOARD_SERVICE);
        if (clipboard == null || !clipboard.hasPrimaryClip()) {
            return null;
        }
        android.content.ClipData clip = clipboard.getPrimaryClip();
        if (clip == null || clip.getItemCount() == 0) {
            return null;
        }
        return clip;
    }

    private static final class ThemeClickListener implements DialogInterface.OnClickListener {
        private final MainActivity activity;
        private final ViewerTheme[] themes;

        private ThemeClickListener(MainActivity activity, ViewerTheme[] themes) {
            this.activity = activity;
            this.themes = themes;
        }

        @Override
        public void onClick(DialogInterface dialog, int which) {
            activity.applySelectedTheme(themes[which]);
        }
    }
}
