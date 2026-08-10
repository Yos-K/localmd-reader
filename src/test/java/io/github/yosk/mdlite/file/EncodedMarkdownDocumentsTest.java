package io.github.yosk.mdlite.file;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public final class EncodedMarkdownDocumentsTest {
    @Nested
    final class WhenParsingTheTermuxTransportPayload {
        @Test
        void validRecordsPreserveArgumentOrderAndEncodedFields() {
            EncodedMarkdownDocuments documents = EncodedMarkdownDocuments.parse(
                    "dGl0bGUx:c291cmNlMQ==:dGV4dDE=,dGl0bGUy:c291cmNlMg==:dGV4dDI=");

            assertEquals(2, documents.size());
            assertEquals("dGl0bGUx", documents.get(0).encodedTitle());
            assertEquals("dGV4dDI=", documents.get(1).encodedText());
        }

        @Test
        void malformedRecordsAreExcludedWithoutInvalidatingValidDocuments() {
            EncodedMarkdownDocuments documents = EncodedMarkdownDocuments.parse(
                    "missing-fields,dGl0bGU=:c291cmNl:dGV4dA==,too:many:fields:here");

            assertEquals(1, documents.size());
            assertEquals("c291cmNl", documents.get(0).encodedSource());
        }

        @Test
        void invalidBase64FieldsAreExcludedAtTheExternalBoundary() {
            EncodedMarkdownDocuments documents = EncodedMarkdownDocuments.parse(
                    "not_base64:c291cmNl:dGV4dA==,dGl0bGU=:c291cmNl:dGV4dA==");

            assertEquals(1, documents.size());
            assertEquals("dGl0bGU=", documents.get(0).encodedTitle());
        }

        @Test
        void absentPayloadProducesAnEmptyAlwaysValidCollection() {
            assertEquals(0, EncodedMarkdownDocuments.parse(null).size());
        }
    }
}
