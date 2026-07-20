package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.domain.FeatureEntitlement;
import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class ViewerThemeTest {

    @Test
    void lightThemeIsNotDark() {
        ViewerTheme theme = ViewerTheme.light();

        TestAssertions.assertFalse(theme.isDark(), "Light theme must not be dark");
        TestAssertions.assertFalse(theme.isAmoled(), "Light theme must not be AMOLED");
    }

    @Test
    void darkThemeIsDark() {
        ViewerTheme theme = ViewerTheme.dark();

        TestAssertions.assertTrue(theme.isDark(), "Dark theme must be dark");
        TestAssertions.assertFalse(theme.isAmoled(), "Dark theme must not be AMOLED");
    }

    @Test
    void amoledThemeIsDarkAndAmoled() {
        ViewerTheme theme = ViewerTheme.amoled();

        TestAssertions.assertTrue(theme.isDark(), "AMOLED theme must behave as a dark theme");
        TestAssertions.assertTrue(theme.isAmoled(), "AMOLED theme must expose its specific identity");
        TestAssertions.assertFalse(theme.isGradient(), "AMOLED theme must not expose gradient identity");
    }

    @Test
    void gradientThemeIsNotDarkAndUsesGradientIdentity() {
        ViewerTheme theme = ViewerTheme.gradient();

        TestAssertions.assertFalse(theme.isDark(), "Gradient theme must keep light text contrast rules");
        TestAssertions.assertFalse(theme.isAmoled(), "Gradient theme must not expose AMOLED identity");
        TestAssertions.assertTrue(theme.isGradient(), "Gradient theme must expose its specific identity");
    }

    @Test
    void auroraThemeIsNotDarkAndUsesAuroraIdentity() {
        ViewerTheme theme = ViewerTheme.aurora();

        TestAssertions.assertFalse(theme.isDark(), "Aurora theme must keep light text contrast rules");
        TestAssertions.assertTrue(theme.isAurora(), "Aurora theme must expose its specific identity");
    }

    @Test
    void mistThemeIsNotDarkAndUsesMistIdentity() {
        ViewerTheme theme = ViewerTheme.mist();

        TestAssertions.assertFalse(theme.isDark(), "Mist theme must keep light text contrast rules");
        TestAssertions.assertTrue(theme.isMist(), "Mist theme must expose its specific identity");
    }

    @Test
    void duskThemeIsNotDarkAndUsesDuskIdentity() {
        ViewerTheme theme = ViewerTheme.dusk();

        TestAssertions.assertFalse(theme.isDark(), "Dusk theme must keep light text contrast rules");
        TestAssertions.assertTrue(theme.isDusk(), "Dusk theme must expose its specific identity");
    }

    @Test
    void freeEntitlementCyclesBetweenLightAndDarkOnly() {
        ViewerTheme next = ViewerTheme.dark().next(FeatureEntitlement.free());

        TestAssertions.assertFalse(next.isDark(), "Free theme cycle must return from Dark to Light");
        TestAssertions.assertFalse(next.isAmoled(), "Free theme cycle must not include AMOLED");
    }

    @Test
    void proEntitlementCyclesFromDarkToAmoled() {
        ViewerTheme next = ViewerTheme.dark().next(FeatureEntitlement.pro());

        TestAssertions.assertTrue(next.isAmoled(), "Pro theme cycle must include AMOLED after Dark");
    }

    @Test
    void proEntitlementCyclesFromAmoledToGradient() {
        ViewerTheme next = ViewerTheme.amoled().next(FeatureEntitlement.pro());

        TestAssertions.assertTrue(next.isGradient(), "Pro theme cycle must include Gradient after AMOLED");
    }

    @Test
    void proEntitlementCyclesFromGradientToAurora() {
        ViewerTheme next = ViewerTheme.gradient().next(FeatureEntitlement.pro());

        TestAssertions.assertTrue(next.isAurora(), "Pro theme cycle must include Aurora after Gradient");
    }

    @Test
    void proEntitlementCyclesFromAuroraToMist() {
        ViewerTheme next = ViewerTheme.aurora().next(FeatureEntitlement.pro());

        TestAssertions.assertTrue(next.isMist(), "Pro theme cycle must include Mist after Aurora");
    }

    @Test
    void proEntitlementCyclesFromMistToDusk() {
        ViewerTheme next = ViewerTheme.mist().next(FeatureEntitlement.pro());

        TestAssertions.assertTrue(next.isDusk(), "Pro theme cycle must include Dusk after Mist");
    }

    @Test
    void proEntitlementCyclesFromDuskToLight() {
        ViewerTheme next = ViewerTheme.dusk().next(FeatureEntitlement.pro());

        TestAssertions.assertFalse(next.isDark(), "Pro theme cycle must return from Dusk to Light");
        TestAssertions.assertFalse(next.isDusk(), "Light theme after Dusk must not keep Dusk identity");
    }

    @Test
    void storedValueRestoresGradientTheme() {
        ViewerTheme theme = ViewerTheme.fromStoredValue(ViewerTheme.gradient().storedValue());

        TestAssertions.assertTrue(theme.isGradient(), "stored gradient value must restore the gradient theme");
    }

    @Test
    void storedValueRoundTripsForEveryTheme() {
        assertRoundTrip(ViewerTheme.light());
        assertRoundTrip(ViewerTheme.dark());
        assertRoundTrip(ViewerTheme.amoled());
        assertRoundTrip(ViewerTheme.gradient());
        assertRoundTrip(ViewerTheme.aurora());
        assertRoundTrip(ViewerTheme.mist());
        assertRoundTrip(ViewerTheme.dusk());
    }

    private void assertRoundTrip(ViewerTheme theme) {
        ViewerTheme restored = ViewerTheme.fromStoredValue(theme.storedValue());

        TestAssertions.assertEquals(theme.storedValue(), restored.storedValue(),
                "every theme must survive a save/restore round trip");
    }

    @Test
    void freeAvailableThemesContainOnlyLightAndDark() {
        ViewerTheme[] themes = ViewerTheme.availableThemes(FeatureEntitlement.free());

        TestAssertions.assertEquals(2, themes.length, "Free theme picker must expose only Light and Dark");
        TestAssertions.assertFalse(themes[0].isDark(), "Free theme picker must expose Light first");
        TestAssertions.assertTrue(themes[1].isDark(), "Free theme picker must expose Dark second");
    }

    @Test
    void proAvailableThemesIncludeDuskTheme() {
        ViewerTheme[] themes = ViewerTheme.availableThemes(FeatureEntitlement.pro());

        TestAssertions.assertEquals(7, themes.length, "Pro theme picker must expose all visual themes");
        TestAssertions.assertTrue(themes[6].isDusk(), "Pro theme picker must include Dusk as a direct choice");
    }

    @Test
    void proOnlyThemeReclampsToLightWhenEntitlementFallsBackToFree() {
        ViewerTheme theme = ViewerTheme.dusk().clampedForEntitlement(FeatureEntitlement.free());

        TestAssertions.assertEquals(ViewerTheme.LIGHT_VALUE, theme.storedValue(),
                "Pro-only theme must immediately reclamp to Light when entitlement no longer allows extra themes");
    }

    @Test
    void darkThemeStaysAvailableWhenEntitlementFallsBackToFree() {
        ViewerTheme theme = ViewerTheme.dark().clampedForEntitlement(FeatureEntitlement.free());

        TestAssertions.assertEquals(ViewerTheme.DARK_VALUE, theme.storedValue(),
                "Dark theme must remain available after a Free entitlement reclamp");
    }

    @Test
    void proEntitlementKeepsTheCurrentProThemeDuringReclamp() {
        ViewerTheme theme = ViewerTheme.aurora().clampedForEntitlement(FeatureEntitlement.pro());

        TestAssertions.assertEquals(ViewerTheme.AURORA_VALUE, theme.storedValue(),
                "Pro entitlement must keep the selected Pro theme during reclamp");
    }
}
