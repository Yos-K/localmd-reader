package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class ViewerPreferencesTest {
    @Test
    void languageToggleChangesOnlyTheLanguage() {
        ViewerPreferences preferences = ViewerPreferences.defaults();

        ViewerPreferences changed = preferences.withLanguage(preferences.language().toggled());

        TestAssertions.assertTrue(changed.language().isJapanese(), "language toggle must switch to Japanese");
        TestAssertions.assertEquals(ViewerTheme.LIGHT_VALUE, changed.theme().storedValue(),
                "language toggle must preserve the theme");
    }

    @Test
    void themeChangePreservesLanguageAndControlsPlacement() {
        ViewerPreferences preferences = ViewerPreferences.defaults();

        ViewerPreferences changed = preferences.withTheme(ViewerTheme.dark());

        TestAssertions.assertEquals(ViewerLanguage.ENGLISH_VALUE, changed.language().storedValue(),
                "theme change must preserve the language");
        TestAssertions.assertEquals(ControlsPlacement.TOP_VALUE, changed.controlsPlacement().storedValue(),
                "theme change must preserve controls placement");
    }

    @Test
    void controlsPlacementTogglePreservesLanguageAndTheme() {
        ViewerPreferences preferences = ViewerPreferences.defaults();

        ViewerPreferences changed = preferences.withControlsPlacement(preferences.controlsPlacement().toggled());

        TestAssertions.assertEquals(ViewerLanguage.ENGLISH_VALUE, changed.language().storedValue(),
                "controls placement change must preserve the language");
        TestAssertions.assertEquals(ViewerTheme.LIGHT_VALUE, changed.theme().storedValue(),
                "controls placement change must preserve the theme");
        TestAssertions.assertEquals(ControlsPlacement.BOTTOM_VALUE, changed.controlsPlacement().storedValue(),
                "controls placement must toggle to bottom");
    }

    @Test
    void themeIsClampedWithoutChangingOtherPreferences() {
        ViewerPreferences preferences = new ViewerPreferences(
                ViewerLanguage.japanese(), ViewerTheme.aurora(), ControlsPlacement.bottom());

        ViewerPreferences changed = preferences.clampTheme(io.github.yosk.mdlite.domain.FeatureEntitlement.free());

        TestAssertions.assertEquals(ViewerTheme.LIGHT_VALUE, changed.theme().storedValue(),
                "free entitlement must clamp pro-only theme to light");
        TestAssertions.assertTrue(changed.language().isJapanese(), "theme clamp must preserve the language");
        TestAssertions.assertTrue(changed.controlsPlacement().isBottom(),
                "theme clamp must preserve controls placement");
    }
}
