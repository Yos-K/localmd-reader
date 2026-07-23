package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class ProFeatureCatalogTest {
    @Test
    void initialCatalogContainsEveryConvenienceFeatureInDisplayOrder() {
        ViewerFeature[] features = ProFeatureCatalog.initialFeatures();

        TestAssertions.assertEquals(10, features.length, "initial Pro catalog size");
        TestAssertions.assertSame(ViewerFeature.EXTRA_THEMES, features[0], "first Pro feature");
        TestAssertions.assertSame(ViewerFeature.CUSTOM_GESTURE_SHORTCUTS, features[1], "second Pro feature");
        TestAssertions.assertSame(ViewerFeature.TABLE_OF_CONTENTS, features[2], "third Pro feature");
        TestAssertions.assertSame(ViewerFeature.HEADING_JUMP, features[3], "fourth Pro feature");
        TestAssertions.assertSame(ViewerFeature.TABLE_READING_ENHANCEMENTS, features[4], "fifth Pro feature");
        TestAssertions.assertSame(ViewerFeature.EXTENDED_RECENT_FILES, features[5], "sixth Pro feature");
        TestAssertions.assertSame(ViewerFeature.RELATIVE_LINKS, features[6], "seventh Pro feature");
        TestAssertions.assertSame(ViewerFeature.RELATIVE_IMAGES, features[7], "eighth Pro feature");
        TestAssertions.assertSame(ViewerFeature.EXPORT_OPTIONS, features[8], "ninth Pro feature");
        TestAssertions.assertSame(ViewerFeature.PROJECT_LIBRARY, features[9], "tenth Pro feature");
    }

    @Test
    void catalogLookupReturnsTheMatchingFeature() {
        TestAssertions.assertSame(ViewerFeature.EXTRA_THEMES,
                ProFeatureCatalog.find(ViewerFeature.EXTRA_THEMES), "looked up feature");
    }

    @Test
    void unknownCatalogLookupFails() {
        TestAssertions.assertThrows(IllegalArgumentException.class,
                () -> ProFeatureCatalog.find(ViewerFeature.RECENT_FILES_LIMITED));
    }

}
