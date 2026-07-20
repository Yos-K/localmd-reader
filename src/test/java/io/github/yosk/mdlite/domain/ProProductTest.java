package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class ProProductTest {

    @Test
    void proProductUsesStableGooglePlayProductId() {
        TestAssertions.assertEquals(
                "localmd_reader_pro",
                ProProduct.pro().productId(),
                "Pro product must use the stable Google Play product id");
    }

    @Test
    void proProductUsesOneTimePurchaseType() {
        TestAssertions.assertTrue(
                ProProduct.pro().isOneTimePurchase(),
                "Pro product must be a one-time purchase");
    }
}
