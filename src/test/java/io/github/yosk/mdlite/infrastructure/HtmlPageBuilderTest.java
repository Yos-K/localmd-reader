package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.viewer.ViewerTheme;
import io.github.yosk.mdlite.viewer.FontSize;
import io.github.yosk.mdlite.testing.TestAssertions;

public final class HtmlPageBuilderTest {
    public static void main(String[] args) {
        HtmlPageBuilderTest test = new HtmlPageBuilderTest();
        test.rendersLightThemeWithLightBackgroundAndDarkText();
        test.rendersDarkThemeWithDarkBackgroundAndLightText();
        test.rendersAmoledThemeWithBlackBackgroundAndLightText();
        test.rendersGradientThemeWithModernReadableBackground();
        test.rendersAuroraThemeWithModernReadableBackground();
        test.rendersMistThemeWithModernReadableBackground();
        test.rendersDuskThemeWithModernReadableBackground();
        test.welcomePrimaryActionUsesSolidTextColorOnGradientThemes();
        test.includesRenderedMarkdownBodyUnchanged();
        test.appliesFontSizeToParagraphText();
        test.appliesFontSizeToHeadings();
        test.stylesLinksWithThemeReadableColor();
        test.stylesChecklistForReadableMobileLayout();
        test.stylesTablesForReadableMobileLayout();
        test.stylesTablesWithVisibleScrollHintInDarkTheme();
        test.stylesTablesWithOpaqueCellsInAuroraTheme();
        test.stylesWelcomeHomeForFirstRun();
    }

