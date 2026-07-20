package io.github.yosk.mdlite.presentation;

import android.app.AlertDialog;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import io.github.yosk.mdlite.domain.ViewerFeature;
import io.github.yosk.mdlite.viewer.CustomGestureShape;
import io.github.yosk.mdlite.viewer.CustomGestureShortcut;
import io.github.yosk.mdlite.viewer.GestureShortcutAction;
import io.github.yosk.mdlite.viewer.GestureShortcutBinding;
import io.github.yosk.mdlite.viewer.GestureShortcutTrigger;

final class GestureShortcutDialogs {
    private final MainActivity activity;

    GestureShortcutDialogs(MainActivity activity) {
        this.activity = activity;
    }

    boolean hasCustomGestureShortcut() {
        return activity.settingsStore.loadCustomGestureShortcut() != null;
    }

    void showGestureShortcutsDialog() {
        GestureShortcutListeners.RowClickListener listener = new GestureShortcutListeners.RowClickListener(this);
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle(activity.viewerText.gestures())
                // interaction-surface: gesture-shortcuts-dialog
                .setView(gestureShortcutListView(listener))
                .setNegativeButton("OK", null)
                .create();
        listener.attach(dialog);
        dialog.show();
    }

    void onCustomGestureDrawn(float[] xs, float[] ys) {
        try {
            // Same dp normalization as CircleGestureTrace: the registered shape
            // and later recognition input must share physical units (#147).
            activity.pendingCustomGestureShape =
                    CustomGestureShape.fromPoints(toDp(xs), toDp(ys));
            finishCustomGestureDrawing();
            showCustomGestureActionDialog();
        } catch (IllegalArgumentException e) {
            finishCustomGestureDrawing();
            activity.showInfoDialog(activity.viewerText.registerCustomGesture(),
                    activity.viewerText.customGestureTooSmall());
        }
    }

    private float[] toDp(float[] px) {
        float density = activity.getResources().getDisplayMetrics().density;
        float[] dp = new float[px.length];
        for (int i = 0; i < px.length; i++) {
            dp[i] = px[i] / density;
        }
        return dp;
    }

    private android.view.View gestureShortcutListView(GestureShortcutListeners.RowClickListener listener) {
        LinearLayout list = new LinearLayout(activity);
        list.setOrientation(LinearLayout.VERTICAL);
        list.setPadding(activity.dp(8), activity.dp(6), activity.dp(8), activity.dp(6));
        list.addView(gestureShortcutRow(0, GesturePreviewKind.DOUBLE_TAP, doubleTapShortcut(), listener));
        list.addView(gestureShortcutRow(1, GesturePreviewKind.CIRCLE, circleGestureShortcut(), listener));
        list.addView(gestureShortcutRow(2, GesturePreviewKind.CHEVRON_LEFT, gestureAction(GestureShortcutTrigger.swipeLeft()), listener));
        list.addView(gestureShortcutRow(3, GesturePreviewKind.CHEVRON_RIGHT, gestureAction(GestureShortcutTrigger.swipeRight()), listener));
        list.addView(gestureShortcutRow(4, GesturePreviewKind.CHEVRON_UP, gestureAction(GestureShortcutTrigger.swipeUp()), listener));
        list.addView(gestureShortcutRow(5, GesturePreviewKind.CHEVRON_DOWN, gestureAction(GestureShortcutTrigger.swipeDown()), listener));
        list.addView(gestureShortcutRow(6, GesturePreviewKind.CUSTOM, customGestureShortcut(), listener));
        return list;
    }

    private GestureShortcutRow gestureShortcutRow(int targetIndex, int previewKind,
            GestureShortcutAction action, GestureShortcutListeners.RowClickListener listener) {
        boolean available = gestureShortcutTargetAvailable(targetIndex);
        return new GestureShortcutRow(activity, targetIndex, previewKind,
                shortcutActionLabel(targetIndex, action),
                !action.isOff() && available,
                action.isOff() ? activity.surfaceColor() : activity.surfaceAltColor(),
                activity.borderColor(), activity.primaryStrongColor(),
                activity.textColor(), activity.mutedColor(), listener);
    }

    private boolean gestureShortcutTargetAvailable(int targetIndex) {
        if (targetIndex == 0) {
            return activity.featureEntitlement.allows(ViewerFeature.DOUBLE_TAP_SHORTCUTS);
        }
        return activity.featureEntitlement.allows(ViewerFeature.CUSTOM_GESTURE_SHORTCUTS);
    }

