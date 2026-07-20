package io.github.yosk.mdlite.presentation;

import android.os.Build;
import android.view.View;
import android.view.Window;

/**
 * Synchronizes the status / navigation bars with the viewer theme (#69).
 *
 * Bar backgrounds take the theme background color and icon brightness follows
 * ViewerThemeStyle.hasDarkBackground(), so dark themes (including aurora) get
 * light icons.
 *
 * Icon brightness intentionally uses the legacy systemUiVisibility flags on
 * every API level: applyNativeTheme() first runs in onCreate before
 * setContentView, where Window.getInsetsController() dereferences a decor that
 * does not exist yet (NPE on 30+) and would return a controller only after the
 * view is attached. The legacy flags force decor creation, persist on it, and
 * the platform bridges them to the insets controller on attach. Guards: the
 * light-status flag exists from minSdk 23; the light-navigation flag needs 26+.
 */
final class SystemBarsTheme {
    private SystemBarsTheme() {
    }

    static void apply(Window window, ViewerPalette palette) {
        if (window == null || palette == null) {
            return;
        }
        window.setStatusBarColor(palette.background);
        window.setNavigationBarColor(palette.background);
        applyIconBrightness(window, !palette.darkBackground);
    }

    private static void applyIconBrightness(Window window, boolean lightBackground) {
        View decorView = window.getDecorView();
        int flags = withFlag(decorView.getSystemUiVisibility(),
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR, lightBackground);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            flags = withFlag(flags, View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR, lightBackground);
        }
        decorView.setSystemUiVisibility(flags);
    }

    private static int withFlag(int flags, int flag, boolean enabled) {
        return enabled ? flags | flag : flags & ~flag;
    }
}
