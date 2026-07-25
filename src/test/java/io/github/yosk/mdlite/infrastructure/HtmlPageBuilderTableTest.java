package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.domain.TableReadingMode;
import io.github.yosk.mdlite.testing.TestAssertions;
import io.github.yosk.mdlite.viewer.FontSize;
import io.github.yosk.mdlite.viewer.ViewerTheme;
import org.junit.jupiter.api.Test;

public final class HtmlPageBuilderTableTest {
    @Test
    void stylesTablesForReadableMobileLayout() {
        String page = page(ViewerTheme.light(), TableReadingMode.standard());

        TestAssertions.assertContains(page, ".table-scroll{overflow-x:auto;margin:0 0 16px;",
                "table scroll container must allow horizontal overflow");
        TestAssertions.assertContains(page,
                ".table-scroll::-webkit-scrollbar-thumb{background:#c9d8d5;border-radius:4px;}",
                "table scrollbar thumb must be visible");
        TestAssertions.assertContains(page,
                "table{font-size:var(--localmd-body-font-size);border-collapse:collapse;min-width:max-content;"
                        + "background:#ffffff;}",
                "table CSS must use selected font size and preserve wide content");
        TestAssertions.assertContains(page,
                "th,td{border:1px solid #c9d8d5;padding:6px 8px;text-align:left;background:#ffffff;}",
                "table cells must have readable borders and padding");
    }

    @Test
    void stylesTablesWithVisibleScrollHintInDarkTheme() {
        String page = page(ViewerTheme.dark(), TableReadingMode.standard());

        TestAssertions.assertContains(page, "linear-gradient(to right,rgba(143,184,173,0.58),rgba(143,184,173,0))",
                "dark table scroll hint must be brighter than the dark border");
        TestAssertions.assertContains(page,
                ".table-scroll::-webkit-scrollbar-thumb{background:#8fb8ad;border-radius:4px;}",
                "dark table scrollbar thumb must be visible");
    }

    @Test
    void stylesTablesWithOpaqueCellsInAuroraTheme() {
        String page = page(ViewerTheme.aurora(), TableReadingMode.standard());

        TestAssertions.assertContains(page,
                "table{font-size:var(--localmd-body-font-size);border-collapse:collapse;min-width:max-content;"
                        + "background:#10211d;}",
                "Aurora table must use an opaque cell background");
        TestAssertions.assertContains(page,
                "th,td{border:1px solid #4f7569;padding:6px 8px;text-align:left;background:#10211d;}",
                "Aurora table cell borders must remain visible");
        TestAssertions.assertContains(page,
                ".table-scroll::-webkit-scrollbar-thumb{background:#7ce7b6;border-radius:4px;}",
                "Aurora table scrollbar thumb must be visible");
    }

    @Test
    void standardTableReadingDoesNotMarkTablesAsEnhanced() {
        String page = page(ViewerTheme.light(), TableReadingMode.standard());

        TestAssertions.assertContains(
                page, "<div class=\"table-scroll\"><table>", "standard table reading must keep the Free table class");
        TestAssertions.assertNotContains(page, "<div class=\"table-scroll enhanced-table-reading\"><table>",
                "standard table reading must not mark tables as enhanced");
    }

    @Test
    void enhancedTableReadingKeepsHeaderAndFirstColumnVisibleWhileScrolling() {
        String page = page(ViewerTheme.light(), TableReadingMode.enhanced());

        TestAssertions.assertContains(page, ".table-scroll.enhanced-table-reading th{position:sticky;top:0;z-index:3;",
                "enhanced table reading must keep header cells visible");
        TestAssertions.assertContains(page,
                ".table-scroll.enhanced-table-reading th:first-child,.table-scroll.enhanced-table-reading "
                        + "td:first-child{position:sticky;left:0;z-index:2;",
                "enhanced table reading must keep first column visible");
        TestAssertions.assertContains(page, ".table-scroll.enhanced-table-reading th:first-child{z-index:4;}",
                "top-left header cell must stay above sticky cells");
    }

    private static String page(ViewerTheme theme, TableReadingMode mode) {
        return HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<div class=\"table-scroll\"><table><thead><tr>"
                        + "<th>Name</th></tr></thead><tbody><tr><td>Value</td></tr></tbody></table></div>"),
                theme, FontSize.of(18), mode);
    }
}
