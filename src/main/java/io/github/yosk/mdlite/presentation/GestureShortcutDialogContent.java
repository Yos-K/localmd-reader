package io.github.yosk.mdlite.presentation;

import io.github.yosk.mdlite.domain.ViewerFeature;
import io.github.yosk.mdlite.viewer.CustomGestureMenu;
import io.github.yosk.mdlite.viewer.GestureShortcutAction;
import io.github.yosk.mdlite.viewer.GestureShortcutActionLabels;
import io.github.yosk.mdlite.viewer.GestureShortcutTrigger;

final class GestureShortcutDialogContent {
    private final MainActivity activity;

    GestureShortcutDialogContent(MainActivity activity) {
        this.activity = activity;
    }

    boolean targetAvailable(int targetIndex) {
        return targetIndex == 0 ? activity.featureEntitlement.allows(ViewerFeature.DOUBLE_TAP_SHORTCUTS)
                                : activity.featureEntitlement.allows(ViewerFeature.CUSTOM_GESTURE_SHORTCUTS);
    }

    String shortcutActionLabel(int targetIndex, GestureShortcutAction action) {
        return targetAvailable(targetIndex) ? actionLabel(action) : activity.viewerText.proOnly();
    }

    String actionLabel(GestureShortcutAction action) {
        if (action.isOpenFile())
            return activity.viewerText.openFile();
        if (action.isOpenMenu())
            return activity.viewerText.openMenu();
        if (action.isPreviousTab())
            return activity.viewerText.previousTabAction();
        if (action.isNextTab())
            return activity.viewerText.nextTabAction();
        if (action.isNextTheme())
            return activity.viewerText.nextThemeAction();
        if (action.isMoveControls())
            return activity.viewerText.moveControlsAction();
        if (action.isShowSearchBar())
            return activity.viewerText.showSearchBarAction();
        if (action.isNextHeading())
            return activity.viewerText.nextHeadingAction();
        if (action.isPreviousHeading())
            return activity.viewerText.previousHeadingAction();
        return activity.viewerText.off();
    }

    CustomGestureMenu customGestureMenu(boolean registered) {
        return registered ? CustomGestureMenu.registered() : CustomGestureMenu.unregistered();
    }

    String[] customGestureMenuLabels(CustomGestureMenu.Action[] actions) {
        String[] labels = new String[actions.length];
        for (int index = 0; index < actions.length; index++) {
            if (actions[index] == CustomGestureMenu.Action.REGISTER) {
                labels[index] = activity.viewerText.registerCustomGesture();
            } else if (actions[index] == CustomGestureMenu.Action.CHANGE_ACTION) {
                labels[index] = activity.viewerText.changeCustomGestureAction();
            } else {
                labels[index] = activity.viewerText.clearCustomGesture();
            }
        }
        return labels;
    }

    GestureShortcutAction[] availableActions() {
        return GestureShortcutAction.availableActions(activity.featureEntitlement);
    }

    String[] actionLabels(GestureShortcutAction[] actions) {
        return GestureShortcutActionLabels.from(actions, activity.gestureShortcutBindings, activity.viewerText);
    }

    GestureShortcutAction action(GestureShortcutTrigger trigger) {
        return activity.gestureShortcutBindings.actionFor(trigger);
    }
}
