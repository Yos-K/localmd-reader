package io.github.yosk.mdlite.presentation;

import static org.junit.Assert.assertEquals;

import android.view.View;

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
}
