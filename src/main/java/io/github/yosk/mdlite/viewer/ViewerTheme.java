package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.domain.FeatureEntitlement;
import io.github.yosk.mdlite.domain.ViewerFeature;

public final class ViewerTheme {
    private static final int LIGHT = 1;
    private static final int DARK = 2;
    private static final int AMOLED = 3;
    private static final int GRADIENT = 4;
    private static final int AURORA = 5;
    private static final int MIST = 6;
    private static final int DUSK = 7;
    public static final String LIGHT_VALUE = "light";
    public static final String DARK_VALUE = "dark";
    public static final String AMOLED_VALUE = "amoled";
    public static final String GRADIENT_VALUE = "gradient";
    public static final String AURORA_VALUE = "aurora";
    public static final String MIST_VALUE = "mist";
    public static final String DUSK_VALUE = "dusk";

    private final int value;

    private ViewerTheme(int value) {
        if (value != LIGHT && value != DARK && value != AMOLED && value != GRADIENT
                && value != AURORA && value != MIST && value != DUSK) {
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

    public static ViewerTheme gradient() {
        return new ViewerTheme(GRADIENT);
    }

    public static ViewerTheme aurora() {
        return new ViewerTheme(AURORA);
    }

    public static ViewerTheme mist() {
        return new ViewerTheme(MIST);
    }

    public static ViewerTheme dusk() {
        return new ViewerTheme(DUSK);
    }

    public static ViewerTheme fromStoredValue(String storedValue) {
        if (DARK_VALUE.equals(storedValue)) {
            return dark();
        }
        if (AMOLED_VALUE.equals(storedValue)) {
            return amoled();
        }
        if (GRADIENT_VALUE.equals(storedValue)) {
            return gradient();
        }
        if (AURORA_VALUE.equals(storedValue)) {
            return aurora();
        }
        if (MIST_VALUE.equals(storedValue)) {
            return mist();
        }
        if (DUSK_VALUE.equals(storedValue)) {
            return dusk();
        }
        return light();
    }

    public static ViewerTheme[] availableThemes(FeatureEntitlement entitlement) {
        FeatureEntitlement safeEntitlement = entitlement == null ? FeatureEntitlement.free() : entitlement;
        if (!safeEntitlement.allows(ViewerFeature.EXTRA_THEMES)) {
            return new ViewerTheme[] { light(), dark() };
        }
        return new ViewerTheme[] { light(), dark(), amoled(), gradient(), aurora(), mist(), dusk() };
    }

    public boolean isDark() {
        return value == DARK || value == AMOLED;
    }

    public boolean isAmoled() {
        return value == AMOLED;
    }

    public boolean isGradient() {
        return value == GRADIENT;
    }

    public boolean isAurora() {
        return value == AURORA;
    }

    public boolean isMist() {
        return value == MIST;
    }

    public boolean isDusk() {
        return value == DUSK;
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
        if (value == AMOLED) {
            return gradient();
        }
        if (value == GRADIENT) {
            return aurora();
        }
        if (value == AURORA) {
            return mist();
        }
        if (value == MIST) {
            return dusk();
        }
        return light();
    }

    public ViewerTheme clampedForEntitlement(FeatureEntitlement entitlement) {
        FeatureEntitlement safeEntitlement = entitlement == null ? FeatureEntitlement.free() : entitlement;
        if (safeEntitlement.allows(ViewerFeature.EXTRA_THEMES)) {
            return this;
        }
        return DARK_VALUE.equals(storedValue()) ? dark() : light();
    }

    public String storedValue() {
        if (value == DARK) {
            return DARK_VALUE;
        }
        if (value == AMOLED) {
            return AMOLED_VALUE;
        }
        if (value == GRADIENT) {
            return GRADIENT_VALUE;
        }
        if (value == AURORA) {
            return AURORA_VALUE;
        }
        if (value == MIST) {
            return MIST_VALUE;
        }
        if (value == DUSK) {
            return DUSK_VALUE;
        }
        return LIGHT_VALUE;
    }
}
