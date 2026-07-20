package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.domain.FeatureEntitlement;
import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class GestureShortcutActionTest {

    @Test
    void offActionCyclesToOpenMenuForProUsers() {
        GestureShortcutAction action = GestureShortcutAction.off().next(FeatureEntitlement.pro());

        TestAssertions.assertTrue(action.isOpenFile(), "Pro shortcut must cycle from Off to Open file");
    }

    @Test
    void openFileActionCyclesToOpenMenuForProUsers() {
        GestureShortcutAction action = GestureShortcutAction.openFile().next(FeatureEntitlement.pro());

        TestAssertions.assertTrue(action.isOpenMenu(), "Pro shortcut must cycle from Open file to Open menu");
    }

    @Test
    void openMenuActionCyclesToPreviousTabForProUsers() {
        GestureShortcutAction action = GestureShortcutAction.openMenu().next(FeatureEntitlement.pro());

        TestAssertions.assertTrue(action.isPreviousTab(), "Pro shortcut must cycle from Open menu to Previous tab");
    }

    @Test
    void previousTabActionCyclesToNextTabForProUsers() {
        GestureShortcutAction action = GestureShortcutAction.previousTab().next(FeatureEntitlement.pro());

        TestAssertions.assertTrue(action.isNextTab(), "Pro shortcut must cycle from Previous tab to Next tab");
    }

    @Test
    void nextTabActionCyclesToNextThemeForProUsers() {
        GestureShortcutAction action = GestureShortcutAction.nextTab().next(FeatureEntitlement.pro());

        TestAssertions.assertTrue(action.isNextTheme(), "Pro shortcut must cycle from Next tab to Next theme");
    }

    @Test
    void nextThemeActionCyclesToMoveControlsForProUsers() {
        GestureShortcutAction action = GestureShortcutAction.nextTheme().next(FeatureEntitlement.pro());

        TestAssertions.assertTrue(action.isMoveControls(), "Pro double-tap shortcut must cycle from Next theme to Move controls");
    }

    @Test
    void moveControlsActionCyclesToOffForProUsers() {
        GestureShortcutAction action = GestureShortcutAction.moveControls().next(FeatureEntitlement.pro());

        TestAssertions.assertTrue(action.isShowSearchBar(), "Pro double-tap shortcut must cycle from Move controls to Show search");
    }

    @Test
    void showSearchBarActionCyclesToNextHeadingForProUsers() {
        GestureShortcutAction action = GestureShortcutAction.showSearchBar().next(FeatureEntitlement.pro());

        TestAssertions.assertTrue(action.isNextHeading(), "Pro double-tap shortcut must cycle from Show search to Next heading");
    }

    @Test
    void nextHeadingActionCyclesToPreviousHeadingForProUsers() {
        GestureShortcutAction action = GestureShortcutAction.nextHeading().next(FeatureEntitlement.pro());

        TestAssertions.assertTrue(action.isPreviousHeading(), "Pro double-tap shortcut must cycle from Next heading to Previous heading");
    }

    @Test
    void previousHeadingActionCyclesToOffForProUsers() {
        GestureShortcutAction action = GestureShortcutAction.previousHeading().next(FeatureEntitlement.pro());

        TestAssertions.assertTrue(action.isOff(), "Pro double-tap shortcut must cycle from Previous heading to Off");
    }

    @Test
    void showSearchBarActionCyclesToOffForFreeUsers() {
        GestureShortcutAction action = GestureShortcutAction.showSearchBar().next(FeatureEntitlement.free());

        TestAssertions.assertTrue(action.isOff(), "Free double-tap shortcut must skip Pro-only heading jumps after Show search");
    }

    @Test
    void freeUsersCanCycleDoubleTapShortcutActions() {
        GestureShortcutAction action = GestureShortcutAction.openMenu().next(FeatureEntitlement.free());

        TestAssertions.assertTrue(action.isPreviousTab(), "Free entitlement must allow double-tap shortcut action cycling");
    }

    @Test
    void storedValueRestoresOpenMenuAction() {
        GestureShortcutAction action = GestureShortcutAction.fromStoredValue("open_menu");

        TestAssertions.assertTrue(action.isOpenMenu(), "stored open_menu value must restore the Open menu shortcut");
    }

    @Test
    void storedValueRestoresNextTabAction() {
        GestureShortcutAction action = GestureShortcutAction.fromStoredValue("next_tab");

        TestAssertions.assertTrue(action.isNextTab(), "stored next_tab value must restore the Next tab shortcut");
    }

    @Test
    void storedValueRestoresShowSearchBarAction() {
        GestureShortcutAction action = GestureShortcutAction.fromStoredValue("show_search_bar");

        TestAssertions.assertTrue(action.isShowSearchBar(), "stored show_search_bar value must restore the Show search shortcut");
    }

    @Test
    void storedValueRestoresNextHeadingAction() {
        GestureShortcutAction action = GestureShortcutAction.fromStoredValue("next_heading");

        TestAssertions.assertTrue(action.isNextHeading(), "stored next_heading value must restore the Next heading shortcut");
    }

    @Test
    void storedValueRestoresPreviousHeadingAction() {
        GestureShortcutAction action = GestureShortcutAction.fromStoredValue("previous_heading");

        TestAssertions.assertTrue(action.isPreviousHeading(), "stored previous_heading value must restore the Previous heading shortcut");
    }

    @Test
    void unknownStoredValueRestoresOffAction() {
        GestureShortcutAction action = GestureShortcutAction.fromStoredValue("bad_value");

        TestAssertions.assertTrue(action.isOff(), "unknown stored gesture shortcut values must safely restore Off");
    }

    @Test
    void freeAvailableActionsExposeDoubleTapChoices() {
        GestureShortcutAction[] actions = GestureShortcutAction.availableActions(FeatureEntitlement.free());

        TestAssertions.assertEquals(8, actions.length, "Free double-tap shortcut picker must expose only Free-safe actions");
        TestAssertions.assertTrue(actions[7].isShowSearchBar(), "Free double-tap shortcut picker must expose Show search directly");
    }

    @Test
    void proAvailableActionsExposeMoveControlsAsDirectChoice() {
        GestureShortcutAction[] actions = GestureShortcutAction.availableActions(FeatureEntitlement.pro());

        TestAssertions.assertEquals(10, actions.length, "Pro gesture shortcut picker must expose all actions");
        TestAssertions.assertTrue(actions[7].isShowSearchBar(), "Pro gesture shortcut picker must expose Show search directly");
        TestAssertions.assertTrue(actions[8].isNextHeading(), "Pro gesture shortcut picker must expose Next heading directly");
        TestAssertions.assertTrue(actions[9].isPreviousHeading(), "Pro gesture shortcut picker must expose Previous heading directly");
    }
}
