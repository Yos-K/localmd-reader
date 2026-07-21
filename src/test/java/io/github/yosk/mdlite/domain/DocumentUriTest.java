package io.github.yosk.mdlite.domain;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class DocumentUriTest {
    @Test
    void validUriPreservesItsCanonicalText() {
        DocumentUri uri = DocumentUri.from("content://guide");

        TestAssertions.assertEquals("content://guide", uri.value(),
                "document URI must preserve its canonical boundary text");
    }

    @Test
    void surroundingWhitespaceIsRemovedAtTheBoundary() {
        DocumentUri uri = DocumentUri.from("  content://guide  ");

        TestAssertions.assertEquals("content://guide", uri.value(),
                "document URI must normalize boundary whitespace once");
    }

    @Test
    void equalUriValuesHaveValueEquality() {
        DocumentUri first = DocumentUri.from("content://guide");
        DocumentUri second = DocumentUri.from("content://guide");

        TestAssertions.assertTrue(first.equals(second),
                "document URI identity must use its normalized value");
    }

    @Test
    void missingUriIsRejectedBeforeCreatingADocumentIdentity() {
        TestAssertions.assertThrows(
                IllegalArgumentException.class,
                new TestAssertions.ThrowingRunnable() {
                    @Override public void run() { DocumentUri.from(null); }
                });
    }

    @Test
    void blankUriIsRejectedBeforeCreatingADocumentIdentity() {
        TestAssertions.assertThrows(
                IllegalArgumentException.class,
                new TestAssertions.ThrowingRunnable() {
                    @Override public void run() { DocumentUri.from("   "); }
                });
    }
}
