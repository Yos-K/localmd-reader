package io.github.yosk.mdlite.infrastructure;

import io.github.yosk.mdlite.domain.SafeHtml;

public final class JavaSimpleMarkdownRendererTest {
    public static void main(String[] args) {
        JavaSimpleMarkdownRendererTest test = new JavaSimpleMarkdownRendererTest();
        test.escapesRawHtmlTagsInMarkdownText();
        test.doesNotEmitRawScriptTags();
        test.escapesAmpersandsBeforeAngleBrackets();
        test.rendersLevelOneHeadingAsH1();
        test.rendersLevelTwoHeadingAsH2();
        test.rendersPlainTextAsParagraph();
        test.rendersFencedCodeBlockWithEscapedContent();
        test.rendersFencedCodeBlockWithLanguageInfoAsCodeBlock();
        test.rendersSafeLanguageClassForFencedCodeBlock();
        test.doesNotRenderUnsafeLanguageClassForFencedCodeBlock();
        test.rendersInlineCodeWithEscapedContent();
        test.rendersHttpsMarkdownLinkAsSafeAnchor();
        test.rendersHttpMarkdownLinkAsSafeAnchor();
        test.doesNotRenderJavascriptMarkdownLinkAsAnchor();
        test.rendersBulletListItemsAsUnorderedList();
        test.rendersUncheckedChecklistItemAsDisabledCheckboxInList();
        test.rendersCheckedChecklistItemAsDisabledCheckedCheckboxInList();
        test.rendersNumberedListItemsAsOrderedList();
        test.rendersPipeTableWithHeaderAndBodyRows();
        test.escapesPipeTableCellContentAndRendersInlineCode();
        test.rendersBlockquoteAsBlockquote();
        test.rendersHorizontalRule();
        test.rendererDoesNotCrashOnEmptyInput();
        test.rendererDoesNotCrashOnNullInput();
        test.rendererClosesUnterminatedCodeFenceInsteadOfCrashing();
        test.rendererDoesNotCrashOnGeneratedMixedMarkdownText();
    }

    private final JavaSimpleMarkdownRenderer renderer = new JavaSimpleMarkdownRenderer();

    public void escapesRawHtmlTagsInMarkdownText() {
        SafeHtml html = renderer.render("<b>hello</b>");

        assertContains(html.value(), "&lt;b&gt;hello&lt;/b&gt;", "raw HTML tags must be escaped as text");
        assertNotContains(html.value(), "<b>hello</b>", "raw HTML must not be emitted");
    }

    public void doesNotEmitRawScriptTags() {
        SafeHtml html = renderer.render("<script>alert(1)</script>");

        assertContains(html.value(), "&lt;script&gt;alert(1)&lt;/script&gt;", "script tags must be escaped");
        assertNotContains(html.value(), "<script>", "raw script start tag must not be emitted");
        assertNotContains(html.value(), "</script>", "raw script end tag must not be emitted");
    }

    public void escapesAmpersandsBeforeAngleBrackets() {
        SafeHtml html = renderer.render("A & B < C");

        assertContains(html.value(), "A &amp; B &lt; C", "ampersands and angle brackets must be escaped");
        assertNotContains(html.value(), "A & B &lt; C", "raw ampersand must not remain before escaped angle bracket");
    }

    public void rendersLevelOneHeadingAsH1() {
        SafeHtml html = renderer.render("# Title");

        assertContains(html.value(), "<h1>Title</h1>", "level 1 heading must render as h1");
    }

    public void rendersLevelTwoHeadingAsH2() {
        SafeHtml html = renderer.render("## Implementation Domain Model");

        assertContains(html.value(), "<h2>Implementation Domain Model</h2>", "level 2 heading must render as h2");
    }

    public void rendersPlainTextAsParagraph() {
        SafeHtml html = renderer.render("Plain text");

        assertContains(html.value(), "<p>Plain text</p>", "plain text must render as paragraph");
    }

