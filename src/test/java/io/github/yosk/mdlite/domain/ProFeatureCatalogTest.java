package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;

public final class ProFeatureCatalogTest {
    public static void main(String[] args) {
        initialCatalogContainsTheFourPlannedProFeatures();
        descriptorsExposeFeatureTitleAndDescriptionWithoutNulls();
        freeEntitlementMarksEveryCatalogItemLocked();
        proEntitlementMarksEveryCatalogItemAvailable();
        catalogLookupReturnsTheMatchingDescriptor();
        unknownCatalogLookupFails();
    }

    private static void initialCatalogContainsTheFourPlannedProFeatures() {
        ProFeatureDescriptor[] descriptors = ProFeatureCatalog.initialFeatures();

        TestAssertions.assertEquals(4, descriptors.length, "initial Pro catalog size");
        TestAssertions.assertSame(ViewerFeature.EXTRA_THEMES, descriptors[0].feature(), "first Pro feature");
        TestAssertions.assertSame(ViewerFeature.CODE_HIGHLIGHTING, descriptors[1].feature(), "second Pro feature");
        TestAssertions.assertSame(ViewerFeature.CUSTOM_GESTURE_SHORTCUTS, descriptors[2].feature(), "third Pro feature");
        TestAssertions.assertSame(ViewerFeature.MERMAID_RENDERING, descriptors[3].feature(), "fourth Pro feature");
    }

    private static void descriptorsExposeFeatureTitleAndDescriptionWithoutNulls() {
        ProFeatureDescriptor[] descriptors = ProFeatureCatalog.initialFeatures();

        descriptorTextIsPresent(descriptors[0], "first descriptor");
        descriptorTextIsPresent(descriptors[1], "second descriptor");
        descriptorTextIsPresent(descriptors[2], "third descriptor");
        descriptorTextIsPresent(descriptors[3], "fourth descriptor");
    }

    private static void freeEntitlementMarksEveryCatalogItemLocked() {
        ProFeatureDescriptor[] descriptors = ProFeatureCatalog.initialFeatures();

        TestAssertions.assertFalse(descriptors[0].isAvailableFor(FeatureEntitlement.free()), "Free entitlement must keep first Pro catalog item locked");
        TestAssertions.assertFalse(descriptors[1].isAvailableFor(FeatureEntitlement.free()), "Free entitlement must keep second Pro catalog item locked");
        TestAssertions.assertFalse(descriptors[2].isAvailableFor(FeatureEntitlement.free()), "Free entitlement must keep third Pro catalog item locked");
        TestAssertions.assertFalse(descriptors[3].isAvailableFor(FeatureEntitlement.free()), "Free entitlement must keep fourth Pro catalog item locked");
    }

    private static void proEntitlementMarksEveryCatalogItemAvailable() {
        ProFeatureDescriptor[] descriptors = ProFeatureCatalog.initialFeatures();

        TestAssertions.assertTrue(descriptors[0].isAvailableFor(FeatureEntitlement.pro()), "Pro entitlement must allow first Pro catalog item");
        TestAssertions.assertTrue(descriptors[1].isAvailableFor(FeatureEntitlement.pro()), "Pro entitlement must allow second Pro catalog item");
        TestAssertions.assertTrue(descriptors[2].isAvailableFor(FeatureEntitlement.pro()), "Pro entitlement must allow third Pro catalog item");
        TestAssertions.assertTrue(descriptors[3].isAvailableFor(FeatureEntitlement.pro()), "Pro entitlement must allow fourth Pro catalog item");
    }

    private static void descriptorTextIsPresent(ProFeatureDescriptor descriptor, String label) {
        TestAssertions.assertNotEmpty(descriptor.title(), label + " title");
        TestAssertions.assertNotEmpty(descriptor.description(), label + " description");
    }

    private static void catalogLookupReturnsTheMatchingDescriptor() {
        ProFeatureDescriptor descriptor = ProFeatureCatalog.find(ViewerFeature.CODE_HIGHLIGHTING);

        TestAssertions.assertSame(ViewerFeature.CODE_HIGHLIGHTING, descriptor.feature(), "looked up feature");
    }

    private static void unknownCatalogLookupFails() {
        TestAssertions.assertThrows(
                IllegalArgumentException.class,
                new TestAssertions.ThrowingRunnable() {
                    @Override
                    public void run() {
                        ProFeatureCatalog.find(ViewerFeature.RECENT_FILES_LIMITED);
                    }
                });
    }
}
