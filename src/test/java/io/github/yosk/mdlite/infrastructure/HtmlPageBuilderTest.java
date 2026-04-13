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
        test.stylesChecklistForReadableMobileLayout();
        test.stylesTablesForReadableMobileLayout();
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

        assertContains(page, ".table-scroll{overflow-x:auto;margin:0 0 16px;border:1px solid #c9d8d5;border-radius:4px;box-shadow:inset -16px 0 12px -12px #c9d8d5;}", "table scroll container must provide a visible horizontal scroll hint");
        assertContains(page, "table{font-size:18px;border-collapse:collapse;min-width:max-content;}", "table CSS must use selected font size and preserve wide content");
        assertContains(page, "th,td{border:1px solid #c9d8d5;padding:6px 8px;text-align:left;}", "table cells must have readable borders and padding");
    }

    private static void assertContains(String actual, String expected, String message) {
        if (!actual.contains(expected)) {
            throw new AssertionError(message + "\nExpected to contain: " + expected + "\nActual: " + actual);
        }
    }
}
