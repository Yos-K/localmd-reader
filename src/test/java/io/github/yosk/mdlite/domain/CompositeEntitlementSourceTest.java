package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class CompositeEntitlementSourceTest {

    @Test
    void allFreeSourcesReturnFreeEntitlement() {
        EntitlementSource source = CompositeEntitlementSource.anyPro(
                StaticEntitlementSource.free(),
                StaticEntitlementSource.free());

        TestAssertions.assertFalse(source.currentEntitlement().isPro(), "All Free sources must return Free entitlement");
    }

    @Test
    void firstProSourceReturnsProEntitlement() {
        EntitlementSource source = CompositeEntitlementSource.anyPro(
                StaticEntitlementSource.pro(),
                StaticEntitlementSource.free());

        TestAssertions.assertTrue(source.currentEntitlement().isPro(), "First Pro source must return Pro entitlement");
    }

    @Test
    void laterProSourceReturnsProEntitlement() {
        EntitlementSource source = CompositeEntitlementSource.anyPro(
                StaticEntitlementSource.free(),
                StaticEntitlementSource.pro());

        TestAssertions.assertTrue(source.currentEntitlement().isPro(), "Later Pro source must return Pro entitlement");
    }

    @Test
    void missingSourceEntriesAreIgnored() {
        EntitlementSource source = CompositeEntitlementSource.anyPro(
                null,
                StaticEntitlementSource.pro());

        TestAssertions.assertTrue(source.currentEntitlement().isPro(), "Missing source entries must not hide a valid Pro source");
    }

    @Test
    void missingSourceArrayReturnsFreeEntitlement() {
        EntitlementSource source = CompositeEntitlementSource.anyPro((EntitlementSource[]) null);

        TestAssertions.assertFalse(source.currentEntitlement().isPro(), "Missing source array must return Free entitlement");
    }
}
