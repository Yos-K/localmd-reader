package io.github.yosk.mdlite.viewer;

import io.github.yosk.mdlite.testing.TestAssertions;
import io.github.yosk.mdlite.testing.WcagContrast;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Accessibility fitness gate: every theme must keep text readable (WCAG AA).
 *
 * Measured 2026-06-05 across all 7 themes: every monitored pair already clears
 * 4.5:1 (minimum was mist onPrimary/primary at 4.53), so the gate starts as a
 * hard 4.5 floor with no warning band. When a new theme or token is added, this
 * test fails before an unreadable combination can ship (see issue #68).
 */
public final class ViewerThemeContrastTest {
    private static final double WCAG_AA_BODY_TEXT = 4.5;

    @ParameterizedTest
    @ValueSource(strings = {
        ViewerTheme.LIGHT_VALUE, ViewerTheme.DARK_VALUE, ViewerTheme.AMOLED_VALUE,
        ViewerTheme.GRADIENT_VALUE, ViewerTheme.AURORA_VALUE, ViewerTheme.MIST_VALUE,
        ViewerTheme.DUSK_VALUE
    })
    void bodyTextStaysReadableOnEverySurface(String themeName) {
        ViewerThemeStyle style = styleOf(themeName);
        TestAssertions.assertAtLeast(WCAG_AA_BODY_TEXT,
                WcagContrast.ratio(style.text, style.background),
                readable(themeName, "text on background", style.text, style.background));
        TestAssertions.assertAtLeast(WCAG_AA_BODY_TEXT,
                WcagContrast.ratio(style.text, style.surface),
                readable(themeName, "text on surface", style.text, style.surface));
        TestAssertions.assertAtLeast(WCAG_AA_BODY_TEXT,
                WcagContrast.ratio(style.text, style.tableCellBackground),
                readable(themeName, "text on table cell", style.text, style.tableCellBackground));
        TestAssertions.assertAtLeast(WCAG_AA_BODY_TEXT,
                WcagContrast.ratio(style.text, style.message),
                readable(themeName, "text on message bar", style.text, style.message));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        ViewerTheme.LIGHT_VALUE, ViewerTheme.DARK_VALUE, ViewerTheme.AMOLED_VALUE,
        ViewerTheme.GRADIENT_VALUE, ViewerTheme.AURORA_VALUE, ViewerTheme.MIST_VALUE,
        ViewerTheme.DUSK_VALUE
    })
    void mutedTextStaysReadable(String themeName) {
        ViewerThemeStyle style = styleOf(themeName);
        TestAssertions.assertAtLeast(WCAG_AA_BODY_TEXT,
                WcagContrast.ratio(style.muted, style.background),
                readable(themeName, "muted on background", style.muted, style.background));
        TestAssertions.assertAtLeast(WCAG_AA_BODY_TEXT,
                WcagContrast.ratio(style.muted, style.surface),
                readable(themeName, "muted on surface", style.muted, style.surface));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        ViewerTheme.LIGHT_VALUE, ViewerTheme.DARK_VALUE, ViewerTheme.AMOLED_VALUE,
        ViewerTheme.GRADIENT_VALUE, ViewerTheme.AURORA_VALUE, ViewerTheme.MIST_VALUE,
        ViewerTheme.DUSK_VALUE
    })
    void linksAndAccentTextStayReadable(String themeName) {
        ViewerThemeStyle style = styleOf(themeName);
        TestAssertions.assertAtLeast(WCAG_AA_BODY_TEXT,
                WcagContrast.ratio(style.link, style.background),
                readable(themeName, "link on background", style.link, style.background));
        TestAssertions.assertAtLeast(WCAG_AA_BODY_TEXT,
                WcagContrast.ratio(style.primary, style.background),
                readable(themeName, "primary accent text on background", style.primary, style.background));
        TestAssertions.assertAtLeast(WCAG_AA_BODY_TEXT,
                WcagContrast.ratio(style.primaryStrong, style.background),
                readable(themeName, "strong accent text on background", style.primaryStrong, style.background));
        TestAssertions.assertAtLeast(WCAG_AA_BODY_TEXT,
                WcagContrast.ratio(style.primaryStrong, style.surfaceAlt),
                readable(themeName, "strong accent text on toolbar button", style.primaryStrong, style.surfaceAlt));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        ViewerTheme.LIGHT_VALUE, ViewerTheme.DARK_VALUE, ViewerTheme.AMOLED_VALUE,
        ViewerTheme.GRADIENT_VALUE, ViewerTheme.AURORA_VALUE, ViewerTheme.MIST_VALUE,
        ViewerTheme.DUSK_VALUE
    })
    void highlightedCodeStaysReadable(String themeName) {
        ViewerThemeStyle style = styleOf(themeName);
        TestAssertions.assertAtLeast(WCAG_AA_BODY_TEXT,
                WcagContrast.ratio(style.codeKeyword, style.codeBackground),
                readable(themeName, "code keyword on code background", style.codeKeyword, style.codeBackground));
        TestAssertions.assertAtLeast(WCAG_AA_BODY_TEXT,
                WcagContrast.ratio(style.codeLiteral, style.codeBackground),
                readable(themeName, "code literal on code background", style.codeLiteral, style.codeBackground));
        TestAssertions.assertAtLeast(WCAG_AA_BODY_TEXT,
                WcagContrast.ratio(style.codeString, style.codeBackground),
                readable(themeName, "code string on code background", style.codeString, style.codeBackground));
        TestAssertions.assertAtLeast(WCAG_AA_BODY_TEXT,
                WcagContrast.ratio(style.codeCommand, style.codeBackground),
                readable(themeName, "code command on code background", style.codeCommand, style.codeBackground));
    }

    @ParameterizedTest
    @ValueSource(strings = {
        ViewerTheme.LIGHT_VALUE, ViewerTheme.DARK_VALUE, ViewerTheme.AMOLED_VALUE,
        ViewerTheme.GRADIENT_VALUE, ViewerTheme.AURORA_VALUE, ViewerTheme.MIST_VALUE,
        ViewerTheme.DUSK_VALUE
    })
    void textOnPrimaryStaysReadableForActiveTabAndPrimaryAction(String themeName) {
        ViewerThemeStyle style = styleOf(themeName);
        TestAssertions.assertAtLeast(WCAG_AA_BODY_TEXT,
                WcagContrast.ratio(style.onPrimary, style.primary),
                readable(themeName, "onPrimary on primary (active tab, primary action)",
                        style.onPrimary, style.primary));
    }

    private static ViewerThemeStyle styleOf(String themeName) {
        return ViewerThemeStyle.from(ViewerTheme.fromStoredValue(themeName));
    }

    private static String readable(String themeName, String pairLabel, String foreground, String background) {
        return themeName + " theme: " + pairLabel + " (" + foreground + " on " + background
                + ") must reach WCAG AA 4.5:1";
    }
}
