package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;

public final class MarkdownFileOpenResultTest {
    private static final long MAX_SIZE_BYTES = 10L * 1024L * 1024L;

    public static void main(String[] args) {
        MarkdownFileOpenResultTest test = new MarkdownFileOpenResultTest();
        test.fromReturnsReadableMarkdownFileWhenDisplayNameAndSizeAreAccepted();
        test.fromReturnsUnsupportedFileWhenDisplayNameIsNotMarkdown();
        test.fromReturnsOversizedFileWhenMarkdownFileExceedsPolicy();
        test.readableMarkdownFileCannotBeCreatedWithUnsupportedDisplayName();
        test.readableMarkdownFileCannotBeCreatedWithInvalidSize();
    }

    public void fromReturnsReadableMarkdownFileWhenDisplayNameAndSizeAreAccepted() {
        MarkdownFileOpenResult result = MarkdownFileOpenResult.from(
                " README.MD ",
                MAX_SIZE_BYTES,
                new FileSizePolicy(MAX_SIZE_BYTES));

        TestAssertions.assertTrue(result instanceof MarkdownFileOpenResult.ReadableMarkdownFile, "accepted Markdown input must become a readable Markdown file");
        MarkdownFileOpenResult.ReadableMarkdownFile file = (MarkdownFileOpenResult.ReadableMarkdownFile) result;
        TestAssertions.assertEquals("README.MD", file.displayName(), "readable Markdown file must keep a trimmed display name");
        TestAssertions.assertEquals(MAX_SIZE_BYTES, file.sizeBytes(), "readable Markdown file must keep the accepted size");
    }

    public void fromReturnsUnsupportedFileWhenDisplayNameIsNotMarkdown() {
        MarkdownFileOpenResult result = MarkdownFileOpenResult.from(
                "note.txt",
                10,
                new FileSizePolicy(MAX_SIZE_BYTES));

        TestAssertions.assertTrue(result instanceof MarkdownFileOpenResult.UnsupportedMarkdownFile, "unsupported file input must be explicit before rendering");
    }

    public void fromReturnsOversizedFileWhenMarkdownFileExceedsPolicy() {
        MarkdownFileOpenResult result = MarkdownFileOpenResult.from(
                "note.md",
                MAX_SIZE_BYTES + 1,
                new FileSizePolicy(MAX_SIZE_BYTES));

        TestAssertions.assertTrue(result instanceof MarkdownFileOpenResult.OversizedMarkdownFile, "oversized Markdown input must be explicit before reading content");
    }

    public void readableMarkdownFileCannotBeCreatedWithUnsupportedDisplayName() {
        TestAssertions.assertThrows(IllegalArgumentException.class, new TestAssertions.ThrowingRunnable() {
            @Override
            public void run() {
                MarkdownFileOpenResult.ReadableMarkdownFile.of("note.txt", 10);
            }
        });
    }

    public void readableMarkdownFileCannotBeCreatedWithInvalidSize() {
        TestAssertions.assertThrows(IllegalArgumentException.class, new TestAssertions.ThrowingRunnable() {
            @Override
            public void run() {
                MarkdownFileOpenResult.ReadableMarkdownFile.of("note.md", -2);
            }
        });
    }
}
