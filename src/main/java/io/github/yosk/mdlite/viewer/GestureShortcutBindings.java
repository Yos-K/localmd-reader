package io.github.yosk.mdlite.viewer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class GestureShortcutBindings {
    private final List<GestureShortcutBinding> items;

    private GestureShortcutBindings(List<GestureShortcutBinding> items) {
        this.items = Collections.unmodifiableList(new ArrayList<GestureShortcutBinding>(items));
    }

    public static GestureShortcutBindings empty() {
        return new GestureShortcutBindings(new ArrayList<GestureShortcutBinding>());
    }

    public GestureShortcutBindings put(GestureShortcutBinding binding) {
        if (binding == null) {
            throw new IllegalArgumentException("gesture shortcut binding is required");
        }
        ArrayList<GestureShortcutBinding> next = withoutTriggerAndActiveAction(binding);
        next.add(binding);
        return new GestureShortcutBindings(next);
    }

    public GestureShortcutAction actionFor(GestureShortcutTrigger trigger) {
        if (trigger == null) {
            return GestureShortcutAction.off();
        }
        for (int i = 0; i < items.size(); i++) {
            GestureShortcutBinding item = items.get(i);
            if (trigger.equals(item.trigger())) {
                return item.action();
            }
        }
        return GestureShortcutAction.off();
    }

    public boolean hasShapeGestureShortcuts() {
        return !actionFor(GestureShortcutTrigger.circle()).isOff()
                || !actionFor(GestureShortcutTrigger.customShape()).isOff()
                || !actionFor(GestureShortcutTrigger.swipeLeft()).isOff()
                || !actionFor(GestureShortcutTrigger.swipeRight()).isOff()
                || !actionFor(GestureShortcutTrigger.swipeUp()).isOff()
                || !actionFor(GestureShortcutTrigger.swipeDown()).isOff();
    }

    public List<GestureShortcutBinding> items() {
        return items;
    }

    public GestureShortcutTrigger triggerFor(GestureShortcutAction action) {
        if (action == null || action.isOff()) {
            return null;
        }
        for (int i = 0; i < items.size(); i++) {
            GestureShortcutBinding item = items.get(i);
            if (action.equals(item.action())) {
                return item.trigger();
            }
        }
        return null;
    }

    private ArrayList<GestureShortcutBinding> withoutTriggerAndActiveAction(
            GestureShortcutBinding binding) {
        ArrayList<GestureShortcutBinding> next = new ArrayList<GestureShortcutBinding>();
        for (int i = 0; i < items.size(); i++) {
            GestureShortcutBinding item = items.get(i);
            if (!item.trigger().equals(binding.trigger())
                    && (binding.action().isOff() || !item.action().equals(binding.action()))) {
                next.add(item);
            }
        }
        return next;
    }
}
