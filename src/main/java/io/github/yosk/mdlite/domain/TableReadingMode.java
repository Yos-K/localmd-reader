package io.github.yosk.mdlite.domain;

public final class TableReadingMode {
    private static final TableReadingMode STANDARD = new TableReadingMode(false);
    private static final TableReadingMode ENHANCED = new TableReadingMode(true);

    private final boolean enhanced;

    private TableReadingMode(boolean enhanced) {
        this.enhanced = enhanced;
    }

    public static TableReadingMode standard() {
        return STANDARD;
    }

    public static TableReadingMode enhanced() {
        return ENHANCED;
    }

    public static TableReadingMode fromEntitlement(FeatureEntitlement entitlement) {
        FeatureEntitlement safeEntitlement = entitlement == null ? FeatureEntitlement.free() : entitlement;
        return safeEntitlement.allows(ViewerFeature.TABLE_READING_ENHANCEMENTS) ? enhanced() : standard();
    }

    public boolean isEnhanced() {
        return enhanced;
    }
}
