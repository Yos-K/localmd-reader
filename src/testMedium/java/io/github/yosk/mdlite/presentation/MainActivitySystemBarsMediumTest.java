package io.github.yosk.mdlite.presentation;

import static org.junit.Assert.assertEquals;

import android.view.View;
import android.view.Window;

import io.github.yosk.mdlite.viewer.ViewerTheme;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

/**
 * Medium-tier test (#69): status / navigation bars must follow the viewer
 * theme, on the JVM (Robolectric), without an emulator.
 *
 * Flag assertions are pinned to SDK 29 so the systemUiVisibility values can be
 * read deterministically; one case runs on the default SDK (35) because the
 * first implementation NPE'd there (Window.getInsetsController dereferences a
 * decor that does not exist before setContentView).
 *
 * Aurora is the regression-sensitive case: it has a dark background but is not
 * in the dark/amoled family, so an isDark()-based implementation would get its
 * icon brightness wrong.
 */
@RunWith(RobolectricTestRunner.class)
public class MainActivitySystemBarsMediumTest {

    @Test
    public void applyingThemeDuringOnCreateSucceedsOnTheDefaultSdk() {
        // Regression guard: building the activity runs applyNativeTheme before
        // setContentView; the first implementation NPE'd here on SDK 30+. On
        // Android 15 setStatusBarColor is a no-op (enforced edge-to-edge paints
        // the app background behind the bars), so this case asserts the icon
        // brightness request instead; bar colors are asserted on SDK 29 below.
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();

        assertEquals("light default theme must request dark status bar icons on the default SDK",
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR,
                activity.getWindow().getDecorView().getSystemUiVisibility()
                        & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    @Test
    @Config(sdk = 29)
    public void lightThemePaintsBarsWithBackgroundAndRequestsDarkIcons() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();
        Window window = activity.getWindow();

        assertEquals("status bar must take the light theme background",
                activity.backgroundColor(), window.getStatusBarColor());
        assertEquals("navigation bar must take the light theme background",
                activity.backgroundColor(), window.getNavigationBarColor());
        assertEquals("light background must request dark status bar icons",
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR,
                window.getDecorView().getSystemUiVisibility() & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    @Test
    @Config(sdk = 29)
    public void switchingToAuroraRepaintsBarsAndRequestsLightIcons() {
        MainActivity activity = Robolectric.buildActivity(MainActivity.class).setup().get();
        Window window = activity.getWindow();

        activity.applySelectedTheme(ViewerTheme.aurora());

        assertEquals("status bar must follow the aurora background after a theme switch",
                activity.backgroundColor(), window.getStatusBarColor());
        assertEquals("navigation bar must follow the aurora background after a theme switch",
                activity.backgroundColor(), window.getNavigationBarColor());
        assertEquals("dark aurora background must clear the light-status-icon flag",
                0,
                window.getDecorView().getSystemUiVisibility() & View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }
}
