package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class TableReadingModeTest {

    @Test
    void freeEntitlementUsesStandardTableReading() {
        TableReadingMode mode = TableReadingMode.fromEntitlement(FeatureEntitlement.free());

        TestAssertions.assertFalse(mode.isEnhanced(), "Free table reading must keep the standard table layout");
    }

    @Test
    void proEntitlementUsesEnhancedTableReading() {
        TableReadingMode mode = TableReadingMode.fromEntitlement(FeatureEntitlement.pro());

        TestAssertions.assertTrue(mode.isEnhanced(), "Pro table reading must enable table reading enhancements");
    }

    @Test
    void missingEntitlementFallsBackToStandardTableReading() {
        TableReadingMode mode = TableReadingMode.fromEntitlement(null);

        TestAssertions.assertFalse(mode.isEnhanced(), "Missing entitlement must fail safe to standard table reading");
    }
}
