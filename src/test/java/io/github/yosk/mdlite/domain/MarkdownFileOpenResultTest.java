package io.github.yosk.mdlite.domain;

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

        assertTrue(result instanceof MarkdownFileOpenResult.ReadableMarkdownFile, "accepted Markdown input must become a readable Markdown file");
        MarkdownFileOpenResult.ReadableMarkdownFile file = (MarkdownFileOpenResult.ReadableMarkdownFile) result;
        assertEquals("README.MD", file.displayName(), "readable Markdown file must keep a trimmed display name");
        assertEquals(MAX_SIZE_BYTES, file.sizeBytes(), "readable Markdown file must keep the accepted size");
    }

    public void fromReturnsUnsupportedFileWhenDisplayNameIsNotMarkdown() {
        MarkdownFileOpenResult result = MarkdownFileOpenResult.from(
                "note.txt",
                10,
                new FileSizePolicy(MAX_SIZE_BYTES));

        assertTrue(result instanceof MarkdownFileOpenResult.UnsupportedMarkdownFile, "unsupported file input must be explicit before rendering");
    }

    public void fromReturnsOversizedFileWhenMarkdownFileExceedsPolicy() {
        MarkdownFileOpenResult result = MarkdownFileOpenResult.from(
                "note.md",
                MAX_SIZE_BYTES + 1,
                new FileSizePolicy(MAX_SIZE_BYTES));

        assertTrue(result instanceof MarkdownFileOpenResult.OversizedMarkdownFile, "oversized Markdown input must be explicit before reading content");
    }

    public void readableMarkdownFileCannotBeCreatedWithUnsupportedDisplayName() {
        assertThrows(new ThrowingRunnable() {
            @Override
            public void run() {
                MarkdownFileOpenResult.ReadableMarkdownFile.of("note.txt", 10);
            }
        }, "readable Markdown file must not exist with unsupported display name");
    }

    public void readableMarkdownFileCannotBeCreatedWithInvalidSize() {
        assertThrows(new ThrowingRunnable() {
            @Override
            public void run() {
                MarkdownFileOpenResult.ReadableMarkdownFile.of("note.md", -2);
            }
        }, "readable Markdown file must not exist with invalid size");
    }

    private static void assertTrue(boolean actual, String message) {
        if (!actual) {
            throw new AssertionError(message);
        }
    }

    private static void assertEquals(String expected, String actual, String message) {
        if (!expected.equals(actual)) {
            throw new AssertionError(message + "\nExpected: " + expected + "\nActual: " + actual);
        }
    }

    private static void assertEquals(long expected, long actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + "\nExpected: " + expected + "\nActual: " + actual);
        }
    }

    private static void assertThrows(ThrowingRunnable runnable, String message) {
        try {
            runnable.run();
            throw new AssertionError(message + "\nExpected IllegalArgumentException");
        } catch (IllegalArgumentException expected) {
            return;
        }
    }

    private interface ThrowingRunnable {
        void run();
    }
}
