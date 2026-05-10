package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;

public final class ViewerThemeTest {
    public static void main(String[] args) {
        lightThemeIsNotDark();
        darkThemeIsDark();
        amoledThemeIsDarkAndAmoled();
        freeEntitlementCyclesBetweenLightAndDarkOnly();
        proEntitlementCyclesFromDarkToAmoled();
        proEntitlementCyclesFromAmoledToLight();
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

    private static void proEntitlementCyclesFromAmoledToLight() {
        ViewerTheme next = ViewerTheme.amoled().next(FeatureEntitlement.pro());

        TestAssertions.assertFalse(next.isDark(), "Pro theme cycle must return from AMOLED to Light");
    }
}
