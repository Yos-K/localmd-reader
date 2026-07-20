package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class ViewerThemeStyleTest {

    @Test
    void lightThemeStyleProvidesSharedBackgroundColor() {
        ViewerThemeStyle style = ViewerThemeStyle.from(ViewerTheme.light());

        TestAssertions.assertEquals("#f8fbfa", style.background, "Light theme native background must come from the shared theme style");
        TestAssertions.assertEquals("#f8fbfa", style.cssBackground, "Light theme CSS background must come from the shared theme style");
    }

    @Test
    void gradientThemeStyleProvidesNativeColorAndCssGradient() {
        ViewerThemeStyle style = ViewerThemeStyle.from(ViewerTheme.gradient());

        TestAssertions.assertEquals("#f3f7ff", style.background, "Gradient theme native background must keep a solid color fallback");
        TestAssertions.assertEquals(
                "linear-gradient(135deg,#f3f7ff 0%,#d9f2ec 32%,#ffe2d1 68%,#f8f0ff 100%)",
                style.cssBackground,
                "Gradient theme CSS background must come from the shared theme style");
    }

    @Test
    void gradientAndAuroraUseClearlyDifferentBackgrounds() {
        ViewerThemeStyle gradient = ViewerThemeStyle.from(ViewerTheme.gradient());
        ViewerThemeStyle aurora = ViewerThemeStyle.from(ViewerTheme.aurora());

        TestAssertions.assertNotEquals(gradient.background, aurora.background, "Gradient and Aurora must not share the same native background");
        TestAssertions.assertNotEquals(gradient.cssBackground, aurora.cssBackground, "Gradient and Aurora must not share the same CSS gradient");
        TestAssertions.assertNotEquals(gradient.text, aurora.text, "Gradient and Aurora must use different text contrast rules");
    }

    @Test
    void darkAndAmoledUseClearlyDifferentSurfaces() {
        ViewerThemeStyle dark = ViewerThemeStyle.from(ViewerTheme.dark());
        ViewerThemeStyle amoled = ViewerThemeStyle.from(ViewerTheme.amoled());

        TestAssertions.assertNotEquals(dark.background, amoled.background, "Dark and AMOLED must not share the same native background");
        TestAssertions.assertNotEquals(dark.surface, amoled.surface, "Dark and AMOLED must not share the same button surface");
        TestAssertions.assertEquals("#000000", amoled.background, "AMOLED must use a pure black native background");
    }

    @Test
    void proLightFamilyThemesServeDifferentReadingPurposes() {
        ViewerThemeStyle light = ViewerThemeStyle.from(ViewerTheme.light());
        ViewerThemeStyle gradient = ViewerThemeStyle.from(ViewerTheme.gradient());
        ViewerThemeStyle mist = ViewerThemeStyle.from(ViewerTheme.mist());
        ViewerThemeStyle dusk = ViewerThemeStyle.from(ViewerTheme.dusk());

        TestAssertions.assertNotEquals(light.cssBackground, gradient.cssBackground,
                "Gradient must offer a visible modern background instead of another plain light surface");
        TestAssertions.assertEquals("#111827", mist.primaryStrong,
                "Mist must serve high-contrast reading with an ink-like strong accent");
        TestAssertions.assertEquals("#f4ead7", dusk.background,
                "Dusk must serve sepia long-form reading instead of a near-white light variant");
        TestAssertions.assertNotEquals(gradient.background, mist.background,
                "Gradient and Mist must not collapse into the same light background");
        TestAssertions.assertNotEquals(mist.background, dusk.background,
                "Mist and Dusk must not collapse into the same light background");
    }

    @Test
    void auroraThemeStyleUsesReadableDarkNativeColors() {
        ViewerThemeStyle style = ViewerThemeStyle.from(ViewerTheme.aurora());

        TestAssertions.assertEquals("#10211d", style.surface, "Aurora button surface must be dark enough for light text");
        TestAssertions.assertEquals("#10211d", style.tableCellBackground, "Aurora table cells must hide the page gradient behind table content");
        TestAssertions.assertEquals("#4f7569", style.border, "Aurora borders must be visible against dark table cells");
        TestAssertions.assertEquals("#f2fff7", style.text, "Aurora native text must be light enough for dark surfaces");
    }

    @Test
    void amoledThemeStyleUsesReadablePureBlackNativeColors() {
        ViewerThemeStyle style = ViewerThemeStyle.from(ViewerTheme.amoled());

        TestAssertions.assertEquals("#050505", style.surface, "AMOLED button surface must stay near pure black");
        TestAssertions.assertEquals("#f7fff9", style.text, "AMOLED native text must be light enough for black surfaces");
    }

    @Test
    void darkFamilyThemesUseDarkTextOnBrightPrimary() {
        TestAssertions.assertEquals("#141a1f", ViewerThemeStyle.from(ViewerTheme.dark()).onPrimary,
                "Dark theme text on primary must be dark because its primary teal is bright");
        TestAssertions.assertEquals("#000000", ViewerThemeStyle.from(ViewerTheme.amoled()).onPrimary,
                "AMOLED theme text on primary must be black because its primary green is bright");
        TestAssertions.assertEquals("#081411", ViewerThemeStyle.from(ViewerTheme.aurora()).onPrimary,
                "Aurora theme text on primary must be dark because its primary green is bright");
    }

    @Test
    void lightFamilyThemesUseLightTextOnDarkPrimary() {
        TestAssertions.assertEquals("#f8fbfa", ViewerThemeStyle.from(ViewerTheme.light()).onPrimary,
                "Light theme text on primary must stay light because its primary teal is dark");
        TestAssertions.assertEquals("#fff8eb", ViewerThemeStyle.from(ViewerTheme.dusk()).onPrimary,
                "Dusk theme text on primary must stay light because its primary brown is dark");
    }

    @Test
    void darkBackgroundThemesReportDarkBackground() {
        TestAssertions.assertTrue(ViewerThemeStyle.from(ViewerTheme.dark()).hasDarkBackground(),
                "Dark theme must report a dark background so system bar icons turn light");
        TestAssertions.assertTrue(ViewerThemeStyle.from(ViewerTheme.amoled()).hasDarkBackground(),
                "AMOLED theme must report a dark background so system bar icons turn light");
        TestAssertions.assertTrue(ViewerThemeStyle.from(ViewerTheme.aurora()).hasDarkBackground(),
                "Aurora theme must report a dark background even though it is not in the dark/amoled family");
    }

    @Test
    void lightBackgroundThemesReportLightBackground() {
        TestAssertions.assertFalse(ViewerThemeStyle.from(ViewerTheme.light()).hasDarkBackground(),
                "Light theme must report a light background so system bar icons stay dark");
        TestAssertions.assertFalse(ViewerThemeStyle.from(ViewerTheme.gradient()).hasDarkBackground(),
                "Gradient theme must report a light background so system bar icons stay dark");
        TestAssertions.assertFalse(ViewerThemeStyle.from(ViewerTheme.mist()).hasDarkBackground(),
                "Mist theme must report a light background so system bar icons stay dark");
        TestAssertions.assertFalse(ViewerThemeStyle.from(ViewerTheme.dusk()).hasDarkBackground(),
                "Dusk theme must report a light background so system bar icons stay dark");
    }

    @Test
    void darkThemeStyleProvidesSharedTableScrollHint() {
        ViewerThemeStyle style = ViewerThemeStyle.from(ViewerTheme.dark());

        TestAssertions.assertEquals("#8fb8ad", style.tableScrollHint, "Dark theme table scroll hint must come from the shared theme style");
        TestAssertions.assertEquals("0.58", style.tableScrollHintOpacity, "Dark theme table scroll opacity must come from the shared theme style");
    }

    @Test
    void darkThemeStyleProvidesLightDiagramTextAndLines() {
        ViewerThemeStyle style = ViewerThemeStyle.from(ViewerTheme.dark());

        TestAssertions.assertEquals("#202a31", style.diagramBackground, "Dark Mermaid diagrams must use a dark background matching document surfaces");
        TestAssertions.assertEquals("#f4faf8", style.diagramText, "Dark Mermaid diagrams must use light text");
        TestAssertions.assertEquals("#a9c8c0", style.diagramLine, "Dark Mermaid diagrams must use visible light lines");
    }

    @Test
    void auroraThemeStyleProvidesLightDiagramTextAndLines() {
        ViewerThemeStyle style = ViewerThemeStyle.from(ViewerTheme.aurora());

        TestAssertions.assertEquals("#10211d", style.diagramBackground, "Aurora Mermaid diagrams must use an opaque dark background");
        TestAssertions.assertEquals("#f2fff7", style.diagramText, "Aurora Mermaid diagrams must use light text");
        TestAssertions.assertEquals("#a6d8c0", style.diagramLine, "Aurora Mermaid diagrams must keep lines readable over dark surfaces");
    }
}
