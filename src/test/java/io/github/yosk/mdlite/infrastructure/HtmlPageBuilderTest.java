package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.SafeHtml;
import io.github.yosk.mdlite.domain.ViewerTheme;
import io.github.yosk.mdlite.domain.FontSize;

public final class HtmlPageBuilderTest {
    public static void main(String[] args) {
        HtmlPageBuilderTest test = new HtmlPageBuilderTest();
        test.rendersLightThemeWithLightBackgroundAndDarkText();
        test.rendersDarkThemeWithDarkBackgroundAndLightText();
        test.includesRenderedMarkdownBodyUnchanged();
        test.appliesFontSizeToParagraphText();
        test.appliesFontSizeToHeadings();
        test.stylesLinksWithThemeReadableColor();
        test.stylesChecklistForReadableMobileLayout();
        test.stylesTablesForReadableMobileLayout();
        test.stylesTablesWithVisibleScrollHintInDarkTheme();
        test.stylesWelcomeHomeForFirstRun();
    }

    public void rendersLightThemeWithLightBackgroundAndDarkText() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<h1>Title</h1>"),
                ViewerTheme.light());

        assertContains(page, "background:#f8fbfa", "light theme must use light page background");
        assertContains(page, "color:#172121", "light theme must use dark text");
    }

    public void rendersDarkThemeWithDarkBackgroundAndLightText() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<h1>Title</h1>"),
                ViewerTheme.dark());

        assertContains(page, "background:#101414", "dark theme must use dark page background");
        assertContains(page, "color:#edf5f2", "dark theme must use light text");
    }

    public void includesRenderedMarkdownBodyUnchanged() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<h1>Title</h1><p>Body</p>"),
                ViewerTheme.light());

        assertContains(page, "<h1>Title</h1><p>Body</p>", "page must include rendered Markdown body unchanged");
    }

    public void appliesFontSizeToParagraphText() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<p>Body</p>"),
                ViewerTheme.light(),
                FontSize.of(18));

        assertContains(page, "p{font-size:18px", "paragraph CSS must use selected font size");
    }

    public void appliesFontSizeToHeadings() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<h1>Title</h1><h2>Section</h2>"),
                ViewerTheme.light(),
                FontSize.of(18));

        assertContains(page, "h1{font-size:26px", "h1 CSS must scale from selected font size");
        assertContains(page, "h2{font-size:23px", "h2 CSS must scale from selected font size");
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

        assertContains(lightPage, "a{color:#0b6f87;text-decoration:underline;}", "light theme links must be visibly styled");
        assertContains(darkPage, "a{color:#7ccbe0;text-decoration:underline;}", "dark theme links must be visibly styled");
    }

    public void stylesChecklistForReadableMobileLayout() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<ul class=\"checklist\"><li><input type=\"checkbox\" disabled> Done</li></ul>"),
                ViewerTheme.light(),
                FontSize.of(18));

        assertContains(page, "ul.checklist{list-style:none;padding-left:0;}", "checklist CSS must remove duplicate bullets and excess left padding");
        assertContains(page, "ul.checklist input{margin-right:8px;}", "checklist checkbox must keep readable spacing from text");
    }

    public void stylesTablesForReadableMobileLayout() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<div class=\"table-scroll\"><table><thead><tr><th>Name</th></tr></thead><tbody><tr><td>Value</td></tr></tbody></table></div>"),
                ViewerTheme.light(),
                FontSize.of(18));

        assertContains(page, ".table-scroll{overflow-x:auto;margin:0 0 16px;background:linear-gradient(to right,#f8fbfa 30%,rgba(248,251,250,0)),linear-gradient(to right,rgba(248,251,250,0),#f8fbfa 70%) 100% 0,linear-gradient(to right,rgba(201,216,213,0.45),rgba(201,216,213,0)),linear-gradient(to left,rgba(201,216,213,0.45),rgba(201,216,213,0)) 100% 0;background-repeat:no-repeat;background-size:32px 100%,32px 100%,16px 100%,16px 100%;background-attachment:local,local,scroll,scroll;}", "table scroll container must hint overflow without looking scrollable when the table fits");
        assertContains(page, ".table-scroll::-webkit-scrollbar-thumb{background:#c9d8d5;border-radius:4px;}", "table scrollbar thumb must be visible");
        assertContains(page, "table{font-size:18px;border-collapse:collapse;min-width:max-content;}", "table CSS must use selected font size and preserve wide content");
        assertContains(page, "th,td{border:1px solid #c9d8d5;padding:6px 8px;text-align:left;}", "table cells must have readable borders and padding");
    }

    public void stylesTablesWithVisibleScrollHintInDarkTheme() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<div class=\"table-scroll\"><table><thead><tr><th>Name</th></tr></thead><tbody><tr><td>Value</td></tr></tbody></table></div>"),
                ViewerTheme.dark(),
                FontSize.of(18));

        assertContains(page, "linear-gradient(to right,rgba(128,168,161,0.55),rgba(128,168,161,0))", "dark table scroll hint must be brighter than the dark border");
        assertContains(page, ".table-scroll::-webkit-scrollbar-thumb{background:#80a8a1;border-radius:4px;}", "dark table scrollbar thumb must be visible");
    }

    public void stylesWelcomeHomeForFirstRun() {
        String page = HtmlPageBuilder.buildPage(
                SafeHtml.fromTrustedRendererOutput("<section class=\"welcome\"><p class=\"welcome-kicker\">Local Markdown reader</p></section>"),
                ViewerTheme.light(),
                FontSize.of(18));

        assertContains(page, ".welcome-grid{display:grid;gap:10px;margin:0 0 18px;}", "welcome home must lay out guidance as a stable grid");
        assertContains(page, ".welcome-card{background:#ffffff;border:1px solid #c9d8d5;border-radius:8px;padding:14px;}", "welcome cards must use the theme surface");
        assertContains(page, ".welcome-note{background:#eef5f3;border-left:4px solid #006d77;padding:10px 12px;margin:0;color:#566664;}", "welcome note must use a readable accent treatment");
    }

    private static void assertContains(String actual, String expected, String message) {
        if (!actual.contains(expected)) {
            throw new AssertionError(message + "\nExpected to contain: " + expected + "\nActual: " + actual);
        }
    }
}
