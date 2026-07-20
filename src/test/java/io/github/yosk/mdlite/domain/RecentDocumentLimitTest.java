package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class RecentDocumentLimitTest {

    @Test
    void freeEntitlementKeepsRecentDocumentsLimitedToFiveItems() {
        RecentDocumentLimit limit = RecentDocumentLimit.fromEntitlement(FeatureEntitlement.free());

        TestAssertions.assertEquals(5, limit.maxItems(), "Free recent documents must keep the small baseline history");
    }

    @Test
    void proEntitlementExtendsRecentDocumentsToTwentyItems() {
        RecentDocumentLimit limit = RecentDocumentLimit.fromEntitlement(FeatureEntitlement.pro());

        TestAssertions.assertEquals(20, limit.maxItems(), "Pro recent documents must unlock extended history");
    }

    @Test
    void missingEntitlementFallsBackToFreeRecentDocumentLimit() {
        RecentDocumentLimit limit = RecentDocumentLimit.fromEntitlement(null);

        TestAssertions.assertEquals(5, limit.maxItems(), "Missing entitlement must fail safe to the Free recent history limit");
    }
}