    public void rendersFencedCodeBlockWithEscapedContent() {
        SafeHtml html = renderer.render("```\nif (a < b) {\n  return true;\n}\n```");

        assertContains(html.value(), "<pre><code>", "fenced code block must render as pre/code");
        assertContains(html.value(), "if (a &lt; b) {", "code block content must be escaped");
        assertContains(html.value(), "</code></pre>", "fenced code block must close pre/code");
    }

    public void rendersFencedCodeBlockWithLanguageInfoAsCodeBlock() {
        SafeHtml html = renderer.render("```text\ndata MarkdownFile = ReadableMarkdownFile OR UnsupportedFile\n```");

        assertContains(html.value(), "<pre><code", "fenced code block with language info must render as pre/code");
        assertContains(html.value(), "data MarkdownFile = ReadableMarkdownFile OR UnsupportedFile", "fenced code block content must be preserved");
        assertNotContains(html.value(), "<p>```text", "language fence marker must not render as paragraph");
    }

    public void rendersSafeLanguageClassForFencedCodeBlock() {
        SafeHtml html = renderer.render("```java\npublic final class Note {}\n```");

        assertContains(html.value(), "<pre><code class=\"language-java\">", "safe language info must render as a language class");
        assertContains(html.value(), "public final class Note {}", "code content must be preserved");
    }

    public void doesNotRenderUnsafeLanguageClassForFencedCodeBlock() {
        SafeHtml html = renderer.render("```java\" onclick=\"alert(1)\nvalue\n```");

        assertContains(html.value(), "<pre><code>", "unsafe language info must fall back to plain code block");
        assertNotContains(html.value(), "onclick", "unsafe language info must not be emitted");
    }

    public void rendersInlineCodeWithEscapedContent() {
        SafeHtml html = renderer.render("Use `<tag>` here");

        assertContains(html.value(), "Use <code>&lt;tag&gt;</code> here", "inline code content must be escaped");
    }

    public void rendersHttpsMarkdownLinkAsSafeAnchor() {
        SafeHtml html = renderer.render("Read [docs](https://example.com/docs?a=1&b=2)");

        assertContains(html.value(), "Read <a href=\"https://example.com/docs?a=1&amp;b=2\">docs</a>", "HTTPS Markdown link must render as safe anchor with escaped URL");
    }

    public void rendersHttpMarkdownLinkAsSafeAnchor() {
        SafeHtml html = renderer.render("Open [site](http://example.com)");

        assertContains(html.value(), "Open <a href=\"http://example.com\">site</a>", "HTTP Markdown link must render as safe anchor");
    }

    public void doesNotRenderJavascriptMarkdownLinkAsAnchor() {
        SafeHtml html = renderer.render("Bad [link](javascript:alert(1))");

        assertContains(html.value(), "Bad link", "unsafe Markdown link must keep readable text");
        assertNotContains(html.value(), "<a href=\"javascript:", "unsafe Markdown link must not render as anchor");
    }

    public void rendersBulletListItemsAsUnorderedList() {
        SafeHtml html = renderer.render("- first\n- second");

        assertContains(html.value(), "<ul><li>first</li><li>second</li></ul>", "bullet list items must render as unordered list");
    }

    public void rendersUncheckedChecklistItemAsDisabledCheckboxInList() {
        SafeHtml html = renderer.render("- [ ] Write tests");

        assertContains(html.value(), "<ul class=\"checklist\"><li><input type=\"checkbox\" disabled> Write tests</li></ul>", "unchecked checklist item must render as disabled unchecked checkbox");
    }

    public void rendersCheckedChecklistItemAsDisabledCheckedCheckboxInList() {
        SafeHtml html = renderer.render("- [x] Build APK");

        assertContains(html.value(), "<ul class=\"checklist\"><li><input type=\"checkbox\" checked disabled> Build APK</li></ul>", "checked checklist item must render as disabled checked checkbox");
    }

    public void rendersNumberedListItemsAsOrderedList() {
        SafeHtml html = renderer.render("1. first\n2. second");

        assertContains(html.value(), "<ol><li>first</li><li>second</li></ol>", "numbered list items must render as ordered list");
    }

