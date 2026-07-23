package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class StyledMarkdownTextTest {
    @Test
    void plainClipboardMarkdownKeepsExistingSyntaxAvailableToTheRenderer() {
        StyledMarkdownText text = new StyledMarkdownText()
                .append("This is **important**.", MarkdownStyle.plain());

        TestAssertions.assertEquals("This is **important**.", text.value(),
                "plain clipboard text must preserve intentional Markdown syntax");
    }

    @Test
    void plainTextKeepsReadableMarkdownText() {
        String markdown = new StyledMarkdownText()
                .append("Hello", MarkdownStyle.plain())
                .value();

        TestAssertions.assertEquals("Hello", markdown, "plain copied text must stay readable as Markdown");
    }

    @Test
    void boldTextCreatesMarkdownBoldTag() {
        String markdown = new StyledMarkdownText()
                .append("Important", MarkdownStyle.plain().withBold())
                .value();

        TestAssertions.assertEquals("**Important**", markdown, "bold copied text must become Markdown bold");
    }

    @Test
    void italicTextCreatesMarkdownItalicTag() {
        String markdown = new StyledMarkdownText()
                .append("Emphasis", MarkdownStyle.plain().withItalic())
                .value();

        TestAssertions.assertEquals("_Emphasis_", markdown, "italic copied text must become Markdown italic");
    }

    @Test
    void underlineTextCreatesHtmlUnderlineTag() {
        String markdown = new StyledMarkdownText()
                .append("Underlined", MarkdownStyle.plain().withUnderline())
                .value();

        TestAssertions.assertEquals("<u>Underlined</u>", markdown, "underlined copied text must stay visible in Markdown rendering");
    }

    @Test
    void linkTextCreatesMarkdownLinkTag() {
        String markdown = new StyledMarkdownText()
                .append("OpenAI", MarkdownStyle.plain().withLink("https://openai.com"))
                .value();

        TestAssertions.assertEquals("[OpenAI](https://openai.com)", markdown, "linked copied text must become a Markdown link");
    }

    @Test
    void headingTextCreatesMarkdownHeadingTag() {
        String markdown = new StyledMarkdownText()
                .append("Section", MarkdownStyle.plain().withHeadingLevel(2))
                .value();

        TestAssertions.assertEquals("## Section\n\n", markdown, "large copied text must become a Markdown heading");
    }

    @Test
    void bulletTextCreatesMarkdownListItem() {
        String markdown = new StyledMarkdownText()
                .append("First item\n", MarkdownStyle.plain().withBulletListItem())
                .append("Second item", MarkdownStyle.plain().withBulletListItem())
                .value();

        TestAssertions.assertEquals("- First item\n- Second item\n", markdown, "bulleted copied text must become Markdown list items");
    }

    @Test
    void tabSeparatedLinesCreateMarkdownTable() {
        String markdown = new StyledMarkdownText()
                .append("Name\tValue\nAlpha\t1\nBeta\t2", MarkdownStyle.plain())
                .value();

        TestAssertions.assertEquals("| Name | Value |\n| --- | --- |\n| Alpha | 1 |\n| Beta | 2 |", markdown, "tab-separated copied text must become a Markdown table");
    }

    @Test
    void markdownSpecialCharactersAreEscapedBeforeApplyingStyle() {
        String markdown = new StyledMarkdownText()
                .append("a_b*", MarkdownStyle.plain().withBold())
                .value();

        TestAssertions.assertEquals("**a\\_b\\***", markdown, "Markdown syntax in copied text must be escaped before adding generated tags");
    }
}
