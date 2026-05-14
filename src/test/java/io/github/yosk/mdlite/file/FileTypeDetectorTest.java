package io.github.yosk.mdlite.file;

import io.github.yosk.mdlite.testing.TestAssertions;

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
        TestAssertions.assertTrue(FileTypeDetector.isMarkdownDisplayName("note.md"), ".md files must be accepted");
    }

    public void acceptsMarkdownExtension() {
        TestAssertions.assertTrue(FileTypeDetector.isMarkdownDisplayName("note.markdown"), ".markdown files must be accepted");
    }

    public void acceptsUppercaseMdExtension() {
        TestAssertions.assertTrue(FileTypeDetector.isMarkdownDisplayName("README.MD"), "uppercase .MD files must be accepted");
    }

    public void rejectsUnknownExtension() {
        TestAssertions.assertFalse(FileTypeDetector.isMarkdownDisplayName("note.txt"), "unknown extensions must be rejected");
    }

    public void rejectsEmptyDisplayName() {
        TestAssertions.assertFalse(FileTypeDetector.isMarkdownDisplayName(""), "empty display name must be rejected");
    }

    public void rejectsMissingExtension() {
        TestAssertions.assertFalse(FileTypeDetector.isMarkdownDisplayName("README"), "display names without extension must be rejected");
    }
}
