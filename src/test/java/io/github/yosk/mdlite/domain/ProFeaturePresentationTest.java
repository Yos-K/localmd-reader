package io.github.yosk.mdlite.domain;

public final class ProFeaturePresentationTest {
    public static void main(String[] args) {
        freeEntitlementCreatesLockedItemsForTheInitialCatalog();
        proEntitlementCreatesAvailableItemsForTheInitialCatalog();
        presentationItemsPreserveDescriptorText();
    }

    private static void freeEntitlementCreatesLockedItemsForTheInitialCatalog() {
        ProFeaturePresentationItem[] items =
                ProFeaturePresentation.from(FeatureEntitlement.free(), ProFeatureCatalog.initialFeatures());

        assertEquals(4, items.length, "presentation item count");
        for (int i = 0; i < items.length; i++) {
            assertFalse(items[i].isAvailable(), "Free entitlement must lock Pro presentation items");
            assertEquals("Locked", items[i].statusLabel(), "Free status label");
        }
    }

    private static void proEntitlementCreatesAvailableItemsForTheInitialCatalog() {
        ProFeaturePresentationItem[] items =
                ProFeaturePresentation.from(FeatureEntitlement.pro(), ProFeatureCatalog.initialFeatures());

        assertEquals(4, items.length, "presentation item count");
        for (int i = 0; i < items.length; i++) {
            assertTrue(items[i].isAvailable(), "Pro entitlement must make Pro presentation items available");
            assertEquals("Available", items[i].statusLabel(), "Pro status label");
        }
    }

    private static void presentationItemsPreserveDescriptorText() {
        ProFeatureDescriptor descriptor = ProFeatureCatalog.find(ViewerFeature.CODE_HIGHLIGHTING);
        ProFeaturePresentationItem[] items =
                ProFeaturePresentation.from(FeatureEntitlement.free(), new ProFeatureDescriptor[] {descriptor});

        assertEquals(1, items.length, "single presentation item count");
        assertEquals(descriptor.title(), items[0].title(), "title");
        assertEquals(descriptor.description(), items[0].description(), "description");
        assertSame(descriptor.feature(), items[0].feature(), "feature");
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + "\nExpected: " + expected + "\nActual: " + actual);
        }
    }

    private static void assertEquals(String expected, String actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + "\nExpected: " + expected + "\nActual: " + actual);
        }
    }

    private static void assertSame(Object expected, Object actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message);
        }
    }

    private static void assertTrue(boolean actual, String message) {
        if (!actual) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean actual, String message) {
        if (actual) {
            throw new AssertionError(message);
        }
    }
}
