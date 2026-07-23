package io.github.yosk.mdlite.domain;

public final class ProFeatureCatalog {
    private static final ViewerFeature[] INITIAL_FEATURES = {
        ViewerFeature.EXTRA_THEMES,
        ViewerFeature.CUSTOM_GESTURE_SHORTCUTS,
        ViewerFeature.TABLE_OF_CONTENTS,
        ViewerFeature.HEADING_JUMP,
        ViewerFeature.TABLE_READING_ENHANCEMENTS,
        ViewerFeature.EXTENDED_RECENT_FILES,
        ViewerFeature.RELATIVE_LINKS,
        ViewerFeature.RELATIVE_IMAGES,
        ViewerFeature.EXPORT_OPTIONS,
        ViewerFeature.PROJECT_LIBRARY
    };

    private ProFeatureCatalog() {
    }

    public static ViewerFeature[] initialFeatures() {
        ViewerFeature[] copy = new ViewerFeature[INITIAL_FEATURES.length];
        System.arraycopy(INITIAL_FEATURES, 0, copy, 0, INITIAL_FEATURES.length);
        return copy;
    }

    public static ViewerFeature find(ViewerFeature feature) {
        for (int i = 0; i < INITIAL_FEATURES.length; i++) {
            ViewerFeature catalogFeature = INITIAL_FEATURES[i];
            if (catalogFeature == feature) {
                return catalogFeature;
            }
        }
        throw new IllegalArgumentException("Feature is not in the initial Pro catalog.");
    }
}
