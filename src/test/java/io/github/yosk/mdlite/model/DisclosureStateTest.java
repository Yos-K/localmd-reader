package io.github.yosk.mdlite.model;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class DisclosureStateTest {
    @Test
    void togglingACollapsedSectionProducesAnExpandedSection() {
        DisclosureState next = DisclosureState.collapsed().toggled();

        TestAssertions.assertTrue(next instanceof DisclosureState.Expanded,
                "toggling a collapsed section must expose its content");
    }

    @Test
    void togglingAnExpandedSectionProducesACollapsedSection() {
        DisclosureState next = DisclosureState.expanded().toggled();

        TestAssertions.assertTrue(next instanceof DisclosureState.Collapsed,
                "toggling an expanded section must hide its content");
    }

    @Test
    void togglingTwiceReturnsToTheOriginalCollapsedState() {
        DisclosureState.Collapsed collapsed = DisclosureState.collapsed();

        TestAssertions.assertSame(collapsed, collapsed.toggled().toggled(),
                "two toggles must preserve the original valid disclosure state");
    }
}
