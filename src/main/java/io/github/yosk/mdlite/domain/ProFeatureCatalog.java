package io.github.yosk.mdlite.domain;

public final class ProFeatureCatalog {
    private static final ProFeatureDescriptor[] INITIAL_FEATURES = {
        new ProFeatureDescriptor(
                ViewerFeature.EXTRA_THEMES,
                "Additional themes",
                "Extra reading themes beyond light and dark."),
        new ProFeatureDescriptor(
                ViewerFeature.CODE_HIGHLIGHTING,
                "Code highlighting",
                "Syntax highlighting for fenced code blocks."),
        new ProFeatureDescriptor(
                ViewerFeature.CUSTOM_GESTURE_SHORTCUTS,
                "Custom gestures",
                "User-configurable reading shortcuts for frequent actions."),
        new ProFeatureDescriptor(
                ViewerFeature.MERMAID_RENDERING,
                "Mermaid diagrams",
                "Local rendering for Mermaid diagram blocks.")
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
