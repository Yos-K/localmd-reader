package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.domain.FeatureEntitlement;
import io.github.yosk.mdlite.domain.ViewerFeature;

public final class GestureShortcutAction {
    private static final int OFF = 1;
    private static final int OPEN_FILE = 2;
    private static final int OPEN_MENU = 3;
    private static final int NEXT_THEME = 4;
    private static final int MOVE_CONTROLS = 5;
    private static final int PREVIOUS_TAB = 6;
    private static final int NEXT_TAB = 7;
    private static final int SHOW_SEARCH_BAR = 8;
    private static final int NEXT_HEADING = 9;
    private static final int PREVIOUS_HEADING = 10;

    private static final String OFF_VALUE = "off";
    private static final String OPEN_FILE_VALUE = "open_file";
    private static final String OPEN_MENU_VALUE = "open_menu";
    private static final String NEXT_THEME_VALUE = "next_theme";
    private static final String MOVE_CONTROLS_VALUE = "move_controls";
    private static final String PREVIOUS_TAB_VALUE = "previous_tab";
    private static final String NEXT_TAB_VALUE = "next_tab";
    private static final String SHOW_SEARCH_BAR_VALUE = "show_search_bar";
    private static final String NEXT_HEADING_VALUE = "next_heading";
    private static final String PREVIOUS_HEADING_VALUE = "previous_heading";

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

    public static GestureShortcutAction previousTab() {
        return new GestureShortcutAction(PREVIOUS_TAB);
    }

    public static GestureShortcutAction nextTab() {
        return new GestureShortcutAction(NEXT_TAB);
    }

    public static GestureShortcutAction showSearchBar() {
        return new GestureShortcutAction(SHOW_SEARCH_BAR);
    }

    public static GestureShortcutAction nextHeading() {
        return new GestureShortcutAction(NEXT_HEADING);
    }

    public static GestureShortcutAction previousHeading() {
        return new GestureShortcutAction(PREVIOUS_HEADING);
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
        if (PREVIOUS_TAB_VALUE.equals(storedValue)) {
            return previousTab();
        }
        if (NEXT_TAB_VALUE.equals(storedValue)) {
            return nextTab();
        }
        if (SHOW_SEARCH_BAR_VALUE.equals(storedValue)) {
            return showSearchBar();
        }
        if (NEXT_HEADING_VALUE.equals(storedValue)) {
            return nextHeading();
        }
        if (PREVIOUS_HEADING_VALUE.equals(storedValue)) {
            return previousHeading();
        }
        return off();
    }

    public static GestureShortcutAction[] availableActions(FeatureEntitlement entitlement) {
        FeatureEntitlement safeEntitlement = entitlement == null ? FeatureEntitlement.free() : entitlement;
        if (!safeEntitlement.allows(ViewerFeature.DOUBLE_TAP_SHORTCUTS)) {
            return new GestureShortcutAction[] { off() };
        }
        if (!safeEntitlement.allows(ViewerFeature.HEADING_JUMP)) {
            return new GestureShortcutAction[] {
                off(), openFile(), openMenu(), previousTab(), nextTab(), nextTheme(), moveControls(),
                showSearchBar()
            };
        }
        return new GestureShortcutAction[] {
            off(), openFile(), openMenu(), previousTab(), nextTab(), nextTheme(), moveControls(),
            showSearchBar(), nextHeading(), previousHeading()
        };
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

    public boolean isPreviousTab() {
        return value == PREVIOUS_TAB;
    }

    public boolean isNextTab() {
        return value == NEXT_TAB;
    }

    public boolean isShowSearchBar() {
        return value == SHOW_SEARCH_BAR;
    }

    public boolean isNextHeading() {
        return value == NEXT_HEADING;
    }

    public boolean isPreviousHeading() {
        return value == PREVIOUS_HEADING;
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
        if (isPreviousTab()) {
            return PREVIOUS_TAB_VALUE;
        }
        if (isNextTab()) {
            return NEXT_TAB_VALUE;
        }
        if (isShowSearchBar()) {
            return SHOW_SEARCH_BAR_VALUE;
        }
        if (isNextHeading()) {
            return NEXT_HEADING_VALUE;
        }
        if (isPreviousHeading()) {
            return PREVIOUS_HEADING_VALUE;
        }
        return OFF_VALUE;
    }

    public GestureShortcutAction next(FeatureEntitlement entitlement) {
        FeatureEntitlement safeEntitlement = entitlement == null ? FeatureEntitlement.free() : entitlement;
        if (!safeEntitlement.allows(ViewerFeature.DOUBLE_TAP_SHORTCUTS)) {
            return off();
        }
        if (isOff()) {
            return openFile();
        }
        if (isOpenFile()) {
            return openMenu();
        }
        if (isOpenMenu()) {
            return previousTab();
        }
        if (isPreviousTab()) {
            return nextTab();
        }
        if (isNextTab()) {
            return nextTheme();
        }
        if (isNextTheme()) {
            return moveControls();
        }
        if (isMoveControls()) {
            return showSearchBar();
        }
        if (isShowSearchBar()) {
            if (!safeEntitlement.allows(ViewerFeature.HEADING_JUMP)) {
                return off();
            }
            return nextHeading();
        }
        if (isNextHeading()) {
            return previousHeading();
        }
        return off();
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof GestureShortcutAction
                && value == ((GestureShortcutAction) other).value;
    }

    @Override
    public int hashCode() {
        return value;
    }
}
