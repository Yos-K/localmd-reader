package io.github.yosk.mdlite.presentation;


final class SettingsDialogs {
    private final MainActivity activity;
    private final ProFeaturesDialog proFeaturesDialog;

    SettingsDialogs(MainActivity activity) {
        this.activity = activity;
        this.proFeaturesDialog = new ProFeaturesDialog(activity);
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

}
