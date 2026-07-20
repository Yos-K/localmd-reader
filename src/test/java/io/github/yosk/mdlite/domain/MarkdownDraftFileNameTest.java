package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class MarkdownDraftFileNameTest {

    @Test
    void blankTitleCreatesDefaultMarkdownFileName() {
        MarkdownDraftFileName fileName = MarkdownDraftFileName.fromTitle("", 1);

        TestAssertions.assertEquals("created-markdown.md", fileName.value(), "blank title must create a default Markdown file name");
    }

    @Test
    void titleWithSpacesCreatesSafeMarkdownFileName() {
        MarkdownDraftFileName fileName = MarkdownDraftFileName.fromTitle("DeepSeek note", 1);

        TestAssertions.assertEquals("DeepSeek-note.md", fileName.value(), "title spaces must be converted into a safe Markdown file name");
    }

    @Test
    void secondFileAddsSequenceBeforeMarkdownExtension() {
        MarkdownDraftFileName fileName = MarkdownDraftFileName.fromTitle("DeepSeek note", 2);

        TestAssertions.assertEquals("DeepSeek-note-2.md", fileName.value(), "duplicate draft names must add a sequence before the Markdown extension");
    }
}
