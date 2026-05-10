package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;

public final class ProFeaturePresentationTest {
    public static void main(String[] args) {
        freeEntitlementCreatesLockedItemsForTheInitialCatalog();
        proEntitlementCreatesAvailableItemsForTheInitialCatalog();
        presentationItemsPreserveDescriptorText();
    }

    private static void freeEntitlementCreatesLockedItemsForTheInitialCatalog() {
        ProFeaturePresentationItem[] items =
                ProFeaturePresentation.from(FeatureEntitlement.free(), ProFeatureCatalog.initialFeatures());

        TestAssertions.assertEquals(4, items.length, "presentation item count");
        for (int i = 0; i < items.length; i++) {
            TestAssertions.assertFalse(items[i].isAvailable(), "Free entitlement must lock Pro presentation items");
            TestAssertions.assertEquals("Locked", items[i].statusLabel(), "Free status label");
        }
    }

    private static void proEntitlementCreatesAvailableItemsForTheInitialCatalog() {
        ProFeaturePresentationItem[] items =
                ProFeaturePresentation.from(FeatureEntitlement.pro(), ProFeatureCatalog.initialFeatures());

        TestAssertions.assertEquals(4, items.length, "presentation item count");
        for (int i = 0; i < items.length; i++) {
            TestAssertions.assertTrue(items[i].isAvailable(), "Pro entitlement must make Pro presentation items available");
            TestAssertions.assertEquals("Available", items[i].statusLabel(), "Pro status label");
        }
    }

    private static void presentationItemsPreserveDescriptorText() {
        ProFeatureDescriptor descriptor = ProFeatureCatalog.find(ViewerFeature.CODE_HIGHLIGHTING);
        ProFeaturePresentationItem[] items =
                ProFeaturePresentation.from(FeatureEntitlement.free(), new ProFeatureDescriptor[] {descriptor});

        TestAssertions.assertEquals(1, items.length, "single presentation item count");
        TestAssertions.assertEquals(descriptor.title(), items[0].title(), "title");
        TestAssertions.assertEquals(descriptor.description(), items[0].description(), "description");
        TestAssertions.assertSame(descriptor.feature(), items[0].feature(), "feature");
    }
}