    public void rendersPipeTableWithHeaderAndBodyRows() {
        SafeHtml html = renderer.render("| Name | Status |\n| --- | --- |\n| Tests | Passing |\n| APK | Built |");

        assertContains(html.value(), "<div class=\"table-scroll\"><table><thead><tr><th>Name</th><th>Status</th></tr></thead><tbody><tr><td>Tests</td><td>Passing</td></tr><tr><td>APK</td><td>Built</td></tr></tbody></table></div>", "pipe table must render inside a visible horizontal scroll container");
    }

    public void escapesPipeTableCellContentAndRendersInlineCode() {
        SafeHtml html = renderer.render("| Name | Value |\n| --- | --- |\n| Tag | `<b>` & text |");

        assertContains(html.value(), "<td><code>&lt;b&gt;</code> &amp; text</td>", "table cell content must be escaped and inline code must render safely");
        assertNotContains(html.value(), "<b>", "raw HTML from table cell must not be emitted");
    }

    public void rendersBlockquoteAsBlockquote() {
        SafeHtml html = renderer.render("> quoted <text>");

        assertContains(html.value(), "<blockquote>quoted &lt;text&gt;</blockquote>", "blockquote content must render escaped inside blockquote");
    }

    public void rendersHorizontalRule() {
        SafeHtml html = renderer.render("---");

        assertContains(html.value(), "<hr>", "horizontal rule must render as hr");
    }

    public void rendererDoesNotCrashOnEmptyInput() {
        SafeHtml html = renderer.render("");

        assertEquals("", html.value(), "empty Markdown input must render as an empty safe HTML fragment");
    }

    public void rendererDoesNotCrashOnNullInput() {
        SafeHtml html = renderer.render(null);

        assertEquals("", html.value(), "null Markdown input must be handled as empty input");
    }

    public void rendererClosesUnterminatedCodeFenceInsteadOfCrashing() {
        SafeHtml html = renderer.render("```text\nvalue < 3");

        assertContains(html.value(), "<pre><code", "unterminated fenced code block must still open a code block");
        assertContains(html.value(), "value &lt; 3", "unterminated fenced code block content must be escaped");
        assertContains(html.value(), "</code></pre>", "unterminated fenced code block must be closed at end of document");
    }

    public void rendererDoesNotCrashOnGeneratedMixedMarkdownText() {
        String[] fragments = new String[] {
                "#", "## heading", "- item", "- [x] done", "1. ordered",
                "| a | b |", "| --- | --- |", "`code", "[label](https://example.com)",
                "[bad](javascript:alert(1))", "<script>", "&", "```", "> quote", "---", "\n"
        };

        StringBuilder markdown = new StringBuilder();
        for (int i = 0; i < 128; i++) {
            markdown.append(fragments[(i * 37 + 11) % fragments.length]);
            if (i % 3 == 0) {
                markdown.append(' ');
            } else {
                markdown.append('\n');
            }
        }

        SafeHtml html = renderer.render(markdown.toString());

        assertNotNull(html.value(), "generated mixed Markdown must always produce a safe HTML fragment");
        assertNotContains(html.value(), "<script>", "generated mixed Markdown must not emit raw script tags");
    }

    private static void assertContains(String actual, String expected, String message) {
        if (!actual.contains(expected)) {
            throw new AssertionError(message + "\nExpected to contain: " + expected + "\nActual: " + actual);
        }
    }

    private static void assertNotContains(String actual, String forbidden, String message) {
        if (actual.contains(forbidden)) {
            throw new AssertionError(message + "\nForbidden content: " + forbidden + "\nActual: " + actual);
        }
    }

    private static void assertEquals(String expected, String actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + "\nExpected: " + expected + "\nActual: " + actual);
        }
    }

    private static void assertNotNull(String actual, String message) {
        if (actual == null) {
            throw new AssertionError(message);
        }
    }
}