    public void rendersLightThemeWithLightBackgroundAndDarkText() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<h1>Title</h1>"),
                ViewerTheme.light());

        TestAssertions.assertContains(page, "background:#f8fbfa", "light theme must use light page background");
        TestAssertions.assertContains(page, "color:#172121", "light theme must use dark text");
    }

    public void rendersDarkThemeWithDarkBackgroundAndLightText() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<h1>Title</h1>"),
                ViewerTheme.dark());

        TestAssertions.assertContains(page, "background:#141a1f", "dark theme must use dark page background");
        TestAssertions.assertContains(page, "color:#f4faf8", "dark theme must use light text");
    }

    public void rendersAmoledThemeWithBlackBackgroundAndLightText() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<h1>Title</h1>"),
                ViewerTheme.amoled());

        TestAssertions.assertContains(page, "background:#000000", "AMOLED theme must use a true black page background");
        TestAssertions.assertContains(page, "color:#f7fff9", "AMOLED theme must keep high-contrast light text");
    }

    public void rendersGradientThemeWithModernReadableBackground() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<h1>Title</h1>"),
                ViewerTheme.gradient());

        TestAssertions.assertContains(page, "background:linear-gradient(135deg,#f7fbf9 0%,#dceee9 36%,#f2e7dc 70%,#f7f1e7 100%)", "Gradient theme must use a modern non-purple reading background");
        TestAssertions.assertContains(page, "color:#172121", "Gradient theme must keep dark readable text");
    }

    public void rendersAuroraThemeWithModernReadableBackground() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<h1>Title</h1>"),
                ViewerTheme.aurora());

        TestAssertions.assertContains(page, "background:linear-gradient(135deg,#081411 0%,#123d36 34%,#0a5c58 64%,#3a5b2a 100%)", "Aurora theme must use a distinct dark aurora reading background");
        TestAssertions.assertContains(page, "color:#f2fff7", "Aurora theme must keep light readable text");
    }

    public void rendersMistThemeWithModernReadableBackground() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<h1>Title</h1>"),
                ViewerTheme.mist());

        TestAssertions.assertContains(page, "background:linear-gradient(135deg,#f3f8f7 0%,#e4efed 50%,#f7f4ef 100%)", "Mist theme must use a calm modern reading background");
        TestAssertions.assertContains(page, "color:#1c2524", "Mist theme must keep dark readable text");
    }

    public void rendersDuskThemeWithModernReadableBackground() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<h1>Title</h1>"),
                ViewerTheme.dusk());

        TestAssertions.assertContains(page, "background:linear-gradient(135deg,#fbf6f3 0%,#efe3dc 45%,#dbe9e4 100%)", "Dusk theme must use a warm modern reading background");
        TestAssertions.assertContains(page, "color:#241d1b", "Dusk theme must keep dark readable text");
    }

    public void welcomePrimaryActionUsesSolidTextColorOnGradientThemes() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<section class=\"welcome\"><a class=\"welcome-primary-action\">Open Markdown file</a></section>"),
                ViewerTheme.gradient());

        TestAssertions.assertContains(page, ".welcome-primary-action{display:block;box-sizing:border-box;width:100%;background:#0d756d;color:#f7fbf9;", "welcome primary action must use a readable solid text color");
        TestAssertions.assertNotContains(page, ".welcome-primary-action{display:block;box-sizing:border-box;width:100%;background:#0d756d;color:linear-gradient", "welcome primary action text color must not use CSS gradients");
    }

    public void includesRenderedMarkdownBodyUnchanged() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<h1>Title</h1><p>Body</p>"),
                ViewerTheme.light());

        TestAssertions.assertContains(page, "<h1>Title</h1><p>Body</p>", "page must include rendered Markdown body unchanged");
    }

    public void appliesFontSizeToParagraphText() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<p>Body</p>"),
                ViewerTheme.light(),
                FontSize.of(18));

        TestAssertions.assertContains(page, "p{font-size:18px", "paragraph CSS must use selected font size");
    }

    public void appliesFontSizeToHeadings() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<h1>Title</h1><h2>Section</h2>"),
                ViewerTheme.light(),
                FontSize.of(18));

        TestAssertions.assertContains(page, "h1{font-size:26px", "h1 CSS must scale from selected font size");
        TestAssertions.assertContains(page, "h2{font-size:23px", "h2 CSS must scale from selected font size");
    }

    public void stylesLinksWithThemeReadableColor() {
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

    public void stylesChecklistForReadableMobileLayout() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<ul class=\"checklist\"><li><input type=\"checkbox\" disabled> Done</li></ul>"),
                ViewerTheme.light(),
                FontSize.of(18));

        TestAssertions.assertContains(page, "ul.checklist{list-style:none;padding-left:0;}", "checklist CSS must remove duplicate bullets and excess left padding");
        TestAssertions.assertContains(page, "ul.checklist input{margin-right:8px;}", "checklist checkbox must keep readable spacing from text");
    }

    public void stylesTablesForReadableMobileLayout() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<div class=\"table-scroll\"><table><thead><tr><th>Name</th></tr></thead><tbody><tr><td>Value</td></tr></tbody></table></div>"),
                ViewerTheme.light(),
                FontSize.of(18));

        TestAssertions.assertContains(page, ".table-scroll{overflow-x:auto;margin:0 0 16px;background:linear-gradient(to right,#f8fbfa 30%,rgba(248,251,250,0)),linear-gradient(to right,rgba(248,251,250,0),#f8fbfa 70%) 100% 0,linear-gradient(to right,rgba(201,216,213,0.45),rgba(201,216,213,0)),linear-gradient(to left,rgba(201,216,213,0.45),rgba(201,216,213,0)) 100% 0;background-repeat:no-repeat;background-size:32px 100%,32px 100%,16px 100%,16px 100%;background-attachment:local,local,scroll,scroll;}", "table scroll container must hint overflow without looking scrollable when the table fits");
        TestAssertions.assertContains(page, ".table-scroll::-webkit-scrollbar-thumb{background:#c9d8d5;border-radius:4px;}", "table scrollbar thumb must be visible");
        TestAssertions.assertContains(page, "table{font-size:18px;border-collapse:collapse;min-width:max-content;background:#ffffff;}", "table CSS must use selected font size and preserve wide content");
        TestAssertions.assertContains(page, "th,td{border:1px solid #c9d8d5;padding:6px 8px;text-align:left;background:#ffffff;}", "table cells must have readable borders and padding");
    }

    public void stylesTablesWithVisibleScrollHintInDarkTheme() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<div class=\"table-scroll\"><table><thead><tr><th>Name</th></tr></thead><tbody><tr><td>Value</td></tr></tbody></table></div>"),
                ViewerTheme.dark(),
                FontSize.of(18));

        TestAssertions.assertContains(page, "linear-gradient(to right,rgba(143,184,173,0.58),rgba(143,184,173,0))", "dark table scroll hint must be brighter than the dark border");
        TestAssertions.assertContains(page, ".table-scroll::-webkit-scrollbar-thumb{background:#8fb8ad;border-radius:4px;}", "dark table scrollbar thumb must be visible");
    }

    public void stylesTablesWithOpaqueCellsInAuroraTheme() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<div class=\"table-scroll\"><table><thead><tr><th>Name</th></tr></thead><tbody><tr><td>Value</td></tr></tbody></table></div>"),
                ViewerTheme.aurora(),
                FontSize.of(18));

        TestAssertions.assertContains(page, "table{font-size:18px;border-collapse:collapse;min-width:max-content;background:#10211d;}", "Aurora table must use an opaque cell background");
        TestAssertions.assertContains(page, "th,td{border:1px solid #4f7569;padding:6px 8px;text-align:left;background:#10211d;}", "Aurora table cell borders must remain visible against the dark cell background");
        TestAssertions.assertContains(page, ".table-scroll::-webkit-scrollbar-thumb{background:#7ce7b6;border-radius:4px;}", "Aurora table scrollbar thumb must be visible");
    }

    public void stylesWelcomeHomeForFirstRun() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<section class=\"welcome\"><p class=\"welcome-kicker\">Local Markdown reader</p></section>"),
                ViewerTheme.light(),
                FontSize.of(18));

        TestAssertions.assertContains(page, ".welcome-grid{display:grid;gap:10px;margin:0 0 18px;}", "welcome home must lay out guidance as a stable grid");
        TestAssertions.assertContains(page, ".welcome-card{background:#ffffff;border:1px solid #c9d8d5;border-radius:8px;padding:14px;}", "welcome cards must use the theme surface");
        TestAssertions.assertContains(page, ".welcome-note{background:#eef5f3;border-left:4px solid #006d77;padding:10px 12px;margin:0;color:#566664;}", "welcome note must use a readable accent treatment");
    }
}
