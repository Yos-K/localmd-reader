package io.github.yosk.mdlite.file;

import io.github.yosk.mdlite.testing.TestAssertions;
import org.junit.jupiter.api.Test;

public final class MarkdownLibraryQueryTest {
    @Test
    void blankInputCreatesAQueryThatKeepsEveryLibraryItem() {
        MarkdownLibraryQuery query = MarkdownLibraryQuery.from("   ");

        TestAssertions.assertTrue(query.matches(
                        MarkdownLibraryItem.document("README.md", "content://tree/readme")),
                "blank library query must keep the complete current directory visible");
    }

    @Test
    void readableInputMatchesNamesWithoutCaseOrOuterWhitespace() {
        MarkdownLibraryQuery query = MarkdownLibraryQuery.from("  guide  ");

        TestAssertions.assertTrue(query.matches(
                        MarkdownLibraryItem.document("User-GUIDE.md", "content://tree/guide")),
                "library query must normalize user input before matching display names");
    }

    @Test
    void readableInputRejectsAnUnrelatedName() {
        MarkdownLibraryQuery query = MarkdownLibraryQuery.from("guide");

        TestAssertions.assertFalse(query.matches(
                        MarkdownLibraryItem.document("README.md", "content://tree/readme")),
                "active library query must omit unrelated names");
    }
}
