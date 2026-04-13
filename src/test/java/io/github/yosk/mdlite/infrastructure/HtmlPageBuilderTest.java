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

    private static void assertContains(String actual, String expected, String message) {
        if (!actual.contains(expected)) {
            throw new AssertionError(message + "\nExpected to contain: " + expected + "\nActual: " + actual);
        }
    }
}
