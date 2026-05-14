package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;

public final class ViewerThemeStyleTest {
    public static void main(String[] args) {
        ViewerThemeStyleTest test = new ViewerThemeStyleTest();
        test.lightThemeStyleProvidesSharedBackgroundColor();
        test.gradientThemeStyleProvidesNativeColorAndCssGradient();
        test.darkThemeStyleProvidesSharedTableScrollHint();
    }

    public void lightThemeStyleProvidesSharedBackgroundColor() {
        ViewerThemeStyle style = ViewerThemeStyle.from(ViewerTheme.light());

        TestAssertions.assertEquals("#f8fbfa", style.background, "Light theme native background must come from the shared theme style");
        TestAssertions.assertEquals("#f8fbfa", style.cssBackground, "Light theme CSS background must come from the shared theme style");
    }

    public void gradientThemeStyleProvidesNativeColorAndCssGradient() {
        ViewerThemeStyle style = ViewerThemeStyle.from(ViewerTheme.gradient());

        TestAssertions.assertEquals("#f7fbf8", style.background, "Gradient theme native background must keep a solid color fallback");
        TestAssertions.assertEquals(
                "linear-gradient(135deg,#f7fbf8 0%,#dcefea 45%,#f4dedb 100%)",
                style.cssBackground,
                "Gradient theme CSS background must come from the shared theme style");
    }

    public void darkThemeStyleProvidesSharedTableScrollHint() {
        ViewerThemeStyle style = ViewerThemeStyle.from(ViewerTheme.dark());

        TestAssertions.assertEquals("#80a8a1", style.tableScrollHint, "Dark theme table scroll hint must come from the shared theme style");
        TestAssertions.assertEquals("0.55", style.tableScrollHintOpacity, "Dark theme table scroll opacity must come from the shared theme style");
    }
}
