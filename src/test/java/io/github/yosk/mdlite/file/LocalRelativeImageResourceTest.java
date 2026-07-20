package io.github.yosk.mdlite.file;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class LocalRelativeImageResourceTest {

    @Test
    void fileMarkdownDocumentResolvesSafeRelativeImageRequestInsideTheSameDirectory() {
        LocalRelativeImageResource resource = LocalRelativeImageResource.resolve(
                "file:///sdcard/docs/guide/readme.md",
                "https://localmd.local/images/icon.png");

        TestAssertions.assertTrue(resource.isAvailable(), "safe relative image request must resolve for file Markdown documents");
        TestAssertions.assertEquals("/sdcard/docs/guide/images/icon.png", resource.filePath(), "resolved image path");
        TestAssertions.assertEquals("image/png", resource.mimeType(), "png image MIME type");
    }

    @Test
    void fileMarkdownDocumentDecodesEscapedSpacesInRelativeImageRequest() {
        LocalRelativeImageResource resource = LocalRelativeImageResource.resolve(
                "file:///sdcard/docs/readme.md",
                "https://localmd.local/images/local%20diagram.jpg");

        TestAssertions.assertTrue(resource.isAvailable(), "escaped spaces must be decoded before resolving the file path");
        TestAssertions.assertEquals("/sdcard/docs/images/local diagram.jpg", resource.filePath(), "decoded image path");
        TestAssertions.assertEquals("image/jpeg", resource.mimeType(), "jpeg image MIME type");
    }

    @Test
    void nonFileMarkdownDocumentCannotResolveRelativeImageFiles() {
        LocalRelativeImageResource resource = LocalRelativeImageResource.resolve(
                "content://provider/docs/readme.md",
                "https://localmd.local/images/icon.png");

        TestAssertions.assertFalse(resource.isAvailable(), "content URI documents must not guess sibling image files without directory permission");
    }

    @Test
    void requestOutsideTheLocalMdHostCannotResolveRelativeImageFiles() {
        LocalRelativeImageResource resource = LocalRelativeImageResource.resolve(
                "file:///sdcard/docs/readme.md",
                "https://example.com/images/icon.png");

        TestAssertions.assertFalse(resource.isAvailable(), "non-localmd hosts must not resolve as local image files");
    }

    @Test
    void traversalRequestCannotEscapeTheMarkdownDocumentDirectory() {
        LocalRelativeImageResource resource = LocalRelativeImageResource.resolve(
                "file:///sdcard/docs/readme.md",
                "https://localmd.local/../secret.png");

        TestAssertions.assertFalse(resource.isAvailable(), "relative image requests must not escape the Markdown document directory");
    }

    @Test
    void parentDirectoryImageRequestResolvesWhenItStaysInsideTheAllowedRoot() {
        LocalRelativeImageResource resource = LocalRelativeImageResource.resolve(
                "file:///sdcard/docs/guide/readme.md",
                "https://localmd.local/__relative_image__?path=..%2Fimages%2Ficon.png",
                "/sdcard/docs");

        TestAssertions.assertTrue(resource.isAvailable(), "parent-directory image request must resolve when it stays inside the allowed document set root");
        TestAssertions.assertEquals("/sdcard/docs/images/icon.png", resource.filePath(), "parent-directory image path");
        TestAssertions.assertEquals("image/png", resource.mimeType(), "png image MIME type");
    }

    @Test
    void parentDirectoryImageRequestCannotEscapeTheAllowedRoot() {
        LocalRelativeImageResource resource = LocalRelativeImageResource.resolve(
                "file:///sdcard/docs/guide/readme.md",
                "https://localmd.local/__relative_image__?path=..%2F..%2Fsecret.png",
                "/sdcard/docs");

        TestAssertions.assertFalse(resource.isAvailable(), "parent-directory image request must not escape the allowed document set root");
    }

    @Test
    void doubleEncodedTraversalCannotEscapeTheAllowedRoot() {
        LocalRelativeImageResource resource = LocalRelativeImageResource.resolve(
                "file:///sdcard/docs/guide/readme.md",
                "https://localmd.local/__relative_image__?path=%252e%252e%2Fsecret.png",
                "/sdcard/docs");

        TestAssertions.assertFalse(resource.isAvailable(), "double-encoded traversal must not be decoded twice into an escaped path");
    }

    @Test
    void unsupportedImageExtensionCannotResolveRelativeImageFiles() {
        LocalRelativeImageResource resource = LocalRelativeImageResource.resolve(
                "file:///sdcard/docs/readme.md",
                "https://localmd.local/images/readme.txt");

        TestAssertions.assertFalse(resource.isAvailable(), "unsupported image extensions must stay unavailable");
    }
}
