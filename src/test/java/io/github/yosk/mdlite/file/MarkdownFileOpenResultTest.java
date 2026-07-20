package io.github.yosk.mdlite.file;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class MarkdownFileOpenResultTest {
    private static final long MAX_SIZE_BYTES = 10L * 1024L * 1024L;


    @Test
    void fromReturnsReadableMarkdownFileWhenDisplayNameAndSizeAreAccepted() {
        MarkdownFileOpenResult result = MarkdownFileOpenResult.from(
                " README.MD ",
                MAX_SIZE_BYTES,
                new FileSizePolicy(MAX_SIZE_BYTES));

        TestAssertions.assertTrue(result instanceof MarkdownFileOpenResult.ReadableMarkdownFile, "accepted Markdown input must become a readable Markdown file");
        MarkdownFileOpenResult.ReadableMarkdownFile file = (MarkdownFileOpenResult.ReadableMarkdownFile) result;
        TestAssertions.assertEquals("README.MD", file.displayName(), "readable Markdown file must keep a trimmed display name");
        TestAssertions.assertEquals(MAX_SIZE_BYTES, file.sizeBytes(), "readable Markdown file must keep the accepted size");
    }

    @Test
    void fromReturnsUnsupportedFileWhenDisplayNameIsNotMarkdown() {
        MarkdownFileOpenResult result = MarkdownFileOpenResult.from(
                "note.txt",
                10,
                new FileSizePolicy(MAX_SIZE_BYTES));

        TestAssertions.assertTrue(result instanceof MarkdownFileOpenResult.UnsupportedMarkdownFile, "unsupported file input must be explicit before rendering");
    }

    @Test
    void fromReturnsOversizedFileWhenMarkdownFileExceedsPolicy() {
        MarkdownFileOpenResult result = MarkdownFileOpenResult.from(
                "note.md",
                MAX_SIZE_BYTES + 1,
                new FileSizePolicy(MAX_SIZE_BYTES));

        TestAssertions.assertTrue(result instanceof MarkdownFileOpenResult.OversizedMarkdownFile, "oversized Markdown input must be explicit before reading content");
    }

    @Test
    void readableMarkdownFileCannotBeCreatedWithUnsupportedDisplayName() {
        TestAssertions.assertThrows(IllegalArgumentException.class, new TestAssertions.ThrowingRunnable() {
            @Override
            public void run() {
                MarkdownFileOpenResult.ReadableMarkdownFile.of("note.txt", 10);
            }
        });
    }

    @Test
    void readableMarkdownFileCannotBeCreatedWithInvalidSize() {
        TestAssertions.assertThrows(IllegalArgumentException.class, new TestAssertions.ThrowingRunnable() {
            @Override
            public void run() {
                MarkdownFileOpenResult.ReadableMarkdownFile.of("note.md", -2);
            }
        });
    }
}
