package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class ProFeaturePresentationTest {

    @Test
    void freeEntitlementCreatesLockedItemsForTheInitialCatalog() {
        ProFeaturePresentationItem[] items =
                ProFeaturePresentation.from(FeatureEntitlement.free(), ProFeatureCatalog.initialFeatures());

        TestAssertions.assertEquals(10, items.length, "presentation item count");
        lockedPresentationItem(items[0], "first item");
        lockedPresentationItem(items[1], "second item");
        lockedPresentationItem(items[2], "third item");
        lockedPresentationItem(items[3], "fourth item");
        lockedPresentationItem(items[4], "fifth item");
        lockedPresentationItem(items[5], "sixth item");
        lockedPresentationItem(items[6], "seventh item");
        lockedPresentationItem(items[7], "eighth item");
        lockedPresentationItem(items[8], "HTML export item");
        lockedPresentationItem(items[9], "project library item");
    }

    @Test
    void proEntitlementCreatesAvailableItemsForTheInitialCatalog() {
        ProFeaturePresentationItem[] items =
                ProFeaturePresentation.from(FeatureEntitlement.pro(), ProFeatureCatalog.initialFeatures());

        TestAssertions.assertEquals(10, items.length, "presentation item count");
        availablePresentationItem(items[0], "first item");
        availablePresentationItem(items[1], "second item");
        availablePresentationItem(items[2], "third item");
        availablePresentationItem(items[3], "fourth item");
        availablePresentationItem(items[4], "fifth item");
        availablePresentationItem(items[5], "sixth item");
        availablePresentationItem(items[6], "seventh item");
        availablePresentationItem(items[7], "eighth item");
        availablePresentationItem(items[8], "HTML export item");
        availablePresentationItem(items[9], "project library item");
    }

    private static void lockedPresentationItem(ProFeaturePresentationItem item, String label) {
        TestAssertions.assertFalse(item.isAvailable(), "Free entitlement must lock Pro presentation " + label);
        TestAssertions.assertEquals("Locked", item.statusLabel(), "Free status label for " + label);
    }

    private static void availablePresentationItem(ProFeaturePresentationItem item, String label) {
        TestAssertions.assertTrue(item.isAvailable(), "Pro entitlement must make Pro presentation " + label + " available");
        TestAssertions.assertEquals("Available", item.statusLabel(), "Pro status label for " + label);
    }

    @Test
    void presentationItemsPreserveDescriptorText() {
        ProFeatureDescriptor descriptor = ProFeatureCatalog.find(ViewerFeature.EXTRA_THEMES);
        ProFeaturePresentationItem[] items =
                ProFeaturePresentation.from(FeatureEntitlement.free(), new ProFeatureDescriptor[] {descriptor});

        TestAssertions.assertEquals(1, items.length, "single presentation item count");
        TestAssertions.assertEquals(descriptor.title(), items[0].title(), "title");
        TestAssertions.assertEquals(descriptor.description(), items[0].description(), "description");
        TestAssertions.assertSame(descriptor.feature(), items[0].feature(), "feature");
    }

    // Availability follows the feature's OWN tier (entitlement.allows = pro || feature.isFree()),
    // not whether it sits in a Pro descriptor list. So a free-tier feature placed in a descriptor
    // list is available even to a Free entitlement, while a Pro-tier sibling stays locked.
    // The shipped catalog is all Pro-tier, so this is only exercised here. (exploration 2026-06-13 P3)
    @Test
    void availabilityFollowsTheFeatureTierNotListMembership() {
        ProFeatureDescriptor freeFeature =
                new ProFeatureDescriptor(ViewerFeature.OPEN_LOCAL_MARKDOWN, "Open local", "Open a local Markdown file.");
        ProFeatureDescriptor proFeature =
                new ProFeatureDescriptor(ViewerFeature.EXTRA_THEMES, "More themes", "Extra reading themes.");

        ProFeaturePresentationItem[] items = ProFeaturePresentation.from(
                FeatureEntitlement.free(), new ProFeatureDescriptor[] {freeFeature, proFeature});

        TestAssertions.assertTrue(items[0].isAvailable(),
                "A free-tier feature is available even to a Free entitlement");
        TestAssertions.assertFalse(items[1].isAvailable(),
                "A Pro-tier feature stays locked for a Free entitlement");
    }
}
