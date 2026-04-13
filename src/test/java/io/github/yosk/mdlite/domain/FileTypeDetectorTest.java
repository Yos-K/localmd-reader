package io.github.yosk.mdlite.domain;

public final class FileTypeDetectorTest {
    public static void main(String[] args) {
        FileTypeDetectorTest test = new FileTypeDetectorTest();
        test.acceptsMdExtension();
        test.acceptsMarkdownExtension();
        test.acceptsUppercaseMdExtension();
        test.rejectsUnknownExtension();
        test.rejectsEmptyDisplayName();
        test.rejectsMissingExtension();
    }

    public void acceptsMdExtension() {
        assertTrue(FileTypeDetector.isMarkdownDisplayName("note.md"), ".md files must be accepted");
    }

    public void acceptsMarkdownExtension() {
        assertTrue(FileTypeDetector.isMarkdownDisplayName("note.markdown"), ".markdown files must be accepted");
    }

    public void acceptsUppercaseMdExtension() {
        assertTrue(FileTypeDetector.isMarkdownDisplayName("README.MD"), "uppercase .MD files must be accepted");
    }

    public void rejectsUnknownExtension() {
        assertFalse(FileTypeDetector.isMarkdownDisplayName("note.txt"), "unknown extensions must be rejected");
    }

    public void rejectsEmptyDisplayName() {
        assertFalse(FileTypeDetector.isMarkdownDisplayName(""), "empty display name must be rejected");
    }

    public void rejectsMissingExtension() {
        assertFalse(FileTypeDetector.isMarkdownDisplayName("README"), "display names without extension must be rejected");
    }

    private static void assertTrue(boolean actual, String message) {
        if (!actual) {
            throw new AssertionError(message);
        }
    }

    private static void assertFalse(boolean actual, String message) {
        if (actual) {
            throw new AssertionError(message);
        }
    }
}
