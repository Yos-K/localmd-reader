package io.github.yosk.mdlite.file;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class FileTypeDetectorTest {

    @Test
    void acceptsMdExtension() {
        TestAssertions.assertTrue(FileTypeDetector.isMarkdownDisplayName("note.md"), ".md files must be accepted");
    }

    @Test
    void acceptsMarkdownExtension() {
        TestAssertions.assertTrue(FileTypeDetector.isMarkdownDisplayName("note.markdown"), ".markdown files must be accepted");
    }

    @Test
    void acceptsUppercaseMdExtension() {
        TestAssertions.assertTrue(FileTypeDetector.isMarkdownDisplayName("README.MD"), "uppercase .MD files must be accepted");
    }

    @Test
    void rejectsUnknownExtension() {
        TestAssertions.assertFalse(FileTypeDetector.isMarkdownDisplayName("note.txt"), "unknown extensions must be rejected");
    }

    @Test
    void rejectsEmptyDisplayName() {
        TestAssertions.assertFalse(FileTypeDetector.isMarkdownDisplayName(""), "empty display name must be rejected");
    }

    @Test
    void rejectsMissingExtension() {
        TestAssertions.assertFalse(FileTypeDetector.isMarkdownDisplayName("README"), "display names without extension must be rejected");
    }
}
