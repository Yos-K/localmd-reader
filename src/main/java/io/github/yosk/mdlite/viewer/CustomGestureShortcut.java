package io.github.yosk.mdlite.viewer;

import java.util.Optional;

public final class CustomGestureShortcut {
    private final CustomGestureShape shape;
    private final GestureShortcutAction action;

    private CustomGestureShortcut(CustomGestureShape shape, GestureShortcutAction action) {
        if (shape == null) {
            throw new IllegalArgumentException("custom gesture shape is required");
        }
        if (action == null || action.isOff()) {
            throw new IllegalArgumentException("custom gesture action is required");
        }
        this.shape = shape;
        this.action = action;
    }

    public static CustomGestureShortcut of(CustomGestureShape shape, GestureShortcutAction action) {
        return new CustomGestureShortcut(shape, action);
    }

    public static Optional<CustomGestureShortcut> restore(String shapeValue, String actionValue) {
        GestureShortcutAction action = GestureShortcutAction.fromStoredValue(actionValue);
        if (action.isOff()) {
            return Optional.empty();
        }
        try {
            return Optional.of(of(CustomGestureShape.fromStoredValue(shapeValue), action));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    public CustomGestureShape shape() {
        return shape;
    }

    public GestureShortcutAction action() {
        return action;
    }

    public GestureShortcutBinding binding() {
        return GestureShortcutBinding.of(GestureShortcutTrigger.customShape(), action);
    }
}
