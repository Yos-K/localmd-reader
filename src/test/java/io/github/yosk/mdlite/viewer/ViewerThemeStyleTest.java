package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;

public final class ViewerThemeStyleTest {
    public static void main(String[] args) {
        ViewerThemeStyleTest test = new ViewerThemeStyleTest();
        test.lightThemeStyleProvidesSharedBackgroundColor();
        test.gradientThemeStyleProvidesNativeColorAndCssGradient();
        test.gradientAndAuroraUseClearlyDifferentBackgrounds();
        test.darkAndAmoledUseClearlyDifferentSurfaces();
        test.auroraThemeStyleUsesReadableDarkNativeColors();
        test.amoledThemeStyleUsesReadablePureBlackNativeColors();
        test.darkThemeStyleProvidesSharedTableScrollHint();
    }

    public void lightThemeStyleProvidesSharedBackgroundColor() {
        ViewerThemeStyle style = ViewerThemeStyle.from(ViewerTheme.light());

        TestAssertions.assertEquals("#f8fbfa", style.background, "Light theme native background must come from the shared theme style");
        TestAssertions.assertEquals("#f8fbfa", style.cssBackground, "Light theme CSS background must come from the shared theme style");
    }

    public void gradientThemeStyleProvidesNativeColorAndCssGradient() {
        ViewerThemeStyle style = ViewerThemeStyle.from(ViewerTheme.gradient());

        TestAssertions.assertEquals("#fbf7f2", style.background, "Gradient theme native background must keep a solid color fallback");
        TestAssertions.assertEquals(
                "linear-gradient(135deg,#fbf7f2 0%,#d7efe7 38%,#f6d7c8 72%,#f8e8aa 100%)",
                style.cssBackground,
                "Gradient theme CSS background must come from the shared theme style");
    }

    public void gradientAndAuroraUseClearlyDifferentBackgrounds() {
        ViewerThemeStyle gradient = ViewerThemeStyle.from(ViewerTheme.gradient());
        ViewerThemeStyle aurora = ViewerThemeStyle.from(ViewerTheme.aurora());

        TestAssertions.assertNotEquals(gradient.background, aurora.background, "Gradient and Aurora must not share the same native background");
        TestAssertions.assertNotEquals(gradient.cssBackground, aurora.cssBackground, "Gradient and Aurora must not share the same CSS gradient");
        TestAssertions.assertNotEquals(gradient.text, aurora.text, "Gradient and Aurora must use different text contrast rules");
    }

    public void darkAndAmoledUseClearlyDifferentSurfaces() {
        ViewerThemeStyle dark = ViewerThemeStyle.from(ViewerTheme.dark());
        ViewerThemeStyle amoled = ViewerThemeStyle.from(ViewerTheme.amoled());

        TestAssertions.assertNotEquals(dark.background, amoled.background, "Dark and AMOLED must not share the same native background");
        TestAssertions.assertNotEquals(dark.surface, amoled.surface, "Dark and AMOLED must not share the same button surface");
        TestAssertions.assertEquals("#000000", amoled.background, "AMOLED must use a pure black native background");
    }

    public void auroraThemeStyleUsesReadableDarkNativeColors() {
        ViewerThemeStyle style = ViewerThemeStyle.from(ViewerTheme.aurora());

        TestAssertions.assertEquals("#10211d", style.surface, "Aurora button surface must be dark enough for light text");
        TestAssertions.assertEquals("#f2fff7", style.text, "Aurora native text must be light enough for dark surfaces");
    }

    public void amoledThemeStyleUsesReadablePureBlackNativeColors() {
        ViewerThemeStyle style = ViewerThemeStyle.from(ViewerTheme.amoled());

        TestAssertions.assertEquals("#050505", style.surface, "AMOLED button surface must stay near pure black");
        TestAssertions.assertEquals("#f7fff9", style.text, "AMOLED native text must be light enough for black surfaces");
    }

    public void darkThemeStyleProvidesSharedTableScrollHint() {
        ViewerThemeStyle style = ViewerThemeStyle.from(ViewerTheme.dark());

        TestAssertions.assertEquals("#8fb8ad", style.tableScrollHint, "Dark theme table scroll hint must come from the shared theme style");
        TestAssertions.assertEquals("0.58", style.tableScrollHintOpacity, "Dark theme table scroll opacity must come from the shared theme style");
    }
}
