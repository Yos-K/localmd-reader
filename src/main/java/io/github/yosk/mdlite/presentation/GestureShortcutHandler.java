package io.github.yosk.mdlite.presentation;

import android.view.MotionEvent;
import io.github.yosk.mdlite.domain.ViewerFeature;
import io.github.yosk.mdlite.viewer.CustomGestureShape;
import io.github.yosk.mdlite.viewer.CustomGestureShapeMatcher;
import io.github.yosk.mdlite.viewer.CustomGestureShortcut;
import io.github.yosk.mdlite.viewer.GestureShortcutAction;
import io.github.yosk.mdlite.viewer.GestureShortcutTrigger;

final class GestureShortcutHandler {
    private final MainActivity activity;

    GestureShortcutHandler(MainActivity activity) {
        this.activity = activity;
    }

    boolean handleViewerTouch(MotionEvent event) {
        activity.fontScaleGestureDetector.onTouchEvent(event);
        activity.shortcutGestureDetector.onTouchEvent(event);
        boolean circleHandled = handleCircleGestureTouch(event);
        return circleHandled || event.getPointerCount() > 1
                || activity.fontScaleGestureDetector.isInProgress();
    }

    boolean handleDoubleTapShortcut() {
        return executeShortcutAction(doubleTapShortcut(), ViewerFeature.DOUBLE_TAP_SHORTCUTS);
    }

    boolean executeShortcutAction(GestureShortcutAction action, ViewerFeature requiredFeature) {
        if (!activity.featureEntitlement.allows(requiredFeature)) {
            return false;
        }
        if (action.isOpenFile()) {
            activity.documentOpener.openMarkdownPicker();
            return true;
        }
        if (action.isOpenMenu()) {
            activity.openMenu();
            return true;
        }
        if (action.isPreviousTab()) {
            activity.documentTabSessionController.activatePrevious();
            return true;
        }
        if (action.isNextTab()) {
            activity.documentTabSessionController.activateNext();
            return true;
        }
        if (action.isNextTheme()) {
            activity.currentTheme = activity.currentTheme.next(activity.featureEntitlement);
            activity.settingsStore.saveViewerTheme(activity.currentTheme);
            activity.viewerPalette = ViewerPalette.from(activity.currentTheme);
            activity.updateLocalizedText();
            activity.applyNativeTheme();
            activity.rerenderMermaidDiagramsForCurrentTheme();
            activity.renderTabs();
            activity.renderCurrentDocument();
            return true;
        }
        if (action.isMoveControls()) {
            activity.controlsPlacement = activity.controlsPlacement.toggled();
            activity.settingsStore.saveControlsPlacement(activity.controlsPlacement);
            activity.updateLocalizedText();
            activity.applyControlsPlacement();
            return true;
        }
        if (action.isShowSearchBar()) {
            activity.showFindInDocumentBar();
            return true;
        }
        if (action.isNextHeading()) {
            if (!activity.featureEntitlement.allows(ViewerFeature.HEADING_JUMP)) {
                return false;
            }
            activity.jumpToNextHeading();
            return true;
        }
        if (action.isPreviousHeading()) {
            if (!activity.featureEntitlement.allows(ViewerFeature.HEADING_JUMP)) {
                return false;
            }
            activity.jumpToPreviousHeading();
            return true;
        }
        return false;
    }

    private boolean handleCircleGestureTouch(MotionEvent event) {
        if (!activity.featureEntitlement.allows(ViewerFeature.CUSTOM_GESTURE_SHORTCUTS)) {
            return false;
        }
        if (event.getPointerCount() > 1 || !activity.gestureShortcutBindings.hasShapeGestureShortcuts()) {
            activity.circleGestureTrace.reset();
            return false;
        }
        if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
            activity.circleGestureTrace.reset();
            activity.circleGestureTrace.append(event);
            return false;
        }
        if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
            activity.circleGestureTrace.append(event);
            return activity.circleGestureTrace.rememberIntentionalMovement(40f)
                    || directionalGestureLooksConfigured();
        }
        if (event.getActionMasked() == MotionEvent.ACTION_UP) {
            activity.circleGestureTrace.append(event);
            boolean handled = handleCustomGestureShortcut()
                    || handleDirectionalGestureShortcut()
                    || handleCircleGestureShortcut();
            boolean consumed = activity.circleGestureTrace.isConsuming() || handled;
            activity.circleGestureTrace.reset();
            return consumed;
        }
        if (event.getActionMasked() == MotionEvent.ACTION_CANCEL) {
            activity.circleGestureTrace.reset();
        }
        return false;
    }

    private boolean handleCustomGestureShortcut() {
        CustomGestureShortcut shortcut = activity.settingsStore.loadCustomGestureShortcut();
        if (shortcut == null) {
            return false;
        }
        try {
            CustomGestureShape inputShape = activity.circleGestureTrace.customGestureShape();
            boolean matches = CustomGestureShapeMatcher.forShape(shortcut.shape()).matches(inputShape);
            return matches && executeShortcutAction(shortcut.action(), ViewerFeature.CUSTOM_GESTURE_SHORTCUTS);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean handleCircleGestureShortcut() {
        return activity.circleGestureTrace.isCircleLike()
                && executeShortcutAction(circleGestureShortcut(), ViewerFeature.CUSTOM_GESTURE_SHORTCUTS);
    }

    private boolean handleDirectionalGestureShortcut() {
        GestureShortcutTrigger trigger = activity.circleGestureTrace.directionalGestureTrigger();
        if (trigger == null) {
            return false;
        }
        return executeShortcutAction(gestureAction(trigger), ViewerFeature.CUSTOM_GESTURE_SHORTCUTS);
    }

    private boolean directionalGestureLooksConfigured() {
        GestureShortcutTrigger trigger = activity.circleGestureTrace.directionalGestureTrigger();
        if (trigger == null) {
            return false;
        }
        return !gestureAction(trigger).isOff();
    }

    private GestureShortcutAction doubleTapShortcut() {
        return activity.gestureShortcutBindings.actionFor(GestureShortcutTrigger.doubleTap());
    }

    private GestureShortcutAction circleGestureShortcut() {
        return activity.gestureShortcutBindings.actionFor(GestureShortcutTrigger.circle());
    }

    private GestureShortcutAction gestureAction(GestureShortcutTrigger trigger) {
        return activity.gestureShortcutBindings.actionFor(trigger);
    }

}
