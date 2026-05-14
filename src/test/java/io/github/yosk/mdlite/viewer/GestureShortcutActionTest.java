package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.domain.FeatureEntitlement;
import io.github.yosk.mdlite.testing.TestAssertions;

public final class GestureShortcutActionTest {
    public static void main(String[] args) {
        offActionCyclesToOpenMenuForProUsers();
        openFileActionCyclesToOpenMenuForProUsers();
        openMenuActionCyclesToNextThemeForProUsers();
        nextThemeActionCyclesToMoveControlsForProUsers();
        moveControlsActionCyclesToOffForProUsers();
        freeUsersAlwaysKeepGestureShortcutsOff();
        storedValueRestoresOpenMenuAction();
        unknownStoredValueRestoresOffAction();
        freeAvailableActionsContainOnlyOff();
        proAvailableActionsExposeMoveControlsAsDirectChoice();
    }

    private static void offActionCyclesToOpenMenuForProUsers() {
        GestureShortcutAction action = GestureShortcutAction.off().next(FeatureEntitlement.pro());

        TestAssertions.assertTrue(action.isOpenFile(), "Pro shortcut must cycle from Off to Open file");
    }

    private static void openFileActionCyclesToOpenMenuForProUsers() {
        GestureShortcutAction action = GestureShortcutAction.openFile().next(FeatureEntitlement.pro());

        TestAssertions.assertTrue(action.isOpenMenu(), "Pro shortcut must cycle from Open file to Open menu");
    }

    private static void openMenuActionCyclesToNextThemeForProUsers() {
        GestureShortcutAction action = GestureShortcutAction.openMenu().next(FeatureEntitlement.pro());

        TestAssertions.assertTrue(action.isNextTheme(), "Pro double-tap shortcut must cycle from Open menu to Next theme");
    }

    private static void nextThemeActionCyclesToMoveControlsForProUsers() {
        GestureShortcutAction action = GestureShortcutAction.nextTheme().next(FeatureEntitlement.pro());

        TestAssertions.assertTrue(action.isMoveControls(), "Pro double-tap shortcut must cycle from Next theme to Move controls");
    }

    private static void moveControlsActionCyclesToOffForProUsers() {
        GestureShortcutAction action = GestureShortcutAction.moveControls().next(FeatureEntitlement.pro());

        TestAssertions.assertTrue(action.isOff(), "Pro double-tap shortcut must cycle from Move controls to Off");
    }

    private static void freeUsersAlwaysKeepGestureShortcutsOff() {
        GestureShortcutAction action = GestureShortcutAction.openMenu().next(FeatureEntitlement.free());

        TestAssertions.assertTrue(action.isOff(), "Free entitlement must keep custom gesture shortcuts off");
    }

    private static void storedValueRestoresOpenMenuAction() {
        GestureShortcutAction action = GestureShortcutAction.fromStoredValue("open_menu");

        TestAssertions.assertTrue(action.isOpenMenu(), "stored open_menu value must restore the Open menu shortcut");
    }

    private static void unknownStoredValueRestoresOffAction() {
        GestureShortcutAction action = GestureShortcutAction.fromStoredValue("bad_value");

        TestAssertions.assertTrue(action.isOff(), "unknown stored gesture shortcut values must safely restore Off");
    }

    private static void freeAvailableActionsContainOnlyOff() {
        GestureShortcutAction[] actions = GestureShortcutAction.availableActions(FeatureEntitlement.free());

        TestAssertions.assertEquals(1, actions.length, "Free gesture shortcut picker must expose only Off");
        TestAssertions.assertTrue(actions[0].isOff(), "Free gesture shortcut picker must expose Off");
    }

    private static void proAvailableActionsExposeMoveControlsAsDirectChoice() {
        GestureShortcutAction[] actions = GestureShortcutAction.availableActions(FeatureEntitlement.pro());

        TestAssertions.assertEquals(5, actions.length, "Pro gesture shortcut picker must expose all actions");
        TestAssertions.assertTrue(actions[4].isMoveControls(), "Pro gesture shortcut picker must expose Move controls directly");
    }
}
