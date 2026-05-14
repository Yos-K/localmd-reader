package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.domain.FeatureEntitlement;
import io.github.yosk.mdlite.domain.ViewerFeature;

public final class GestureShortcutAction {
    private static final int OFF = 1;
    private static final int OPEN_FILE = 2;
    private static final int OPEN_MENU = 3;
    private static final int NEXT_THEME = 4;
    private static final int MOVE_CONTROLS = 5;

    private static final String OFF_VALUE = "off";
    private static final String OPEN_FILE_VALUE = "open_file";
    private static final String OPEN_MENU_VALUE = "open_menu";
    private static final String NEXT_THEME_VALUE = "next_theme";
    private static final String MOVE_CONTROLS_VALUE = "move_controls";

    private final int value;

    private GestureShortcutAction(int value) {
        this.value = value;
    }

    public static GestureShortcutAction off() {
        return new GestureShortcutAction(OFF);
    }

    public static GestureShortcutAction openMenu() {
        return new GestureShortcutAction(OPEN_MENU);
    }

    public static GestureShortcutAction openFile() {
        return new GestureShortcutAction(OPEN_FILE);
    }

    public static GestureShortcutAction nextTheme() {
        return new GestureShortcutAction(NEXT_THEME);
    }

    public static GestureShortcutAction moveControls() {
        return new GestureShortcutAction(MOVE_CONTROLS);
    }

    public static GestureShortcutAction fromStoredValue(String storedValue) {
        if (OPEN_MENU_VALUE.equals(storedValue)) {
            return openMenu();
        }
        if (OPEN_FILE_VALUE.equals(storedValue)) {
            return openFile();
        }
        if (NEXT_THEME_VALUE.equals(storedValue)) {
            return nextTheme();
        }
        if (MOVE_CONTROLS_VALUE.equals(storedValue)) {
            return moveControls();
        }
        return off();
    }

    public static GestureShortcutAction[] availableActions(FeatureEntitlement entitlement) {
        FeatureEntitlement safeEntitlement = entitlement == null ? FeatureEntitlement.free() : entitlement;
        if (!safeEntitlement.allows(ViewerFeature.CUSTOM_GESTURE_SHORTCUTS)) {
            return new GestureShortcutAction[] { off() };
        }
        return new GestureShortcutAction[] { off(), openFile(), openMenu(), nextTheme(), moveControls() };
    }

    public boolean isOff() {
        return value == OFF;
    }

    public boolean isOpenMenu() {
        return value == OPEN_MENU;
    }

    public boolean isOpenFile() {
        return value == OPEN_FILE;
    }

    public boolean isNextTheme() {
        return value == NEXT_THEME;
    }

    public boolean isMoveControls() {
        return value == MOVE_CONTROLS;
    }

    public String storedValue() {
        if (isOpenMenu()) {
            return OPEN_MENU_VALUE;
        }
        if (isOpenFile()) {
            return OPEN_FILE_VALUE;
        }
        if (isNextTheme()) {
            return NEXT_THEME_VALUE;
        }
        if (isMoveControls()) {
            return MOVE_CONTROLS_VALUE;
        }
        return OFF_VALUE;
    }

    public GestureShortcutAction next(FeatureEntitlement entitlement) {
        FeatureEntitlement safeEntitlement = entitlement == null ? FeatureEntitlement.free() : entitlement;
        if (!safeEntitlement.allows(ViewerFeature.CUSTOM_GESTURE_SHORTCUTS)) {
            return off();
        }
        if (isOff()) {
            return openFile();
        }
        if (isOpenFile()) {
            return openMenu();
        }
        if (isOpenMenu()) {
            return nextTheme();
        }
        if (isNextTheme()) {
            return moveControls();
        }
        return off();
    }
}