    void selectGestureTarget(int index) {
        if (index == 0) { showDoubleTapShortcutDialog(); return; }
        if (!activity.featureEntitlement.allows(ViewerFeature.CUSTOM_GESTURE_SHORTCUTS)) {
            activity.showProFeaturesDialog();
            return;
        }
        if (index == 1) { showCircleGestureShortcutDialog(); return; }
        if (index == 2) { showDirectionalGestureDialog("<", GestureShortcutTrigger.swipeLeft()); return; }
        if (index == 3) { showDirectionalGestureDialog(">", GestureShortcutTrigger.swipeRight()); return; }
        if (index == 4) { showDirectionalGestureDialog("^", GestureShortcutTrigger.swipeUp()); return; }
        if (index == 5) { showDirectionalGestureDialog("v", GestureShortcutTrigger.swipeDown()); return; }
        if (index == 6) { showCustomGestureDialog(); }
    }

    private void showDoubleTapShortcutDialog() {
        GestureShortcutAction[] actions = availableGestureActions();
        new AlertDialog.Builder(activity)
                .setTitle(activity.viewerText.doubleTapPrefix())
                // interaction-surface: gesture-shortcuts-dialog
                .setItems(gestureActionLabels(actions), new GestureShortcutListeners.DoubleTapClickListener(this, actions))
                .setNegativeButton("OK", null)
                .show();
    }

    private void showCircleGestureShortcutDialog() {
        GestureShortcutAction[] actions = availableGestureActions();
        new AlertDialog.Builder(activity)
                .setTitle(activity.viewerText.circleGesturePrefix())
                // interaction-surface: gesture-shortcuts-dialog
                .setItems(gestureActionLabels(actions), new GestureShortcutListeners.CircleClickListener(this, actions))
                .setNegativeButton("OK", null)
                .show();
    }

    private void showCustomGestureDialog() {
        new AlertDialog.Builder(activity)
                .setTitle(activity.viewerText.customGesturePrefix())
                // interaction-surface: gesture-shortcuts-dialog
                .setItems(customGestureMenuLabels(), new GestureShortcutListeners.CustomMenuClickListener(this))
                .setNegativeButton("OK", null)
                .show();
    }

    private void showDirectionalGestureDialog(String title, GestureShortcutTrigger trigger) {
        GestureShortcutAction[] actions = availableGestureActions();
        new AlertDialog.Builder(activity)
                .setTitle(title)
                // interaction-surface: gesture-shortcuts-dialog
                .setItems(gestureActionLabels(actions), new GestureShortcutListeners.DirectionalClickListener(this, trigger, actions))
                .setNegativeButton("OK", null)
                .show();
    }

    private void showCustomGestureActionDialog() {
        GestureShortcutAction[] actions = availableGestureActions();
        new AlertDialog.Builder(activity)
                .setTitle(activity.viewerText.registerCustomGesture())
                // interaction-surface: gesture-shortcuts-dialog
                .setItems(gestureActionLabels(actions), new GestureShortcutListeners.CustomActionClickListener(this, actions))
                .setNegativeButton("OK", null)
                .show();
    }

    void showChangeCustomGestureActionDialog() {
        CustomGestureShortcut shortcut = activity.settingsStore.loadCustomGestureShortcut();
        if (shortcut == null) {
            return;
        }
        activity.pendingCustomGestureShape = shortcut.shape();
        GestureShortcutAction[] actions = availableGestureActions();
        new AlertDialog.Builder(activity)
                .setTitle(activity.viewerText.changeCustomGestureAction())
                // interaction-surface: gesture-shortcuts-dialog
                .setItems(gestureActionLabels(actions), new GestureShortcutListeners.CustomActionClickListener(this, actions))
                .setNegativeButton("OK", null)
                .show();
    }

    void applyDoubleTapShortcut(GestureShortcutAction action) {
        applyGestureShortcut(GestureShortcutTrigger.doubleTap(), action);
        activity.updateLocalizedText();
        showGestureShortcutsDialog();
    }

    void applyCircleGestureShortcut(GestureShortcutAction action) {
        applyGestureShortcut(GestureShortcutTrigger.circle(), action);
        activity.updateLocalizedText();
        showGestureShortcutsDialog();
    }

    void applyDirectionalGestureShortcut(GestureShortcutTrigger trigger, GestureShortcutAction action) {
        applyGestureShortcut(trigger, action);
        activity.updateLocalizedText();
        showGestureShortcutsDialog();
    }

