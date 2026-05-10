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

        for (int i = 0; i < descriptors.length; i++) {
            ProFeatureDescriptor descriptor = descriptors[i];
            TestAssertions.assertNotEmpty(descriptor.title(), "title");
            TestAssertions.assertNotEmpty(descriptor.description(), "description");
        }
    }

    private static void freeEntitlementMarksEveryCatalogItemLocked() {
        ProFeatureDescriptor[] descriptors = ProFeatureCatalog.initialFeatures();

        for (int i = 0; i < descriptors.length; i++) {
            TestAssertions.assertFalse(
                    descriptors[i].isAvailableFor(FeatureEntitlement.free()),
                    "Free entitlement must keep Pro catalog items locked");
        }
    }

    private static void proEntitlementMarksEveryCatalogItemAvailable() {
        ProFeatureDescriptor[] descriptors = ProFeatureCatalog.initialFeatures();

        for (int i = 0; i < descriptors.length; i++) {
            TestAssertions.assertTrue(
                    descriptors[i].isAvailableFor(FeatureEntitlement.pro()),
                    "Pro entitlement must allow every Pro catalog item");
        }
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
