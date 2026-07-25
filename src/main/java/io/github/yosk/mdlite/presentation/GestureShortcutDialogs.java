package io.github.yosk.mdlite.presentation;

import android.graphics.Typeface;
import android.view.View;
import android.widget.Button;
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
            activity.gestureShortcutPanel.showActions(6);
            activity.openMenu();
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
        if (index != 0 && !activity.featureEntitlement.allows(ViewerFeature.CUSTOM_GESTURE_SHORTCUTS)) {
            activity.showProFeaturesDialog();
            return;
        }
        activity.gestureShortcutPanel.showActions(index);
    }

    View gestureShortcutActionView(final int targetIndex, final Runnable back) {
        LinearLayout list = new LinearLayout(activity);
        list.setOrientation(LinearLayout.VERTICAL);
        list.addView(actionButton("< " + activity.viewerText.cancel(), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back.run();
            }
        }));
        if (targetIndex == 6 && !hasCustomGestureShortcut() && activity.pendingCustomGestureShape == null) {
            list.addView(actionButton(activity.viewerText.registerCustomGesture(), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startCustomGestureRegistration();
                }
            }));
            return list;
        }
        GestureShortcutAction[] actions = content.availableActions();
        String[] labels = content.actionLabels(actions);
        for (int index = 0; index < actions.length; index++) {
            final GestureShortcutAction action = actions[index];
            list.addView(actionButton(labels[index], new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    applySelectedAction(targetIndex, action);
                    back.run();
                }
            }));
        }
        if (targetIndex == 6) {
            list.addView(actionButton(activity.viewerText.clearCustomGesture(), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clearCustomGestureShortcut();
                    back.run();
                }
            }));
        }
        return list;
    }

    private Button actionButton(String label, View.OnClickListener listener) {
        Button button = new Button(activity);
        button.setText(label);
        button.setAllCaps(false);
        button.setTextColor(activity.textColor());
        button.setTextSize(14);
        button.setTypeface(Typeface.DEFAULT);
        button.setBackground(activity.makeTonalBackground(activity.surfaceAltColor(), 8));
        button.setOnClickListener(listener);
        return button;
    }

    private void applySelectedAction(int targetIndex, GestureShortcutAction action) {
        if (targetIndex == 6) {
            if (activity.pendingCustomGestureShape == null) {
                CustomGestureShortcut shortcut = activity.settingsStore.loadCustomGestureShortcut();
                activity.pendingCustomGestureShape = shortcut.shape();
            }
            saveCustomGestureShortcut(action);
            return;
        }
        applyGestureShortcut(triggerAt(targetIndex), action);
        activity.updateLocalizedText();
        activity.refreshGestureShortcutsPanel();
    }

    private GestureShortcutTrigger triggerAt(int targetIndex) {
        GestureShortcutTrigger[] triggers = new GestureShortcutTrigger[] {GestureShortcutTrigger.doubleTap(),
                GestureShortcutTrigger.circle(), GestureShortcutTrigger.swipeLeft(), GestureShortcutTrigger.swipeRight(),
                GestureShortcutTrigger.swipeUp(), GestureShortcutTrigger.swipeDown()};
        return triggers[targetIndex];
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
