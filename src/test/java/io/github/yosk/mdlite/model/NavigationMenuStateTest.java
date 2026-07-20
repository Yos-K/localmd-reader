package io.github.yosk.mdlite.model;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class NavigationMenuStateTest {
    @Test
    void closedMenuOpensAsAnOpenMenu() {
        NavigationMenuState next = NavigationMenuState.closed().open();

        TestAssertions.assertTrue(next instanceof NavigationMenuState.Open,
                "opening a closed menu must produce the open state");
    }

    @Test
    void openMenuClosesAsAClosedMenu() {
        NavigationMenuState next = NavigationMenuState.opened().close();

        TestAssertions.assertTrue(next instanceof NavigationMenuState.Closed,
                "closing an open menu must produce the closed state");
    }

    @Test
    void openingAnOpenMenuIsIdempotent() {
        NavigationMenuState.Open open = NavigationMenuState.opened();

        TestAssertions.assertSame(open, open.open(),
                "repeated open commands must preserve the valid open state");
    }

    @Test
    void closingAClosedMenuIsIdempotent() {
        NavigationMenuState.Closed closed = NavigationMenuState.closed();

        TestAssertions.assertSame(closed, closed.close(),
                "repeated close commands must preserve the valid closed state");
    }
}
