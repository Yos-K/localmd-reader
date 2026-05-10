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
        lockedPresentationItem(items[0], "first item");
        lockedPresentationItem(items[1], "second item");
        lockedPresentationItem(items[2], "third item");
        lockedPresentationItem(items[3], "fourth item");
    }

    private static void proEntitlementCreatesAvailableItemsForTheInitialCatalog() {
        ProFeaturePresentationItem[] items =
                ProFeaturePresentation.from(FeatureEntitlement.pro(), ProFeatureCatalog.initialFeatures());

        TestAssertions.assertEquals(4, items.length, "presentation item count");
        availablePresentationItem(items[0], "first item");
        availablePresentationItem(items[1], "second item");
        availablePresentationItem(items[2], "third item");
        availablePresentationItem(items[3], "fourth item");
    }

    private static void lockedPresentationItem(ProFeaturePresentationItem item, String label) {
        TestAssertions.assertFalse(item.isAvailable(), "Free entitlement must lock Pro presentation " + label);
        TestAssertions.assertEquals("Locked", item.statusLabel(), "Free status label for " + label);
    }

    private static void availablePresentationItem(ProFeaturePresentationItem item, String label) {
        TestAssertions.assertTrue(item.isAvailable(), "Pro entitlement must make Pro presentation " + label + " available");
        TestAssertions.assertEquals("Available", item.statusLabel(), "Pro status label for " + label);
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
