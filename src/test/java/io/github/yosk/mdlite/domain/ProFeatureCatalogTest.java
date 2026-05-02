package io.github.yosk.mdlite.domain;

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

        assertEquals(4, descriptors.length, "initial Pro catalog size");
        assertSame(ViewerFeature.EXTRA_THEMES, descriptors[0].feature(), "first Pro feature");
        assertSame(ViewerFeature.CODE_HIGHLIGHTING, descriptors[1].feature(), "second Pro feature");
        assertSame(ViewerFeature.CUSTOM_GESTURE_SHORTCUTS, descriptors[2].feature(), "third Pro feature");
        assertSame(ViewerFeature.MERMAID_RENDERING, descriptors[3].feature(), "fourth Pro feature");
    }

    private static void descriptorsExposeFeatureTitleAndDescriptionWithoutNulls() {
        ProFeatureDescriptor[] descriptors = ProFeatureCatalog.initialFeatures();

        for (int i = 0; i < descriptors.length; i++) {
            ProFeatureDescriptor descriptor = descriptors[i];
            assertNotEmpty(descriptor.title(), "title");
            assertNotEmpty(descriptor.description(), "description");
        }
    }

    private static void freeEntitlementMarksEveryCatalogItemLocked() {
        ProFeatureDescriptor[] descriptors = ProFeatureCatalog.initialFeatures();

        for (int i = 0; i < descriptors.length; i++) {
            assertFalse(
                    descriptors[i].isAvailableFor(FeatureEntitlement.free()),
                    "Free entitlement must keep Pro catalog items locked");
        }
    }

    private static void proEntitlementMarksEveryCatalogItemAvailable() {
        ProFeatureDescriptor[] descriptors = ProFeatureCatalog.initialFeatures();

        for (int i = 0; i < descriptors.length; i++) {
            assertTrue(
                    descriptors[i].isAvailableFor(FeatureEntitlement.pro()),
                    "Pro entitlement must allow every Pro catalog item");
        }
    }

    private static void catalogLookupReturnsTheMatchingDescriptor() {
        ProFeatureDescriptor descriptor = ProFeatureCatalog.find(ViewerFeature.CODE_HIGHLIGHTING);

        assertSame(ViewerFeature.CODE_HIGHLIGHTING, descriptor.feature(), "looked up feature");
    }

    private static void unknownCatalogLookupFails() {
        assertThrows(
                IllegalArgumentException.class,
                new ThrowingRunnable() {
                    @Override
                    public void run() {
                        ProFeatureCatalog.find(ViewerFeature.RECENT_FILES_LIMITED);
                    }
                });
    }

    private static void assertEquals(int expected, int actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + "\nExpected: " + expected + "\nActual: " + actual);
        }
    }

    private static void assertSame(Object expected, Object actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message);
        }
    }

    private static void assertNotEmpty(String actual, String message) {
        if (actual == null || actual.length() == 0) {
            throw new AssertionError(message + " must not be empty");
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

    private static void assertThrows(
            Class<? extends RuntimeException> expected,
            ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch (RuntimeException actual) {
            if (expected.isInstance(actual)) {
                return;
            }
            throw new AssertionError("Unexpected exception: " + actual);
        }
        throw new AssertionError("Expected exception: " + expected.getName());
    }

    private interface ThrowingRunnable {
        void run();
    }
}
