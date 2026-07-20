package io.github.yosk.mdlite.domain;

public final class ProFeatureCatalog {
    private static final ProFeatureDescriptor[] INITIAL_FEATURES = {
        new ProFeatureDescriptor(
                ViewerFeature.EXTRA_THEMES,
                "More comfortable reading themes",
                "Choose extra color styles when light and dark are not enough for your reading environment."),
        new ProFeatureDescriptor(
                ViewerFeature.CUSTOM_GESTURE_SHORTCUTS,
                "More gesture shortcuts",
                "Assign circle, direction, and custom gestures to the actions you repeat most."),
        new ProFeatureDescriptor(
                ViewerFeature.TABLE_OF_CONTENTS,
                "Faster long-document navigation",
                "Open the table of contents and jump through headings without losing your place."),
        new ProFeatureDescriptor(
                ViewerFeature.HEADING_JUMP,
                "Heading jump shortcuts",
                "Move to the next or previous heading from gestures while reading."),
        new ProFeatureDescriptor(
                ViewerFeature.TABLE_READING_ENHANCEMENTS,
                "Easier wide-table reading",
                "Keep table headers and first columns visible while reading wide tables."),
        new ProFeatureDescriptor(
                ViewerFeature.EXTENDED_RECENT_FILES,
                "More reading history",
                "Keep more recent files available when you switch between many local documents."),
        new ProFeatureDescriptor(
                ViewerFeature.RELATIVE_LINKS,
                "Linked project notes",
                "Open safe relative Markdown links inside local project document sets."),
        new ProFeatureDescriptor(
                ViewerFeature.RELATIVE_IMAGES,
                "Local images in project notes",
                "Render safe relative image references inside local Markdown document sets."),
        new ProFeatureDescriptor(
                ViewerFeature.EXPORT_OPTIONS,
                "Export and print",
                "Save as HTML or use Android printing to save as PDF."),
        new ProFeatureDescriptor(
                ViewerFeature.PROJECT_LIBRARY,
                "Navigate project folders",
                "Move through nested folders and open related Markdown files without leaving the library.")
    };

    private ProFeatureCatalog() {
    }

    public static ProFeatureDescriptor[] initialFeatures() {
        ProFeatureDescriptor[] copy = new ProFeatureDescriptor[INITIAL_FEATURES.length];
        System.arraycopy(INITIAL_FEATURES, 0, copy, 0, INITIAL_FEATURES.length);
        return copy;
    }

    public static ProFeatureDescriptor find(ViewerFeature feature) {
        for (int i = 0; i < INITIAL_FEATURES.length; i++) {
            ProFeatureDescriptor descriptor = INITIAL_FEATURES[i];
            if (descriptor.feature() == feature) {
                return descriptor;
            }
        }
        throw new IllegalArgumentException("Feature is not in the initial Pro catalog.");
    }
}
