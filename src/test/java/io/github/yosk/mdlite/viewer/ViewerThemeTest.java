package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.domain.FeatureEntitlement;
import io.github.yosk.mdlite.testing.TestAssertions;

public final class ViewerThemeTest {
    public static void main(String[] args) {
        lightThemeIsNotDark();
        darkThemeIsDark();
        amoledThemeIsDarkAndAmoled();
        gradientThemeIsNotDarkAndUsesGradientIdentity();
        auroraThemeIsNotDarkAndUsesAuroraIdentity();
        mistThemeIsNotDarkAndUsesMistIdentity();
        duskThemeIsNotDarkAndUsesDuskIdentity();
        freeEntitlementCyclesBetweenLightAndDarkOnly();
        proEntitlementCyclesFromDarkToAmoled();
        proEntitlementCyclesFromAmoledToGradient();
        proEntitlementCyclesFromGradientToAurora();
        proEntitlementCyclesFromAuroraToMist();
        proEntitlementCyclesFromMistToDusk();
        proEntitlementCyclesFromDuskToLight();
    }

    private static void lightThemeIsNotDark() {
        ViewerTheme theme = ViewerTheme.light();

        TestAssertions.assertFalse(theme.isDark(), "Light theme must not be dark");
        TestAssertions.assertFalse(theme.isAmoled(), "Light theme must not be AMOLED");
    }

    private static void darkThemeIsDark() {
        ViewerTheme theme = ViewerTheme.dark();

        TestAssertions.assertTrue(theme.isDark(), "Dark theme must be dark");
        TestAssertions.assertFalse(theme.isAmoled(), "Dark theme must not be AMOLED");
    }

    private static void amoledThemeIsDarkAndAmoled() {
        ViewerTheme theme = ViewerTheme.amoled();

        TestAssertions.assertTrue(theme.isDark(), "AMOLED theme must behave as a dark theme");
        TestAssertions.assertTrue(theme.isAmoled(), "AMOLED theme must expose its specific identity");
        TestAssertions.assertFalse(theme.isGradient(), "AMOLED theme must not expose gradient identity");
    }

    private static void gradientThemeIsNotDarkAndUsesGradientIdentity() {
        ViewerTheme theme = ViewerTheme.gradient();

        TestAssertions.assertFalse(theme.isDark(), "Gradient theme must keep light text contrast rules");
        TestAssertions.assertFalse(theme.isAmoled(), "Gradient theme must not expose AMOLED identity");
        TestAssertions.assertTrue(theme.isGradient(), "Gradient theme must expose its specific identity");
    }

    private static void auroraThemeIsNotDarkAndUsesAuroraIdentity() {
        ViewerTheme theme = ViewerTheme.aurora();

        TestAssertions.assertFalse(theme.isDark(), "Aurora theme must keep light text contrast rules");
        TestAssertions.assertTrue(theme.isAurora(), "Aurora theme must expose its specific identity");
    }

    private static void mistThemeIsNotDarkAndUsesMistIdentity() {
        ViewerTheme theme = ViewerTheme.mist();

        TestAssertions.assertFalse(theme.isDark(), "Mist theme must keep light text contrast rules");
        TestAssertions.assertTrue(theme.isMist(), "Mist theme must expose its specific identity");
    }

    private static void duskThemeIsNotDarkAndUsesDuskIdentity() {
        ViewerTheme theme = ViewerTheme.dusk();

        TestAssertions.assertFalse(theme.isDark(), "Dusk theme must keep light text contrast rules");
        TestAssertions.assertTrue(theme.isDusk(), "Dusk theme must expose its specific identity");
    }

    private static void freeEntitlementCyclesBetweenLightAndDarkOnly() {
        ViewerTheme next = ViewerTheme.dark().next(FeatureEntitlement.free());

        TestAssertions.assertFalse(next.isDark(), "Free theme cycle must return from Dark to Light");
        TestAssertions.assertFalse(next.isAmoled(), "Free theme cycle must not include AMOLED");
    }

    private static void proEntitlementCyclesFromDarkToAmoled() {
        ViewerTheme next = ViewerTheme.dark().next(FeatureEntitlement.pro());

        TestAssertions.assertTrue(next.isAmoled(), "Pro theme cycle must include AMOLED after Dark");
    }

    private static void proEntitlementCyclesFromAmoledToGradient() {
        ViewerTheme next = ViewerTheme.amoled().next(FeatureEntitlement.pro());

        TestAssertions.assertTrue(next.isGradient(), "Pro theme cycle must include Gradient after AMOLED");
    }

    private static void proEntitlementCyclesFromGradientToAurora() {
        ViewerTheme next = ViewerTheme.gradient().next(FeatureEntitlement.pro());

        TestAssertions.assertTrue(next.isAurora(), "Pro theme cycle must include Aurora after Gradient");
    }

    private static void proEntitlementCyclesFromAuroraToMist() {
        ViewerTheme next = ViewerTheme.aurora().next(FeatureEntitlement.pro());

        TestAssertions.assertTrue(next.isMist(), "Pro theme cycle must include Mist after Aurora");
    }

    private static void proEntitlementCyclesFromMistToDusk() {
        ViewerTheme next = ViewerTheme.mist().next(FeatureEntitlement.pro());

        TestAssertions.assertTrue(next.isDusk(), "Pro theme cycle must include Dusk after Mist");
    }

    private static void proEntitlementCyclesFromDuskToLight() {
        ViewerTheme next = ViewerTheme.dusk().next(FeatureEntitlement.pro());

        TestAssertions.assertFalse(next.isDark(), "Pro theme cycle must return from Dusk to Light");
        TestAssertions.assertFalse(next.isDusk(), "Light theme after Dusk must not keep Dusk identity");
    }
}
