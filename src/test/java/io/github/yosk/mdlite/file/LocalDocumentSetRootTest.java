package io.github.yosk.mdlite.file;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class LocalDocumentSetRootTest {
    @Test
    void fileDocumentAllowsNavigationWithinItsParentDocumentSet() {
        LocalDocumentSetRoot root = LocalDocumentSetRoot.fromDocumentUri(
                "file:///storage/emulated/0/Download/project/docs/readme.md");

        TestAssertions.assertEquals("/storage/emulated/0/Download/project", root.path(),
                "relative resources may resolve within the document set containing the file folder");
    }

    @Test
    void contentDocumentDoesNotGrantAFileSystemRoot() {
        LocalDocumentSetRoot root = LocalDocumentSetRoot.fromDocumentUri(
                "content://provider/document/readme.md");

        TestAssertions.assertEquals("", root.path(),
                "non-file documents must not grant an unrelated filesystem boundary");
    }

    @Test
    void malformedDocumentUriDoesNotGrantAFileSystemRoot() {
        LocalDocumentSetRoot root = LocalDocumentSetRoot.fromDocumentUri("not a uri");

        TestAssertions.assertEquals("", root.path(),
                "malformed persistence data must fail closed without an exception");
    }
}