    private void applyGestureShortcut(GestureShortcutTrigger trigger, GestureShortcutAction action) {
        GestureShortcutBinding binding = GestureShortcutBinding.of(trigger, action);
        activity.gestureShortcutBindings = activity.gestureShortcutBindings.put(binding);
        activity.settingsStore.saveGestureShortcutBinding(binding);
    }

    void startCustomGestureRegistration() {
        activity.closeMenu();
        activity.customGestureDrawingView = new CustomGestureDrawingView(activity,
                activity.viewerText.drawCustomGestureInstruction(),
                activity.backgroundColor(), activity.primaryColor(), activity.textColor(), activity);
        activity.appRoot.addView(activity.customGestureDrawingView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
    }

    void clearCustomGestureShortcut() {
        activity.settingsStore.clearCustomGestureShortcut();
        activity.gestureShortcutBindings = activity.settingsStore.loadGestureShortcutBindings();
        activity.updateLocalizedText();
        showGestureShortcutsDialog();
    }

    void saveCustomGestureShortcut(GestureShortcutAction action) {
        if (activity.pendingCustomGestureShape == null) {
            return;
        }
        if (action.isOff()) {
            activity.settingsStore.clearCustomGestureShortcut();
            activity.gestureShortcutBindings = activity.settingsStore.loadGestureShortcutBindings();
            activity.pendingCustomGestureShape = null;
            activity.updateLocalizedText();
            return;
        }
        CustomGestureShortcut shortcut = CustomGestureShortcut.of(activity.pendingCustomGestureShape, action);
        activity.settingsStore.saveCustomGestureShortcut(shortcut);
        activity.gestureShortcutBindings = activity.settingsStore.loadGestureShortcutBindings();
        activity.pendingCustomGestureShape = null;
        activity.updateLocalizedText();
        showGestureShortcutsDialog();
    }

    private void finishCustomGestureDrawing() {
        if (activity.customGestureDrawingView != null) {
            activity.appRoot.removeView(activity.customGestureDrawingView);
            activity.customGestureDrawingView = null;
        }
    }

    private String shortcutActionLabel(int targetIndex, GestureShortcutAction action) {
        if (!gestureShortcutTargetAvailable(targetIndex)) {
            return activity.viewerText.proOnly();
        }
        return actionLabel(action);
    }

    String actionLabel(GestureShortcutAction action) {
        if (action.isOpenFile()) return activity.viewerText.openFile();
        if (action.isOpenMenu()) return activity.viewerText.openMenu();
        if (action.isPreviousTab()) return activity.viewerText.previousTabAction();
        if (action.isNextTab()) return activity.viewerText.nextTabAction();
        if (action.isNextTheme()) return activity.viewerText.nextThemeAction();
        if (action.isMoveControls()) return activity.viewerText.moveControlsAction();
        if (action.isShowSearchBar()) return activity.viewerText.showSearchBarAction();
        if (action.isNextHeading()) return activity.viewerText.nextHeadingAction();
        if (action.isPreviousHeading()) return activity.viewerText.previousHeadingAction();
        return activity.viewerText.off();
    }

    private String[] customGestureMenuLabels() {
        if (activity.settingsStore.loadCustomGestureShortcut() != null) {
            return new String[] {
                activity.viewerText.registerCustomGesture(),
                activity.viewerText.changeCustomGestureAction(),
                activity.viewerText.clearCustomGesture()
            };
        }
        return new String[] {
            activity.viewerText.registerCustomGesture(),
            activity.viewerText.clearCustomGesture()
        };
    }

    private GestureShortcutAction[] availableGestureActions() {
        return GestureShortcutAction.availableActions(activity.featureEntitlement);
    }

    private String[] gestureActionLabels(GestureShortcutAction[] actions) {
        String[] labels = new String[actions.length];
        for (int i = 0; i < actions.length; i++) {
            labels[i] = actionLabel(actions[i]);
        }
        return labels;
    }

    private GestureShortcutAction doubleTapShortcut() {
        return activity.gestureShortcutBindings.actionFor(GestureShortcutTrigger.doubleTap());
    }

    private GestureShortcutAction circleGestureShortcut() {
        return activity.gestureShortcutBindings.actionFor(GestureShortcutTrigger.circle());
    }

    private GestureShortcutAction customGestureShortcut() {
        return gestureAction(GestureShortcutTrigger.customShape());
    }

    private GestureShortcutAction gestureAction(GestureShortcutTrigger trigger) {
        return activity.gestureShortcutBindings.actionFor(trigger);
    }

}
