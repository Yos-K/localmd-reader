package io.github.yosk.mdlite.domain;

public final class ProFeaturePresentation {
    private ProFeaturePresentation() {
    }

    public static ProFeaturePresentationItem[] from(
            FeatureEntitlement entitlement,
            ProFeatureDescriptor[] descriptors) {
        if (entitlement == null) {
            throw new IllegalArgumentException("Entitlement must not be null.");
        }
        if (descriptors == null) {
            throw new IllegalArgumentException("Descriptors must not be null.");
        }
        ProFeaturePresentationItem[] items = new ProFeaturePresentationItem[descriptors.length];
        for (int i = 0; i < descriptors.length; i++) {
            ProFeatureDescriptor descriptor = descriptors[i];
            if (descriptor == null) {
                throw new IllegalArgumentException("Descriptor must not be null.");
            }
            items[i] = new ProFeaturePresentationItem(
                    descriptor.feature(),
                    descriptor.title(),
                    descriptor.description(),
                    descriptor.isAvailableFor(entitlement));
        }
        return items;
    }
}
