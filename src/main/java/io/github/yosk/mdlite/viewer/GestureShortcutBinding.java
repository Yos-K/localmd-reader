package io.github.yosk.mdlite.viewer;

public final class GestureShortcutBinding {
    private final GestureShortcutTrigger trigger;
    private final GestureShortcutAction action;

    private GestureShortcutBinding(GestureShortcutTrigger trigger, GestureShortcutAction action) {
        if (trigger == null) {
            throw new IllegalArgumentException("gesture shortcut trigger is required");
        }
        if (action == null) {
            throw new IllegalArgumentException("gesture shortcut action is required");
        }
        this.trigger = trigger;
        this.action = action;
    }

    public static GestureShortcutBinding of(GestureShortcutTrigger trigger, GestureShortcutAction action) {
        return new GestureShortcutBinding(trigger, action);
    }

    public GestureShortcutTrigger trigger() {
        return trigger;
    }

    public GestureShortcutAction action() {
        return action;
    }
}
