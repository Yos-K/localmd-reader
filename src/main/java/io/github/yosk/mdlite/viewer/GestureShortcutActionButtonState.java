package io.github.yosk.mdlite.viewer;

public final class GestureShortcutActionButtonState {
    private static final GestureShortcutActionButtonState AVAILABLE = new GestureShortcutActionButtonState(false);
    private static final GestureShortcutActionButtonState ASSIGNED_ELSEWHERE =
            new GestureShortcutActionButtonState(true);

    private final boolean muted;

    private GestureShortcutActionButtonState(boolean muted) {
        this.muted = muted;
    }

    public static GestureShortcutActionButtonState forSelection(GestureShortcutBindings bindings,
            GestureShortcutTrigger selectedTrigger, GestureShortcutAction action) {
        if (bindings == null || selectedTrigger == null || action == null) {
            return AVAILABLE;
        }
        GestureShortcutTrigger owner = bindings.triggerFor(action);
        return owner != null && !owner.equals(selectedTrigger) ? ASSIGNED_ELSEWHERE : AVAILABLE;
    }

    public boolean isMuted() {
        return muted;
    }
}
