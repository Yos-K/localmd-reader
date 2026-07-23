package io.github.yosk.mdlite.viewer;

public final class GestureShortcutActionLabels {
    private GestureShortcutActionLabels() {
    }

    public static String[] from(
            GestureShortcutAction[] actions,
            GestureShortcutBindings bindings,
            ViewerText text) {
        GestureShortcutAction[] safeActions = actions == null
                ? new GestureShortcutAction[0] : actions;
        GestureShortcutBindings safeBindings = bindings == null
                ? GestureShortcutBindings.empty() : bindings;
        ViewerText safeText = text == null
                ? ViewerText.fromLanguage(ViewerLanguage.english()) : text;
        String[] labels = new String[safeActions.length];
        for (int i = 0; i < safeActions.length; i++) {
            labels[i] = label(safeActions[i], safeBindings, safeText);
        }
        return labels;
    }

    private static String label(
            GestureShortcutAction action,
            GestureShortcutBindings bindings,
            ViewerText text) {
        GestureShortcutAction safeAction = action == null
                ? GestureShortcutAction.off() : action;
        String label = actionLabel(safeAction, text);
        GestureShortcutTrigger trigger = bindings.triggerFor(safeAction);
        return trigger == null ? label
                : label + text.assignedGestureSuffix(gestureLabel(trigger, text));
    }

    private static String gestureLabel(GestureShortcutTrigger trigger, ViewerText text) {
        if (trigger.isCircle()) { return text.circleGesture(); }
        if (trigger.isCustomShape()) { return text.customGesture(); }
        if (trigger.isSwipeLeft()) { return text.swipeLeftGesture(); }
        if (trigger.isSwipeRight()) { return text.swipeRightGesture(); }
        if (trigger.isSwipeUp()) { return text.swipeUpGesture(); }
        if (trigger.isSwipeDown()) { return text.swipeDownGesture(); }
        return text.doubleTapGesture();
    }

    private static String actionLabel(GestureShortcutAction action, ViewerText text) {
        if (action.isOpenFile()) { return text.openFile(); }
        if (action.isOpenMenu()) { return text.openMenu(); }
        if (action.isPreviousTab()) { return text.previousTabAction(); }
        if (action.isNextTab()) { return text.nextTabAction(); }
        if (action.isNextTheme()) { return text.nextThemeAction(); }
        if (action.isMoveControls()) { return text.moveControlsAction(); }
        if (action.isShowSearchBar()) { return text.showSearchBarAction(); }
        if (action.isNextHeading()) { return text.nextHeadingAction(); }
        if (action.isPreviousHeading()) { return text.previousHeadingAction(); }
        return text.off();
    }
}
