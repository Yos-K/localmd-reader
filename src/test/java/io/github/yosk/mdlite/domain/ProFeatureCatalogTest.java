package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class ProFeatureCatalogTest {

    @Test
    void initialCatalogContainsConvenienceProFeatures() {
        ProFeatureDescriptor[] descriptors = ProFeatureCatalog.initialFeatures();

        TestAssertions.assertEquals(10, descriptors.length, "initial Pro catalog size");
        TestAssertions.assertSame(ViewerFeature.EXTRA_THEMES, descriptors[0].feature(), "first Pro feature");
        TestAssertions.assertSame(ViewerFeature.CUSTOM_GESTURE_SHORTCUTS, descriptors[1].feature(), "second Pro feature");
        TestAssertions.assertSame(ViewerFeature.TABLE_OF_CONTENTS, descriptors[2].feature(), "third Pro feature");
        TestAssertions.assertSame(ViewerFeature.HEADING_JUMP, descriptors[3].feature(), "fourth Pro feature");
        TestAssertions.assertSame(ViewerFeature.TABLE_READING_ENHANCEMENTS, descriptors[4].feature(), "fifth Pro feature");
        TestAssertions.assertSame(ViewerFeature.EXTENDED_RECENT_FILES, descriptors[5].feature(), "sixth Pro feature");
        TestAssertions.assertSame(ViewerFeature.RELATIVE_LINKS, descriptors[6].feature(), "seventh Pro feature");
        TestAssertions.assertSame(ViewerFeature.RELATIVE_IMAGES, descriptors[7].feature(), "eighth Pro feature");
        TestAssertions.assertSame(ViewerFeature.EXPORT_OPTIONS, descriptors[8].feature(), "ninth Pro feature");
        TestAssertions.assertSame(ViewerFeature.PROJECT_LIBRARY, descriptors[9].feature(), "tenth Pro feature");
    }

    @Test
    void descriptorsExposeFeatureTitleAndDescriptionWithoutNulls() {
        ProFeatureDescriptor[] descriptors = ProFeatureCatalog.initialFeatures();

        descriptorTextIsPresent(descriptors[0], "first descriptor");
        descriptorTextIsPresent(descriptors[1], "second descriptor");
        descriptorTextIsPresent(descriptors[2], "third descriptor");
        descriptorTextIsPresent(descriptors[3], "fourth descriptor");
        descriptorTextIsPresent(descriptors[4], "fifth descriptor");
        descriptorTextIsPresent(descriptors[5], "sixth descriptor");
        descriptorTextIsPresent(descriptors[6], "seventh descriptor");
        descriptorTextIsPresent(descriptors[7], "eighth descriptor");
        descriptorTextIsPresent(descriptors[8], "ninth descriptor");
        descriptorTextIsPresent(descriptors[9], "tenth descriptor");
    }

    @Test
    void catalogCopyExplainsProAsReadingConvenienceInsteadOfFeatureLocking() {
        ProFeatureDescriptor[] descriptors = ProFeatureCatalog.initialFeatures();

        TestAssertions.assertEquals(
                "Faster long-document navigation",
                descriptors[2].title(),
                "table of contents title must describe the reading benefit");
        TestAssertions.assertEquals(
                "Linked project notes",
                descriptors[6].title(),
                "relative link title must describe the project-note benefit");
        TestAssertions.assertEquals(
                "Render safe relative image references inside local Markdown document sets.",
                descriptors[7].description(),
                "relative image description must keep the local document-set context");
    }

    @Test
    void freeEntitlementMarksEveryCatalogItemLocked() {
        ProFeatureDescriptor[] descriptors = ProFeatureCatalog.initialFeatures();

        TestAssertions.assertFalse(descriptors[0].isAvailableFor(FeatureEntitlement.free()), "Free entitlement must keep first Pro catalog item locked");
        TestAssertions.assertFalse(descriptors[1].isAvailableFor(FeatureEntitlement.free()), "Free entitlement must keep second Pro catalog item locked");
        TestAssertions.assertFalse(descriptors[2].isAvailableFor(FeatureEntitlement.free()), "Free entitlement must keep third Pro catalog item locked");
        TestAssertions.assertFalse(descriptors[3].isAvailableFor(FeatureEntitlement.free()), "Free entitlement must keep fourth Pro catalog item locked");
        TestAssertions.assertFalse(descriptors[4].isAvailableFor(FeatureEntitlement.free()), "Free entitlement must keep fifth Pro catalog item locked");
        TestAssertions.assertFalse(descriptors[5].isAvailableFor(FeatureEntitlement.free()), "Free entitlement must keep sixth Pro catalog item locked");
        TestAssertions.assertFalse(descriptors[6].isAvailableFor(FeatureEntitlement.free()), "Free entitlement must keep seventh Pro catalog item locked");
        TestAssertions.assertFalse(descriptors[7].isAvailableFor(FeatureEntitlement.free()), "Free entitlement must keep eighth Pro catalog item locked");
        TestAssertions.assertFalse(descriptors[8].isAvailableFor(FeatureEntitlement.free()), "Free entitlement must keep ninth Pro catalog item locked");
        TestAssertions.assertFalse(descriptors[9].isAvailableFor(FeatureEntitlement.free()), "Free entitlement must keep tenth Pro catalog item locked");
    }

    @Test
    void proEntitlementMarksEveryCatalogItemAvailable() {
        ProFeatureDescriptor[] descriptors = ProFeatureCatalog.initialFeatures();

        TestAssertions.assertTrue(descriptors[0].isAvailableFor(FeatureEntitlement.pro()), "Pro entitlement must allow first Pro catalog item");
        TestAssertions.assertTrue(descriptors[1].isAvailableFor(FeatureEntitlement.pro()), "Pro entitlement must allow second Pro catalog item");
        TestAssertions.assertTrue(descriptors[2].isAvailableFor(FeatureEntitlement.pro()), "Pro entitlement must allow third Pro catalog item");
        TestAssertions.assertTrue(descriptors[3].isAvailableFor(FeatureEntitlement.pro()), "Pro entitlement must allow fourth Pro catalog item");
        TestAssertions.assertTrue(descriptors[4].isAvailableFor(FeatureEntitlement.pro()), "Pro entitlement must allow fifth Pro catalog item");
        TestAssertions.assertTrue(descriptors[5].isAvailableFor(FeatureEntitlement.pro()), "Pro entitlement must allow sixth Pro catalog item");
        TestAssertions.assertTrue(descriptors[6].isAvailableFor(FeatureEntitlement.pro()), "Pro entitlement must allow seventh Pro catalog item");
        TestAssertions.assertTrue(descriptors[7].isAvailableFor(FeatureEntitlement.pro()), "Pro entitlement must allow eighth Pro catalog item");
        TestAssertions.assertTrue(descriptors[8].isAvailableFor(FeatureEntitlement.pro()), "Pro entitlement must allow ninth Pro catalog item");
        TestAssertions.assertTrue(descriptors[9].isAvailableFor(FeatureEntitlement.pro()), "Pro entitlement must allow tenth Pro catalog item");
    }

    @Test
    void exportCatalogItemDescribesHtmlAndPdfOutput() {
        ProFeatureDescriptor descriptor = ProFeatureCatalog.find(ViewerFeature.EXPORT_OPTIONS);

        TestAssertions.assertEquals(
                "Export and print",
                descriptor.title(),
                "export title must cover both document output actions");
        TestAssertions.assertEquals(
                "Save as HTML or use Android printing to save as PDF.",
                descriptor.description(),
                "export description must explain both output paths");
    }

    private static void descriptorTextIsPresent(ProFeatureDescriptor descriptor, String label) {
        TestAssertions.assertNotEmpty(descriptor.title(), label + " title");
        TestAssertions.assertNotEmpty(descriptor.description(), label + " description");
    }

    @Test
    void catalogLookupReturnsTheMatchingDescriptor() {
        ProFeatureDescriptor descriptor = ProFeatureCatalog.find(ViewerFeature.EXTRA_THEMES);

        TestAssertions.assertSame(ViewerFeature.EXTRA_THEMES, descriptor.feature(), "looked up feature");
    }

    @Test
    void unknownCatalogLookupFails() {
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
