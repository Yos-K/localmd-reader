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
        test.rendersInlineCodeWithEscapedContent();
        test.rendersBulletListItemsAsUnorderedList();
        test.rendersUncheckedChecklistItemAsDisabledCheckboxInList();
        test.rendersCheckedChecklistItemAsDisabledCheckedCheckboxInList();
        test.rendersNumberedListItemsAsOrderedList();
        test.rendersPipeTableWithHeaderAndBodyRows();
        test.escapesPipeTableCellContentAndRendersInlineCode();
        test.rendersBlockquoteAsBlockquote();
        test.rendersHorizontalRule();
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

        assertContains(html.value(), "<pre><code>", "fenced code block with language info must render as pre/code");
        assertContains(html.value(), "data MarkdownFile = ReadableMarkdownFile OR UnsupportedFile", "fenced code block content must be preserved");
        assertNotContains(html.value(), "<p>```text", "language fence marker must not render as paragraph");
    }

    public void rendersInlineCodeWithEscapedContent() {
        SafeHtml html = renderer.render("Use `<tag>` here");

        assertContains(html.value(), "Use <code>&lt;tag&gt;</code> here", "inline code content must be escaped");
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

        assertContains(html.value(), "<table><thead><tr><th>Name</th><th>Status</th></tr></thead><tbody><tr><td>Tests</td><td>Passing</td></tr><tr><td>APK</td><td>Built</td></tr></tbody></table>", "pipe table must render header and body rows");
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
}
