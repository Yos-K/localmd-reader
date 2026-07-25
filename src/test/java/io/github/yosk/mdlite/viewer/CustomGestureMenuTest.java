package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class CustomGestureMenuTest {
    @Test
    void unregisteredGestureOffersRegistrationWithoutAFalseDeleteAction() {
        CustomGestureMenu menu = CustomGestureMenu.unregistered();

        TestAssertions.assertEquals(1, menu.actions().length,
                "an absent custom gesture must expose only its valid registration transition");
        TestAssertions.assertEquals("REGISTER", menu.actions()[0].name(),
                "registration must be the available transition for an absent custom gesture");
    }

    @Test
    void registeredGestureOffersEveryValidManagementAction() {
        CustomGestureMenu menu = CustomGestureMenu.registered();

        TestAssertions.assertEquals(3, menu.actions().length,
                "a registered custom gesture must expose replace, action change, and clear transitions");
        TestAssertions.assertEquals("REGISTER", menu.actions()[0].name(),
                "a registered custom gesture can be replaced");
        TestAssertions.assertEquals("CHANGE_ACTION", menu.actions()[1].name(),
                "a registered custom gesture can change its action");
        TestAssertions.assertEquals("CLEAR", menu.actions()[2].name(),
                "a registered custom gesture can be cleared");
    }
}
