package io.github.yosk.mdlite.presentation;

import android.app.AlertDialog;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import io.github.yosk.mdlite.domain.ViewerFeature;
import io.github.yosk.mdlite.viewer.CustomGestureMenu;
import io.github.yosk.mdlite.viewer.CustomGestureShape;
import io.github.yosk.mdlite.viewer.CustomGestureShortcut;
import io.github.yosk.mdlite.viewer.GestureShortcutAction;
import io.github.yosk.mdlite.viewer.GestureShortcutBinding;
import io.github.yosk.mdlite.viewer.GestureShortcutTrigger;

final class GestureShortcutDialogs {
    private final MainActivity activity;
    private final GestureShortcutDialogContent content;

    GestureShortcutDialogs(MainActivity activity) {
        this.activity = activity;
        this.content = new GestureShortcutDialogContent(activity);
    }

    boolean hasCustomGestureShortcut() {
        return activity.settingsStore.loadCustomGestureShortcut() != null;
    }

    android.view.View gestureShortcutListView() {
        GestureShortcutListeners.RowClickListener listener = new GestureShortcutListeners.RowClickListener(this);
        return gestureShortcutListView(listener);
    }

    void onCustomGestureDrawn(float[] xs, float[] ys) {
        try {
            // Same dp normalization as CircleGestureTrace: the registered shape
            // and later recognition input must share physical units (#147).
            activity.pendingCustomGestureShape = CustomGestureShape.fromPoints(toDp(xs), toDp(ys));
            finishCustomGestureDrawing();
            showCustomGestureActionDialog();
        } catch (IllegalArgumentException e) {
            finishCustomGestureDrawing();
            activity.showInfoDialog(
                    activity.viewerText.registerCustomGesture(), activity.viewerText.customGestureTooSmall());
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
        list.addView(gestureShortcutRow(
                0, GesturePreviewKind.DOUBLE_TAP, content.action(GestureShortcutTrigger.doubleTap()), listener));
        list.addView(gestureShortcutRow(
                1, GesturePreviewKind.CIRCLE, content.action(GestureShortcutTrigger.circle()), listener));
        list.addView(gestureShortcutRow(
                2, GesturePreviewKind.CHEVRON_LEFT, content.action(GestureShortcutTrigger.swipeLeft()), listener));
        list.addView(gestureShortcutRow(
                3, GesturePreviewKind.CHEVRON_RIGHT, content.action(GestureShortcutTrigger.swipeRight()), listener));
        list.addView(gestureShortcutRow(
                4, GesturePreviewKind.CHEVRON_UP, content.action(GestureShortcutTrigger.swipeUp()), listener));
        list.addView(gestureShortcutRow(
                5, GesturePreviewKind.CHEVRON_DOWN, content.action(GestureShortcutTrigger.swipeDown()), listener));
        list.addView(gestureShortcutRow(
                6, GesturePreviewKind.CUSTOM, content.action(GestureShortcutTrigger.customShape()), listener));
        return list;
    }

    private GestureShortcutRow gestureShortcutRow(int targetIndex, int previewKind, GestureShortcutAction action,
            GestureShortcutListeners.RowClickListener listener) {
        boolean available = content.targetAvailable(targetIndex);
        return new GestureShortcutRow(activity, targetIndex, previewKind,
                content.shortcutActionLabel(targetIndex, action), !action.isOff() && available,
                action.isOff() ? activity.surfaceColor() : activity.surfaceAltColor(), activity.borderColor(),
                activity.primaryStrongColor(), activity.textColor(), activity.mutedColor(), listener);
    }

    void selectShortcutAction(int index) {
        if (index == 0) {
            showDoubleTapShortcutDialog();
            return;
        }
        if (!activity.featureEntitlement.allows(ViewerFeature.CUSTOM_GESTURE_SHORTCUTS)) {
            activity.showProFeaturesDialog();
            return;
        }
        if (index == 1) {
            showCircleGestureShortcutDialog();
            return;
        }
        if (index == 2) {
            showDirectionalGestureDialog("<", GestureShortcutTrigger.swipeLeft());
            return;
        }
        if (index == 3) {
            showDirectionalGestureDialog(">", GestureShortcutTrigger.swipeRight());
            return;
        }
        if (index == 4) {
            showDirectionalGestureDialog("^", GestureShortcutTrigger.swipeUp());
            return;
        }
        if (index == 5) {
            showDirectionalGestureDialog("v", GestureShortcutTrigger.swipeDown());
            return;
        }
        if (index == 6) {
            showCustomGestureDialog();
        }
    }

    private void showDoubleTapShortcutDialog() {
        GestureShortcutAction[] actions = content.availableActions();
        new AlertDialog.Builder(activity)
                .setTitle(activity.viewerText.doubleTapPrefix())
                // interaction-surface: gesture-action-dialog
                .setItems(content.actionLabels(actions),
                        new GestureShortcutListeners.DoubleTapClickListener(this, actions))
                .setNegativeButton("OK", null)
                .show();
    }

    private void showCircleGestureShortcutDialog() {
        GestureShortcutAction[] actions = content.availableActions();
        new AlertDialog.Builder(activity)
                .setTitle(activity.viewerText.circleGesturePrefix())
                // interaction-surface: gesture-action-dialog
                .setItems(
                        content.actionLabels(actions), new GestureShortcutListeners.CircleClickListener(this, actions))
                .setNegativeButton("OK", null)
                .show();
    }

    private void showCustomGestureDialog() {
        CustomGestureMenu menu = content.customGestureMenu(hasCustomGestureShortcut());
        CustomGestureMenu.Action[] actions = menu.actions();
        new AlertDialog.Builder(activity)
                .setTitle(activity.viewerText.customGesturePrefix()
                        + content.actionLabel(content.action(GestureShortcutTrigger.customShape())))
                // interaction-surface: gesture-action-dialog
                .setItems(content.customGestureMenuLabels(actions),
                        new GestureShortcutListeners.CustomMenuClickListener(this, actions))
                .setNegativeButton("OK", null)
                .show();
    }

    private void showDirectionalGestureDialog(String title, GestureShortcutTrigger trigger) {
        GestureShortcutAction[] actions = content.availableActions();
        new AlertDialog.Builder(activity)
                .setTitle(title)
                // interaction-surface: gesture-action-dialog
                .setItems(content.actionLabels(actions),
                        new GestureShortcutListeners.DirectionalClickListener(this, trigger, actions))
                .setNegativeButton("OK", null)
                .show();
    }

    private void showCustomGestureActionDialog() {
        GestureShortcutAction[] actions = content.availableActions();
        new AlertDialog.Builder(activity)
                .setTitle(activity.viewerText.registerCustomGesture())
                // interaction-surface: gesture-action-dialog
                .setItems(content.actionLabels(actions),
                        new GestureShortcutListeners.CustomActionClickListener(this, actions))
                .setNegativeButton("OK", null)
                .show();
    }

    void showChangeCustomGestureActionDialog() {
        CustomGestureShortcut shortcut = activity.settingsStore.loadCustomGestureShortcut();
        if (shortcut == null) {
            return;
        }
        activity.pendingCustomGestureShape = shortcut.shape();
        GestureShortcutAction[] actions = content.availableActions();
        new AlertDialog.Builder(activity)
                .setTitle(activity.viewerText.changeCustomGestureAction())
                // interaction-surface: gesture-action-dialog
                .setItems(content.actionLabels(actions),
                        new GestureShortcutListeners.CustomActionClickListener(this, actions))
                .setNegativeButton("OK", null)
                .show();
    }

    void applyDoubleTapShortcut(GestureShortcutAction action) {
        applyGestureShortcut(GestureShortcutTrigger.doubleTap(), action);
        activity.updateLocalizedText();
        activity.refreshGestureShortcutsPanel();
    }

    void applyCircleGestureShortcut(GestureShortcutAction action) {
        applyGestureShortcut(GestureShortcutTrigger.circle(), action);
        activity.updateLocalizedText();
        activity.refreshGestureShortcutsPanel();
    }

    void applyDirectionalGestureShortcut(GestureShortcutTrigger trigger, GestureShortcutAction action) {
        applyGestureShortcut(trigger, action);
        activity.updateLocalizedText();
        activity.refreshGestureShortcutsPanel();
    }

    private void applyGestureShortcut(GestureShortcutTrigger trigger, GestureShortcutAction action) {
        GestureShortcutBinding binding = GestureShortcutBinding.of(trigger, action);
        activity.gestureShortcutBindings = activity.gestureShortcutBindings.put(binding);
        activity.settingsStore.saveGestureShortcutBinding(binding);
    }

    void startCustomGestureRegistration() {
        activity.closeMenu();
        activity.customGestureDrawingView =
                new CustomGestureDrawingView(activity, activity.viewerText.drawCustomGestureInstruction(),
                        activity.viewerText.cancel(), activity.systemTopInsetPx(), activity.backgroundColor(),
                        activity.primaryColor(), activity.textColor(), activity);
        activity.appRoot.addView(activity.customGestureDrawingView,
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        activity.registerCustomGestureBackCallback();
    }

    void clearCustomGestureShortcut() {
        activity.settingsStore.clearCustomGestureShortcut();
        activity.gestureShortcutBindings = activity.settingsStore.loadGestureShortcutBindings();
        activity.updateLocalizedText();
        activity.refreshGestureShortcutsPanel();
    }

    boolean cancelCustomGestureRegistration() {
        if (activity.customGestureDrawingView == null) {
            return false;
        }
        finishCustomGestureDrawing();
        activity.pendingCustomGestureShape = null;
        return true;
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
        activity.refreshGestureShortcutsPanel();
    }

    private void finishCustomGestureDrawing() {
        activity.unregisterCustomGestureBackCallback();
        if (activity.customGestureDrawingView != null) {
            activity.appRoot.removeView(activity.customGestureDrawingView);
            activity.customGestureDrawingView = null;
        }
    }
}
