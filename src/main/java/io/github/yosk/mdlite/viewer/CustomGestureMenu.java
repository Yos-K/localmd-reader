package io.github.yosk.mdlite.viewer;

public final class CustomGestureMenu {
    public enum Action {
        REGISTER,
        CHANGE_ACTION,
        CLEAR
    }

    private final Action[] actions;

    private CustomGestureMenu(Action[] actions) {
        this.actions = actions;
    }

    public static CustomGestureMenu unregistered() {
        return new CustomGestureMenu(new Action[] { Action.REGISTER });
    }

    public static CustomGestureMenu registered() {
        return new CustomGestureMenu(new Action[] {
            Action.REGISTER,
            Action.CHANGE_ACTION,
            Action.CLEAR
        });
    }

    public Action[] actions() {
        return actions.clone();
    }
}
