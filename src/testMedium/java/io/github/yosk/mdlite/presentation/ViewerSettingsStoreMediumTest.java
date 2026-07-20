package io.github.yosk.mdlite.presentation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import io.github.yosk.mdlite.domain.FeatureEntitlement;
import io.github.yosk.mdlite.viewer.ControlsPlacement;
import io.github.yosk.mdlite.viewer.ViewerLanguage;
import io.github.yosk.mdlite.viewer.ViewerTheme;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

/**
 * Medium-tier test: ViewerSettingsStore persists settings through real Android
 * SharedPreferences on the JVM (Robolectric), so the save/restore glue is checked
 * without an emulator. See docs/harness/test-strategy.md.
 */
@RunWith(RobolectricTestRunner.class)
public class ViewerSettingsStoreMediumTest {

    private ViewerSettingsStore store(FeatureEntitlement entitlement) {
        Context context = RuntimeEnvironment.getApplication();
        return new ViewerSettingsStore(context, entitlement);
    }

    @Test
    public void viewerThemeRoundTripsForPro() {
        store(FeatureEntitlement.pro()).saveViewerTheme(ViewerTheme.amoled());
        assertEquals(
                ViewerTheme.AMOLED_VALUE,
                store(FeatureEntitlement.pro()).loadViewerTheme().storedValue());
    }

    @Test
    public void viewerLanguageRoundTrips() {
        store(FeatureEntitlement.free()).saveViewerLanguage(ViewerLanguage.japanese());
        assertTrue(store(FeatureEntitlement.free()).loadViewerLanguage().isJapanese());
    }

    @Test
    public void controlsPlacementRoundTrips() {
        store(FeatureEntitlement.free()).saveControlsPlacement(ControlsPlacement.bottom());
        assertTrue(store(FeatureEntitlement.free()).loadControlsPlacement().isBottom());
    }

    @Test
    public void freeEntitlementDoesNotRestoreProOnlyTheme() {
        // A Pro-only theme persisted earlier must not come back for a Free user
        // (entitlement-gated theme rule enforced at the persistence boundary).
        store(FeatureEntitlement.pro()).saveViewerTheme(ViewerTheme.amoled());
        assertNotEquals(
                ViewerTheme.AMOLED_VALUE,
                store(FeatureEntitlement.free()).loadViewerTheme().storedValue());
    }

    @Test
    @Config(qualifiers = "night")
    public void firstLaunchFollowsSystemDarkMode() {
        // No saved theme yet: the system night setting decides the default (#70).
        assertEquals(
                ViewerTheme.DARK_VALUE,
                store(FeatureEntitlement.free()).loadViewerTheme().storedValue());
    }

    @Test
    public void firstLaunchDefaultsToLightWithoutSystemDarkMode() {
        assertEquals(
                ViewerTheme.LIGHT_VALUE,
                store(FeatureEntitlement.free()).loadViewerTheme().storedValue());
    }

    @Test
    @Config(qualifiers = "night")
    public void savedThemeAlwaysBeatsSystemDarkMode() {
        store(FeatureEntitlement.free()).saveViewerTheme(ViewerTheme.light());
        assertEquals(
                ViewerTheme.LIGHT_VALUE,
                store(FeatureEntitlement.free()).loadViewerTheme().storedValue());
    }
}
