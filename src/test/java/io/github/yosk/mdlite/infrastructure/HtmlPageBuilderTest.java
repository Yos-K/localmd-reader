package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.domain.TableReadingMode;
import io.github.yosk.mdlite.viewer.ViewerTheme;
import io.github.yosk.mdlite.viewer.FontSize;
import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class HtmlPageBuilderTest {

    @Test
    void rendersLightThemeWithLightBackgroundAndDarkText() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<h1>Title</h1>"),
                ViewerTheme.light());

        TestAssertions.assertContains(page, "background:#f8fbfa", "light theme must use light page background");
        TestAssertions.assertContains(page, "color:#172121", "light theme must use dark text");
    }

    @Test
    void bodyCentersOnWideViewports() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<h1>Title</h1>"),
                ViewerTheme.light());

        TestAssertions.assertContains(page,
                "body{font-family:sans-serif;margin:0 auto;",
                "the body must center on wide viewports instead of hugging the left edge (#73)");
        TestAssertions.assertContains(page, "body{max-width:760px;box-sizing:border-box;}",
                "the reading measure must stay capped at 760px");
    }

    @Test
    void headingScaleCoversH3ToH6() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<h3>Sub</h3>"),
                ViewerTheme.light());

        TestAssertions.assertContains(page, "--localmd-h3-font-size:19px;",
                "h3 must scale from the body size (16+3) so the heading rhythm continues past h2 (#73)");
        TestAssertions.assertContains(page, "--localmd-h4-font-size:17px;",
                "h4 must scale from the body size (16+1)");
        TestAssertions.assertContains(page,
                "h3{font-size:var(--localmd-h3-font-size);margin:24px 0 10px;line-height:1.3;}",
                "h3 must have explicit size and margins instead of WebView defaults");
        TestAssertions.assertContains(page,
                "h4{font-size:var(--localmd-h4-font-size);margin:20px 0 8px;line-height:1.35;}",
                "h4 must have explicit size and margins instead of WebView defaults");
        TestAssertions.assertContains(page,
                "h5,h6{font-size:var(--localmd-body-font-size);margin:18px 0 8px;line-height:1.4;}",
                "h5 and h6 must fall back to the body size with consistent margins");
        TestAssertions.assertContains(page, "h6{color:#566664;}",
                "h6 must use the muted text color to mark the lowest heading level");
    }

    @Test
    void fontSizeUpdateScriptKeepsTheWholeHeadingScale() {
        String script = HtmlPageBuilder.fontSizeUpdateScript(FontSize.of(20));

        TestAssertions.assertContains(script, "'--localmd-h3-font-size','23px'",
                "pinch zoom must rescale h3 along with the body (#73)");
        TestAssertions.assertContains(script, "'--localmd-h4-font-size','21px'",
                "pinch zoom must rescale h4 along with the body");
    }

    @Test
    void rendersDarkThemeWithDarkBackgroundAndLightText() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<h1>Title</h1>"),
                ViewerTheme.dark());

        TestAssertions.assertContains(page, "background:#141a1f", "dark theme must use dark page background");
        TestAssertions.assertContains(page, "color:#f4faf8", "dark theme must use light text");
    }

    @Test
    void rendersAmoledThemeWithBlackBackgroundAndLightText() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<h1>Title</h1>"),
                ViewerTheme.amoled());

        TestAssertions.assertContains(page, "background:#000000", "AMOLED theme must use a true black page background");
        TestAssertions.assertContains(page, "color:#f7fff9", "AMOLED theme must keep high-contrast light text");
    }

    @Test
    void rendersGradientThemeWithModernReadableBackground() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<h1>Title</h1>"),
                ViewerTheme.gradient());

        TestAssertions.assertContains(page, "background:linear-gradient(135deg,#f3f7ff 0%,#d9f2ec 32%,#ffe2d1 68%,#f8f0ff 100%)", "Gradient theme must use a distinct modern reading background");
        TestAssertions.assertContains(page, "color:#142032", "Gradient theme must keep dark readable text");
    }

    @Test
    void rendersAuroraThemeWithModernReadableBackground() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<h1>Title</h1>"),
                ViewerTheme.aurora());

        TestAssertions.assertContains(page, "background:linear-gradient(135deg,#081411 0%,#123d36 34%,#0a5c58 64%,#3a5b2a 100%)", "Aurora theme must use a distinct dark aurora reading background");
        TestAssertions.assertContains(page, "color:#f2fff7", "Aurora theme must keep light readable text");
    }

    @Test
    void rendersMistThemeWithModernReadableBackground() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<h1>Title</h1>"),
                ViewerTheme.mist());

        TestAssertions.assertContains(page, "background:#ffffff", "Mist theme must use a high-contrast plain reading background");
        TestAssertions.assertContains(page, "color:#050505", "Mist theme must keep ink-like readable text");
    }

    @Test
    void rendersDuskThemeWithModernReadableBackground() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<h1>Title</h1>"),
                ViewerTheme.dusk());

        TestAssertions.assertContains(page, "background:linear-gradient(135deg,#f4ead7 0%,#ead8b8 52%,#f8f0df 100%)", "Dusk theme must use a sepia long-form reading background");
        TestAssertions.assertContains(page, "color:#2a2118", "Dusk theme must keep dark readable text");
    }

    @Test
    void addsBottomReadingSpaceAfterLastLine() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<p>Last line</p>"),
                ViewerTheme.light());

        TestAssertions.assertContains(page, "padding:24px 24px 40vh;", "document body must add bottom reading space so the final line can scroll above the screen bottom");
    }

    @Test
    void welcomePrimaryActionUsesSolidTextColorOnGradientThemes() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<section class=\"welcome\"><a class=\"welcome-primary-action\">Open Markdown file</a></section>"),
                ViewerTheme.gradient());

        TestAssertions.assertContains(page, ".welcome-primary-action{display:block;box-sizing:border-box;width:100%;background:#005f73;color:#ffffff;", "welcome primary action must use a readable solid text color");
        TestAssertions.assertNotContains(page, ".welcome-primary-action{display:block;box-sizing:border-box;width:100%;background:#005f73;color:linear-gradient", "welcome primary action text color must not use CSS gradients");
    }

    @Test
    void includesRenderedMarkdownBodyUnchanged() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<h1>Title</h1><p>Body</p>"),
                ViewerTheme.light());

        TestAssertions.assertContains(page, "<h1>Title</h1><p>Body</p>", "page must include rendered Markdown body unchanged");
    }

    @Test
    void appliesFontSizeToParagraphText() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<p>Body</p>"),
                ViewerTheme.light(),
                FontSize.of(18));

        TestAssertions.assertContains(page, "--localmd-body-font-size:18px;", "page CSS variables must use selected paragraph font size");
        TestAssertions.assertContains(page, "p{font-size:var(--localmd-body-font-size)", "paragraph CSS must follow the live font size variable");
    }

    @Test
    void appliesFontSizeToHeadings() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<h1>Title</h1><h2>Section</h2>"),
                ViewerTheme.light(),
                FontSize.of(18));

        TestAssertions.assertContains(page, "--localmd-h1-font-size:26px;", "h1 CSS variable must scale from selected font size");
        TestAssertions.assertContains(page, "--localmd-h2-font-size:23px;", "h2 CSS variable must scale from selected font size");
        TestAssertions.assertContains(page, "h1{font-size:var(--localmd-h1-font-size)", "h1 CSS must follow the live font size variable");
        TestAssertions.assertContains(page, "h2{font-size:var(--localmd-h2-font-size)", "h2 CSS must follow the live font size variable");
    }

    @Test
    void providesFontSizeUpdateScriptForPinchWithoutReloading() {
        String script = HtmlPageBuilder.fontSizeUpdateScript(FontSize.of(24));

        TestAssertions.assertContains(script, "s.setProperty('--localmd-body-font-size','24px');", "pinch font updates must update paragraph size without reloading the page");
        TestAssertions.assertContains(script, "s.setProperty('--localmd-h1-font-size','32px');", "pinch font updates must update heading size without reloading the page");
        TestAssertions.assertContains(script, "s.setProperty('--localmd-diagram-scale','1.500');", "pinch font updates must update Mermaid diagram scale without reloading the page");
    }

    @Test
    void appliesFontSizeScaleToMermaidDiagrams() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<div class=\"mermaid-diagram\"><svg></svg></div>"),
                ViewerTheme.light(),
                FontSize.of(24));

        TestAssertions.assertContains(page, "--localmd-diagram-scale:1.500;", "Mermaid diagram scale must follow selected font size");
        TestAssertions.assertContains(page, ".mermaid-diagram-scale{display:inline-block;min-width:100%;transform:scale(var(--localmd-diagram-scale));transform-origin:top left;}", "Every Mermaid diagram wrapper must scale with the diagram CSS variable");
        TestAssertions.assertContains(page, ".mermaid-diagram-scale svg{max-width:none;height:auto;display:block;}", "Mermaid SVG must keep its intrinsic size inside the scalable wrapper");
        TestAssertions.assertContains(page, ".mermaid-diagram-scale svg,.mermaid-diagram-scale svg *{font-size:16px;}", "Mermaid SVG internal text must not inherit the page font size before scaling");
    }

    @Test
    void stylesLinksWithThemeReadableColor() {
        String lightPage = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<p><a href=\"https://example.com\">Link</a></p>"),
                ViewerTheme.light(),
                FontSize.of(18));
        String darkPage = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<p><a href=\"https://example.com\">Link</a></p>"),
                ViewerTheme.dark(),
                FontSize.of(18));

        TestAssertions.assertContains(lightPage, "a{color:#0b6f87;text-decoration:underline;}", "light theme links must be visibly styled");
        TestAssertions.assertContains(darkPage, "a{color:#8bd7ff;text-decoration:underline;}", "dark theme links must be visibly styled");
    }

    @Test
    void stylesChecklistForReadableMobileLayout() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<ul class=\"checklist\"><li><input type=\"checkbox\" disabled> Done</li></ul>"),
                ViewerTheme.light(),
                FontSize.of(18));

        TestAssertions.assertContains(page, "ul.checklist{list-style:none;padding-left:0;}", "checklist CSS must remove duplicate bullets and excess left padding");
        TestAssertions.assertContains(page, "ul.checklist input{margin-right:8px;}", "checklist checkbox must keep readable spacing from text");
    }

    @Test
    void stylesTablesForReadableMobileLayout() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<div class=\"table-scroll\"><table><thead><tr><th>Name</th></tr></thead><tbody><tr><td>Value</td></tr></tbody></table></div>"),
                ViewerTheme.light(),
                FontSize.of(18));

        TestAssertions.assertContains(page, ".table-scroll{overflow-x:auto;margin:0 0 16px;background:linear-gradient(to right,#f8fbfa 30%,rgba(248,251,250,0)),linear-gradient(to right,rgba(248,251,250,0),#f8fbfa 70%) 100% 0,linear-gradient(to right,rgba(201,216,213,0.45),rgba(201,216,213,0)),linear-gradient(to left,rgba(201,216,213,0.45),rgba(201,216,213,0)) 100% 0;background-repeat:no-repeat;background-size:32px 100%,32px 100%,16px 100%,16px 100%;background-attachment:local,local,scroll,scroll;}", "table scroll container must hint overflow without looking scrollable when the table fits");
        TestAssertions.assertContains(page, ".table-scroll::-webkit-scrollbar-thumb{background:#c9d8d5;border-radius:4px;}", "table scrollbar thumb must be visible");
        TestAssertions.assertContains(page, "table{font-size:var(--localmd-body-font-size);border-collapse:collapse;min-width:max-content;background:#ffffff;}", "table CSS must use selected font size variable and preserve wide content");
        TestAssertions.assertContains(page, "th,td{border:1px solid #c9d8d5;padding:6px 8px;text-align:left;background:#ffffff;}", "table cells must have readable borders and padding");
    }

    @Test
    void stylesTablesWithVisibleScrollHintInDarkTheme() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<div class=\"table-scroll\"><table><thead><tr><th>Name</th></tr></thead><tbody><tr><td>Value</td></tr></tbody></table></div>"),
                ViewerTheme.dark(),
                FontSize.of(18));

        TestAssertions.assertContains(page, "linear-gradient(to right,rgba(143,184,173,0.58),rgba(143,184,173,0))", "dark table scroll hint must be brighter than the dark border");
        TestAssertions.assertContains(page, ".table-scroll::-webkit-scrollbar-thumb{background:#8fb8ad;border-radius:4px;}", "dark table scrollbar thumb must be visible");
    }

    @Test
    void stylesTablesWithOpaqueCellsInAuroraTheme() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<div class=\"table-scroll\"><table><thead><tr><th>Name</th></tr></thead><tbody><tr><td>Value</td></tr></tbody></table></div>"),
                ViewerTheme.aurora(),
                FontSize.of(18));

        TestAssertions.assertContains(page, "table{font-size:var(--localmd-body-font-size);border-collapse:collapse;min-width:max-content;background:#10211d;}", "Aurora table must use an opaque cell background");
        TestAssertions.assertContains(page, "th,td{border:1px solid #4f7569;padding:6px 8px;text-align:left;background:#10211d;}", "Aurora table cell borders must remain visible against the dark cell background");
        TestAssertions.assertContains(page, ".table-scroll::-webkit-scrollbar-thumb{background:#7ce7b6;border-radius:4px;}", "Aurora table scrollbar thumb must be visible");
    }

    @Test
    void standardTableReadingDoesNotMarkTablesAsEnhanced() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<div class=\"table-scroll\"><table><thead><tr><th>Name</th></tr></thead><tbody><tr><td>Value</td></tr></tbody></table></div>"),
                ViewerTheme.light(),
                FontSize.of(18),
                TableReadingMode.standard());

        TestAssertions.assertContains(page, "<div class=\"table-scroll\"><table>", "standard table reading must keep the Free table class");
        TestAssertions.assertNotContains(page, "<div class=\"table-scroll enhanced-table-reading\"><table>", "standard table reading must not mark tables as enhanced");
    }

    @Test
    void enhancedTableReadingKeepsHeaderAndFirstColumnVisibleWhileScrolling() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<div class=\"table-scroll\"><table><thead><tr><th>Name</th></tr></thead><tbody><tr><td>Value</td></tr></tbody></table></div>"),
                ViewerTheme.light(),
                FontSize.of(18),
                TableReadingMode.enhanced());

        TestAssertions.assertContains(page, ".table-scroll.enhanced-table-reading th{position:sticky;top:0;z-index:3;", "enhanced table reading must keep header cells visible");
        TestAssertions.assertContains(page, ".table-scroll.enhanced-table-reading th:first-child,.table-scroll.enhanced-table-reading td:first-child{position:sticky;left:0;z-index:2;", "enhanced table reading must keep first column visible");
        TestAssertions.assertContains(page, ".table-scroll.enhanced-table-reading th:first-child{z-index:4;}", "top-left header cell must stay above sticky header and first column cells");
    }

    @Test
    void stylesWelcomeHomeForFirstRun() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<section class=\"welcome\"><p class=\"welcome-kicker\">Local Markdown reader</p></section>"),
                ViewerTheme.light(),
                FontSize.of(18));

        TestAssertions.assertContains(page, ".welcome-grid{display:grid;gap:10px;margin:0 0 18px;}", "welcome home must lay out guidance as a stable grid");
        TestAssertions.assertContains(page, ".welcome-card{background:#ffffff;border:1px solid #c9d8d5;border-radius:8px;padding:14px;}", "welcome cards must use the theme surface");
        TestAssertions.assertContains(page, ".welcome-note{background:#eef5f3;border-left:4px solid #006d77;padding:10px 12px;margin:0;color:#566664;}", "welcome note must use a readable accent treatment");
    }
}
