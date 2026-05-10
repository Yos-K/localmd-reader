package io.github.yosk.mdlite.domain;

public final class ViewerTheme {
    private static final int LIGHT = 1;
    private static final int DARK = 2;
    private static final int AMOLED = 3;

    private final int value;

    private ViewerTheme(int value) {
        if (value != LIGHT && value != DARK && value != AMOLED) {
            throw new IllegalArgumentException("unsupported viewer theme");
        }
        this.value = value;
    }

    public static ViewerTheme light() {
        return new ViewerTheme(LIGHT);
    }

    public static ViewerTheme dark() {
        return new ViewerTheme(DARK);
    }

    public static ViewerTheme amoled() {
        return new ViewerTheme(AMOLED);
    }

    public boolean isDark() {
        return value == DARK || value == AMOLED;
    }

    public boolean isAmoled() {
        return value == AMOLED;
    }

    public ViewerTheme toggled() {
        return isDark() ? light() : dark();
    }

    public ViewerTheme next(FeatureEntitlement entitlement) {
        FeatureEntitlement safeEntitlement = entitlement == null ? FeatureEntitlement.free() : entitlement;
        if (!safeEntitlement.allows(ViewerFeature.EXTRA_THEMES)) {
            return toggled();
        }
        if (value == LIGHT) {
            return dark();
        }
        if (value == DARK) {
            return amoled();
        }
        return light();
    }
}
