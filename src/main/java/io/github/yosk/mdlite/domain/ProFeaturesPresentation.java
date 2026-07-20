package io.github.yosk.mdlite.domain;

public final class ProFeaturesPresentation {
    private final boolean pro;
    private final ProFeaturePresentationItem[] features;
    private final ProPurchasePresentation purchase;

    private ProFeaturesPresentation(
            boolean pro,
            ProFeaturePresentationItem[] features,
            ProPurchasePresentation purchase) {
        this.pro = pro;
        this.features = features.clone();
        this.purchase = purchase;
    }

    public static ProFeaturesPresentation from(
            FeatureEntitlement entitlement,
            ProPurchaseUiState purchaseUiState,
            ProFeatureDescriptor[] descriptors) {
        if (entitlement == null) {
            throw new IllegalArgumentException("Entitlement must not be null.");
        }
        return new ProFeaturesPresentation(
                entitlement.isPro(),
                ProFeaturePresentation.from(entitlement, descriptors),
                ProPurchasePresentation.from(entitlement, purchaseUiState));
    }

    public boolean isPro() {
        return pro;
    }

    public ProFeaturePresentationItem[] features() {
        return features.clone();
    }

    public ProPurchasePresentation purchase() {
        return purchase;
    }
}
