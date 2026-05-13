package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;

public final class StyledMarkdownTextTest {
    public static void main(String[] args) {
        plainTextKeepsReadableMarkdownText();
        boldTextCreatesMarkdownBoldTag();
        italicTextCreatesMarkdownItalicTag();
        underlineTextCreatesHtmlUnderlineTag();
        linkTextCreatesMarkdownLinkTag();
        headingTextCreatesMarkdownHeadingTag();
        markdownSpecialCharactersAreEscapedBeforeApplyingStyle();
    }

    private static void plainTextKeepsReadableMarkdownText() {
        String markdown = new StyledMarkdownText()
                .append("Hello", MarkdownStyle.plain())
                .value();

        TestAssertions.assertEquals("Hello", markdown, "plain copied text must stay readable as Markdown");
    }

    private static void boldTextCreatesMarkdownBoldTag() {
        String markdown = new StyledMarkdownText()
                .append("Important", MarkdownStyle.plain().withBold())
                .value();

        TestAssertions.assertEquals("**Important**", markdown, "bold copied text must become Markdown bold");
    }

    private static void italicTextCreatesMarkdownItalicTag() {
        String markdown = new StyledMarkdownText()
                .append("Emphasis", MarkdownStyle.plain().withItalic())
                .value();

        TestAssertions.assertEquals("_Emphasis_", markdown, "italic copied text must become Markdown italic");
    }

    private static void underlineTextCreatesHtmlUnderlineTag() {
        String markdown = new StyledMarkdownText()
                .append("Underlined", MarkdownStyle.plain().withUnderline())
                .value();

        TestAssertions.assertEquals("<u>Underlined</u>", markdown, "underlined copied text must stay visible in Markdown rendering");
    }

    private static void linkTextCreatesMarkdownLinkTag() {
        String markdown = new StyledMarkdownText()
                .append("OpenAI", MarkdownStyle.plain().withLink("https://openai.com"))
                .value();

        TestAssertions.assertEquals("[OpenAI](https://openai.com)", markdown, "linked copied text must become a Markdown link");
    }

    private static void headingTextCreatesMarkdownHeadingTag() {
        String markdown = new StyledMarkdownText()
                .append("Section", MarkdownStyle.plain().withHeadingLevel(2))
                .value();

        TestAssertions.assertEquals("## Section\n\n", markdown, "large copied text must become a Markdown heading");
    }

    private static void markdownSpecialCharactersAreEscapedBeforeApplyingStyle() {
        String markdown = new StyledMarkdownText()
                .append("a_b*", MarkdownStyle.plain().withBold())
                .value();

        TestAssertions.assertEquals("**a\\_b\\***", markdown, "Markdown syntax in copied text must be escaped before adding generated tags");
    }
}
