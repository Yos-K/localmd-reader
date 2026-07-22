package io.github.yosk.mdlite.domain;

final class ProFeatureFixtures {
    private ProFeatureFixtures() {
    }

    static ProFeatureDescriptor[] descriptors() {
        ViewerFeature[] features = ProFeatureCatalog.initialFeatures();
        return new ProFeatureDescriptor[] {
            descriptor(features[0]), descriptor(features[1]), descriptor(features[2]),
            descriptor(features[3]), descriptor(features[4]), descriptor(features[5]),
            descriptor(features[6]), descriptor(features[7]), descriptor(features[8]),
            descriptor(features[9])
        };
    }

    private static ProFeatureDescriptor descriptor(ViewerFeature feature) {
        return new ProFeatureDescriptor(feature, "Feature", "Feature description.");
    }
}
