package io.github.yosk.mdlite.presentation;

import static org.junit.Assert.assertEquals;

import android.view.View;

import io.github.yosk.mdlite.domain.FeatureEntitlement;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class MainMenuActionVisibilityMediumTest {

    @Test
    public void clipboardDiagnosticsIsHiddenFromTheNormalUserMenu() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();

        activity.refreshMenuActionButtons();

        assertEquals("clipboard diagnostics is a development aid and must not appear in the normal user menu",
                View.GONE, activity.clipboardDiagnosticsButton.getVisibility());
    }

    @Test
    public void markdownLibraryIsHiddenFromFreeReaders() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();
        activity.featureEntitlement = FeatureEntitlement.free();

        activity.refreshMenuActionButtons();

        assertEquals("Free uses the ordinary file picker instead of a duplicate folder action",
                View.GONE, activity.markdownLibraryButton.getVisibility());
    }

    @Test
    public void markdownLibraryIsVisibleToProReaders() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();
        activity.featureEntitlement = FeatureEntitlement.pro();

        activity.refreshMenuActionButtons();

        assertEquals("Pro must expose its persistent Markdown library",
                View.VISIBLE, activity.markdownLibraryButton.getVisibility());
    }
}
