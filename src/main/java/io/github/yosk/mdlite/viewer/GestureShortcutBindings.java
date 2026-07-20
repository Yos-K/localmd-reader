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
        ArrayList<GestureShortcutBinding> next = withoutTrigger(binding.trigger());
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

    private ArrayList<GestureShortcutBinding> withoutTrigger(GestureShortcutTrigger trigger) {
        ArrayList<GestureShortcutBinding> next = new ArrayList<GestureShortcutBinding>();
        for (int i = 0; i < items.size(); i++) {
            GestureShortcutBinding item = items.get(i);
            if (!trigger.equals(item.trigger())) {
                next.add(item);
            }
        }
        return next;
    }
}
